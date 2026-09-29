from app.db.models import Base
from app.db.session import engine


async def initialize_database():

    async with engine.begin() as connection:

        await connection.run_sync(
            Base.metadata.create_all
        )
