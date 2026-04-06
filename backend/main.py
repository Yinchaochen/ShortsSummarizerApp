from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from api.routes import summarize, jobs, usage

app = FastAPI(title="Shorts Summarizer API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Phase 2: 限制为正式域名
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(summarize.router, prefix="/api")
app.include_router(jobs.router, prefix="/api")
app.include_router(usage.router, prefix="/api")

@app.get("/health")
def health():
    return {"status": "ok"}
