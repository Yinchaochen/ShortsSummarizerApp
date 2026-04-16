from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from api.routes import summarize, jobs, usage, captions

app = FastAPI(title="Uchia API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Versioned routes — use /api/v1 for all new integrations
app.include_router(summarize.router, prefix="/api/v1")
app.include_router(jobs.router,      prefix="/api/v1")
app.include_router(usage.router,     prefix="/api/v1")
app.include_router(captions.router,  prefix="/api/v1")

# Legacy aliases — keeps existing app installs working
app.include_router(summarize.router, prefix="/api")
app.include_router(jobs.router,      prefix="/api")
app.include_router(usage.router,     prefix="/api")


@app.get("/health")
def health():
    return {"status": "ok"}
