package com.uchia.bubble

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class BubblePackage : ReactPackage {
    override fun createNativeModules(context: ReactApplicationContext) =
        listOf(BubbleModule(context))

    override fun createViewManagers(context: ReactApplicationContext): List<ViewManager<*, *>> =
        emptyList()
}
