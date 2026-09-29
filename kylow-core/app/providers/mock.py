from app.models import ChatMessage
from app.providers.base import (
    AIProvider,
    ProviderResult,
)


class MockProvider(AIProvider):

    name = "mock"

    model = "development"


    def configured(self) -> bool:
        return True


    async def chat(
        self,
        messages: list[ChatMessage],
    ) -> ProviderResult:

        latest_user = next(
            (
                item.content
                for item in reversed(messages)
                if item.role == "user"
            ),
            "",
        )

        return ProviderResult(
            provider=self.name,
            model=self.model,
            content=(
                "Kylow Core is online. "
                f"I received: {latest_user}"
            ),
        )
