from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api.chat import (
    router as chat_router,
)
from app.api.conversations import (
    router as conversations_router,
)
from app.api.providers import (
    router as providers_router,
)
from app.config import settings
from app.db.bootstrap import (
    initialize_database,
)
from app.models import HealthResponse


@asynccontextmanager
async def lifespan(app: FastAPI):

    await initialize_database()

    yield


app = FastAPI(
    title=settings.app_name,
    version="0.3.0",
    description=(
        "Multi-provider AI backend "
        "for Kylow."
    ),
    lifespan=lifespan,
)


@app.get(
    "/health",
    response_model=HealthResponse,
)
async def health() -> HealthResponse:

    return HealthResponse(
        status="ok",
        service=settings.app_name,
        environment=settings.environment,
        provider=settings.provider,
        database="ready",
    )


app.include_router(
    chat_router,
    prefix=f"/{settings.api_version}",
    tags=["chat"],
)

app.include_router(
    conversations_router,
    prefix=f"/{settings.api_version}",
    tags=["conversations"],
)

app.include_router(
    providers_router,
    prefix=f"/{settings.api_version}",
    tags=["providers"],
)

# ==================================================
# KYLOW_CLOUD_API_AUTH
# ==================================================

from fastapi import Request as _KylowAuthRequest

from app.security import (
    enforce_api_auth as _kylow_enforce_api_auth,
)


@app.middleware("http")
async def _kylow_cloud_api_auth(
    request: _KylowAuthRequest,
    call_next,
):
    denial = await _kylow_enforce_api_auth(
        request
    )

    if denial is not None:
        return denial

    return await call_next(request)
