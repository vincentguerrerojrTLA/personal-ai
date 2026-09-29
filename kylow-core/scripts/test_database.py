import asyncio
import sys

if sys.platform == "win32":
    asyncio.set_event_loop_policy(
        asyncio.WindowsSelectorEventLoopPolicy()
    )

from sqlalchemy import text

from app.config import settings
from app.db.bootstrap import (
    initialize_database,
)
from app.db.session import engine


async def main():

    print()
    print("=" * 58)
    print(" KYLOW DATABASE TEST")
    print("=" * 58)

    database_type = (
        "Neon/Postgres"
        if "postgresql+psycopg"
        in settings.database_url
        else
        "SQLite"
    )

    print(
        "Database type:",
        database_type,
    )

    await initialize_database()

    async with engine.connect() as connection:

        result = await connection.execute(
            text("SELECT 1")
        )

        value = result.scalar_one()

    if value != 1:
        raise RuntimeError(
            "Database validation failed."
        )

    print(
        "Connection: PASS"
    )

    print(
        "Schema initialization: PASS"
    )

    print(
        "SELECT 1: PASS"
    )

    print("=" * 58)

    await engine.dispose()


if __name__ == "__main__":
    asyncio.run(main())

