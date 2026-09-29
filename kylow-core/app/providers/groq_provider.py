import httpx

from app.config import settings
from app.models import ChatMessage
from app.providers.base import (
    AIProvider,
    ProviderResult,
)
from app.providers.errors import (
    ProviderError,
    ProviderUnavailable,
)


class GroqProvider(AIProvider):

    name = "groq"

    model = settings.groq_model


    def configured(self) -> bool:

        return bool(
            settings.groq_api_key
            and settings.groq_api_key.strip()
        )


    def _messages(
        self,
        messages: list[ChatMessage],
    ) -> list[dict]:

        converted = []

        for message in messages:

            role = message.role

            if role == "tool":
                role = "user"

            converted.append(
                {
                    "role": role,
                    "content": message.content,
                }
            )

        return converted


    async def chat(
        self,
        messages: list[ChatMessage],
    ) -> ProviderResult:

        if not self.configured():

            raise ProviderUnavailable(
                "Groq is not configured."
            )

        headers = {
            "Authorization":
                f"Bearer {settings.groq_api_key}",
            "Content-Type":
                "application/json",
        }

        body = {
            "model": self.model,
            "messages": self._messages(
                messages
            ),
        }

        try:

            async with httpx.AsyncClient(
                timeout=settings
                .request_timeout_seconds
            ) as client:

                response = await client.post(
                    "https://api.groq.com/openai/v1/chat/completions",
                    headers=headers,
                    json=body,
                )

            response.raise_for_status()

        except httpx.HTTPStatusError as exc:

            detail = exc.response.text[:1500]

            raise ProviderError(
                "Groq request failed: "
                + detail
            ) from exc

        except httpx.HTTPError as exc:

            raise ProviderError(
                "Groq connection failed."
            ) from exc


        payload = response.json()

        choices = payload.get(
            "choices",
            [],
        )

        if not choices:

            raise ProviderError(
                "Groq returned no choices."
            )

        content = (
            choices[0]
            .get("message", {})
            .get("content", "")
        )

        if not content:

            raise ProviderError(
                "Groq returned no text."
            )

        return ProviderResult(
            provider=self.name,
            model=self.model,
            content=content.strip(),
        )
