from fastapi import APIRouter

from app.models import ProviderStatus
from app.providers.router import (
    provider_router,
)


router = APIRouter()


@router.get(
    "/providers",
    response_model=list[ProviderStatus],
)
async def providers():

    return provider_router.status()
