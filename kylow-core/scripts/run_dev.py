import asyncio
import os
import selectors
import sys
from pathlib import Path


CORE_ROOT = (
    Path(__file__)
    .resolve()
    .parent
    .parent
)

if str(CORE_ROOT) not in sys.path:
    sys.path.insert(
        0,
        str(CORE_ROOT),
    )

os.chdir(CORE_ROOT)


from app.main import app
import uvicorn


async def serve():
    loop = asyncio.get_running_loop()

    print(
        "Kylow event loop:",
        type(loop).__name__,
    )

    config = uvicorn.Config(
        app=app,
        host="0.0.0.0",
        port=8000,
        reload=False,
        workers=1,
        log_level="info",
    )

    server = uvicorn.Server(config)

    await server.serve()


def selector_loop():
    return asyncio.SelectorEventLoop(
        selectors.SelectSelector()
    )


if __name__ == "__main__":

    if sys.platform == "win32":

        with asyncio.Runner(
            loop_factory=selector_loop
        ) as runner:

            runner.run(
                serve()
            )

    else:

        asyncio.run(
            serve()
        )
