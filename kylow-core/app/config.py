from pathlib import Path

from pydantic_settings import (
    BaseSettings,
    SettingsConfigDict,
)


CORE_ROOT = (
    Path(__file__)
    .resolve()
    .parent
    .parent
)

ENV_FILE = CORE_ROOT / ".env"


def normalize_database_url(
    value: str,
) -> str:

    value = value.strip()

    if value.startswith(
        "postgres://"
    ):

        return value.replace(
            "postgres://",
            "postgresql+psycopg://",
            1,
        )

    if value.startswith(
        "postgresql://"
    ):

        return value.replace(
            "postgresql://",
            "postgresql+psycopg://",
            1,
        )

    return value


class Settings(BaseSettings):

    app_name: str = "Kylow Core"

    environment: str = "development"

    provider: str = "auto"

    api_version: str = "v1"

    database_url: str = (
        "sqlite+aiosqlite:///./kylow.db"
    )

    request_timeout_seconds: float = 120.0


    # ----------------------------------------------
    # OPENAI
    # ----------------------------------------------

    openai_api_key: str | None = None

    openai_model: str = "gpt-5.6-sol"


    # ----------------------------------------------
    # ANTHROPIC
    # ----------------------------------------------

    anthropic_api_key: str | None = None

    anthropic_model: str = "claude-opus-5"


    # ----------------------------------------------
    # GROQ
    # ----------------------------------------------

    groq_api_key: str | None = None

    groq_model: str = (
        "openai/gpt-oss-120b"
    )


    # ----------------------------------------------
    # GOOGLE
    # ----------------------------------------------

    google_api_key: str | None = None

    google_model: str = (
        "gemini-3.8-flash"
    )


    model_config = SettingsConfigDict(
        env_prefix="KYLOW_",
        env_file=str(ENV_FILE),
        extra="ignore",
    )


settings = Settings()

settings.database_url = (
    normalize_database_url(
        settings.database_url
    )
)
