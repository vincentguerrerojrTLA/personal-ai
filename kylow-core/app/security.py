import hmac
import os

from fastapi import Request
from starlette.responses import JSONResponse


def authorize_api_request(
    path: str,
    authorization: str | None,
):
    if not path.startswith("/v1/"):
        return None

    environment = (
        os.getenv(
            "KYLOW_ENVIRONMENT",
            "development",
        )
        .strip()
        .lower()
    )

    token = (
        os.getenv(
            "KYLOW_API_TOKEN",
            "",
        )
        .strip()
    )

    if not token:
        if environment == "production":
            return (
                503,
                "Kylow API authentication is not configured.",
            )

        return None

    authorization = (
        authorization or ""
    ).strip()

    if not authorization.startswith("Bearer "):
        return (
            401,
            "Authentication required.",
        )

    supplied = authorization[7:].strip()

    if not supplied:
        return (
            401,
            "Authentication required.",
        )

    if not hmac.compare_digest(
        supplied,
        token,
    ):
        return (
            401,
            "Invalid authentication token.",
        )

    return None


async def enforce_api_auth(
    request: Request,
):
    result = authorize_api_request(
        request.url.path,
        request.headers.get("authorization"),
    )

    if result is None:
        return None

    status_code, detail = result

    return JSONResponse(
        status_code=status_code,
        content={
            "detail": detail,
        },
    )
