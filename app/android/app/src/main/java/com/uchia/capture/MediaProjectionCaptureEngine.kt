package com.uchia.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import kotlinx.coroutines.*

/**
 * [ICaptureEngine] implementation backed by Android's MediaProjection API.
 *
 * Delivers screen frames via [setFrameCallback] and raw PCM audio via
 * [setAudioCallback]. Both streams run on background threads and are
 * independent — the audio callback is no-op if the device / Android version
 * does not support playback capture.
 *
 * Frame rate: one frame every [FRAME_INTERVAL_MS] ms. [IFrameSampler] in the
 * orchestrator decides which frames actually go to OCR.
 */
class MediaProjectionCaptureEngine(private val context: Context) : ICaptureEngine {

    companion object {
        private const val TAG = "CaptureEngine"
        private const val FRAME_INTERVAL_MS = 300L   // ~3 fps capture rate; sampler filters further
        private const val SAMPLE_RATE = 16_000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var audioRecord: AudioRecord? = null

    private var frameCallback: ((Bitmap, Long) -> Unit)? = null
    private var audioCallback: ((PCMChunk) -> Unit)? = null

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var audioJob: Job? = null

    // ─── ICaptureEngine ───────────────────────────────────────────────────────

    override fun setFrameCallback(cb: (frame: Bitmap, timestampMs: Long) -> Unit) {
        frameCallback = cb
    }

    override fun setAudioCallback(cb: (chunk: PCMChunk) -> Unit) {
        audioCallback = cb
    }

    override fun start(config: CaptureConfig) {
        val pm = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = pm.getMediaProjection(config.resultCode, config.resultData)
            ?: run { Log.e(TAG, "getMediaProjection returned null"); return }
        mediaProjection = projection

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealMetrics(metrics)

        val width  = (metrics.widthPixels  * config.scaleFactor).toInt()
        val height = (metrics.heightPixels * config.scaleFactor).toInt()

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        // Android 14+ requires registerCallback BEFORE createVirtualDisplay
        projection.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                Log.d(TAG, "MediaProjection stopped by system")
                stop()
            }
        }, Handler(Looper.getMainLooper()))

        virtualDisplay = projection.createVirtualDisplay(
            "UchiaCaptureEngine",
            width, height, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null, null
        )

        scope.launch { frameLoop(width, height) }
        startAudioCapture(projection)

        Log.d(TAG, "Started: ${width}x${height} @${1000 / FRAME_INTERVAL_MS}fps capture")
    }

    override fun stop() {
        audioJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
        mediaProjection?.stop()
        mediaProjection = null

        Log.d(TAG, "Stopped")
    }

    override fun release() {
        stop()
        scope.cancel()
        frameCallback = null
        audioCallback = null
    }

    // ─── Frame loop ───────────────────────────────────────────────────────────

    private suspend fun frameLoop(width: Int, height: Int) {
        while (scope.isActive) {
            try {
                acquireFrame(width, height)?.let { bmp ->
                    frameCallback?.invoke(bmp, System.currentTimeMillis())
                    // Caller (Orchestrator) is responsible for recycling the bitmap
                    // after all consumers are done with it.
                }
            } catch (e: Exception) {
                Log.e(TAG, "Frame error: ${e.message}")
            }
            delay(FRAME_INTERVAL_MS)
        }
    }

    private fun acquireFrame(width: Int, height: Int): Bitmap? {
        val image = imageReader?.acquireLatestImage() ?: return null
        return try {
            val plane      = image.planes[0]
            val rowPadding = plane.rowStride - plane.pixelStride * width

            val bmp = Bitmap.createBitmap(
                width + rowPadding / plane.pixelStride,
                height,
                Bitmap.Config.ARGB_8888,
            )
            bmp.copyPixelsFromBuffer(plane.buffer)

            if (rowPadding > 0) {
                val cropped = Bitmap.createBitmap(bmp, 0, 0, width, height)
                bmp.recycle()
                cropped
            } else {
                bmp
            }
        } finally {
            image.close()
        }
    }

    // ─── Audio capture ────────────────────────────────────────────────────────

    private fun startAudioCapture(projection: MediaProjection) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Log.d(TAG, "Audio playback capture requires API 29+, skipping")
            return
        }
        if (audioCallback == null) return

        val minBuf = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        val bufSize = minBuf * 4

        val config = AudioPlaybackCaptureConfiguration.Builder(projection)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .addMatchingUsage(AudioAttributes.USAGE_GAME)
            .build()

        val record = AudioRecord.Builder()
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AUDIO_FORMAT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(CHANNEL_CONFIG)
                    .build()
            )
            .setBufferSizeInBytes(bufSize)
            .setAudioPlaybackCaptureConfig(config)
            .build()

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord failed to initialise")
            record.release()
            return
        }

        audioRecord = record
        record.startRecording()

        audioJob = scope.launch(Dispatchers.IO) {
            val buffer = ShortArray(bufSize / 2)
            while (isActive) {
                val read = record.read(buffer, 0, buffer.size)
                if (read > 0) {
                    audioCallback?.invoke(PCMChunk(buffer.copyOf(read), SAMPLE_RATE))
                }
            }
        }

        Log.d(TAG, "Audio capture started at ${SAMPLE_RATE}Hz")
    }
}
