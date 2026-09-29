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


class AnthropicProvider(AIProvider):

    name = "anthropic"

    model = settings.anthropic_model


    def configured(self) -> bool:

        return bool(
            settings.anthropic_api_key
            and settings.anthropic_api_key.strip()
        )


    def _prepare(
        self,
        messages: list[ChatMessage],
    ):

        system_parts = []

        normal_messages = []


        for message in messages:

            if message.role == "system":

                system_parts.append(
                    message.content
                )

                continue


            role = message.role

            if role not in (
                "user",
                "assistant",
            ):

                role = "user"


            normal_messages.append(
                {
                    "role": role,
                    "content":
                        message.content,
                }
            )


        return (
            "\n\n".join(system_parts),
            normal_messages,
        )


    async def chat(
        self,
        messages: list[ChatMessage],
    ) -> ProviderResult:

        if not self.configured():

            raise ProviderUnavailable(
                "Anthropic is not configured."
            )


        system_prompt, converted = (
            self._prepare(messages)
        )


        headers = {
            "x-api-key":
                settings.anthropic_api_key,
            "anthropic-version":
                "2023-06-01",
            "content-type":
                "application/json",
        }


        body = {
            "model": self.model,
            "max_tokens": 8192,
            "messages": converted,
        }


        if system_prompt:

            body["system"] = system_prompt


        try:

            async with httpx.AsyncClient(
                timeout=settings
                .request_timeout_seconds
            ) as client:

                response = await client.post(
                    "https://api.anthropic.com/v1/messages",
                    headers=headers,
                    json=body,
                )

            response.raise_for_status()

        except httpx.HTTPStatusError as exc:

            detail = (
                exc.response.text[:1000]
            )

            raise ProviderError(
                "Anthropic request failed: "
                + detail
            ) from exc

        except httpx.HTTPError as exc:

            raise ProviderError(
                "Anthropic connection failed."
            ) from exc


        payload = response.json()

        pieces = [
            block.get("text", "")
            for block in payload.get(
                "content",
                [],
            )
            if block.get("type") == "text"
        ]

        content = "".join(
            pieces
        ).strip()


        if not content:

            raise ProviderError(
                "Anthropic returned no text."
            )


        return ProviderResult(
            provider=self.name,
            model=self.model,
            content=content,
        )
