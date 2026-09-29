import asyncio
import sys

from app.models import ChatMessage
from app.providers.router import provider_router


TEST_MESSAGE = (
    "This is a Kylow provider connectivity test. "
    "Reply briefly with: KYLOW LIVE TEST PASS"
)


async def main():

    statuses = provider_router.status()

    print()
    print("=" * 58)
    print(" KYLOW LIVE PROVIDER TEST")
    print("=" * 58)

    configured_real = 0
    passed_real = 0

    for status in statuses:

        name = status["provider"]

        if name == "mock":
            continue

        if not status["configured"]:

            print(
                f"SKIP  {name:<10} "
                f"{status['model']} "
                "(no API key)"
            )

            continue

        configured_real += 1

        print()
        print(
            f"TEST  {name:<10} "
            f"{status['model']}"
        )

        try:

            provider = (
                provider_router
                .get_provider(name)
            )

            result = await provider.chat(
                [
                    ChatMessage(
                        role="user",
                        content=TEST_MESSAGE,
                    )
                ]
            )

            print(
                f"PASS  {name:<10} "
                f"{result.model}"
            )

            preview = (
                result.content
                .replace("\n", " ")
                [:300]
            )

            print(
                "      Response: "
                + preview
            )

            passed_real += 1

        except Exception as exc:

            print(
                f"FAIL  {name:<10} "
                f"{type(exc).__name__}: "
                f"{exc}"
            )

    print()
    print("=" * 58)

    if configured_real == 0:

        print(
            "No real providers are configured yet."
        )

        print("=" * 58)

        return 2

    print(
        f"Configured: {configured_real}"
    )

    print(
        f"Passed:     {passed_real}"
    )

    print(
        f"Failed:     "
        f"{configured_real - passed_real}"
    )

    print("=" * 58)

    if passed_real != configured_real:
        return 1

    return 0


if __name__ == "__main__":
    sys.exit(asyncio.run(main()))
