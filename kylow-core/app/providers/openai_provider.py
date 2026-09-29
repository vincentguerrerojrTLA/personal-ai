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


class OpenAIProvider(AIProvider):

    name = "openai"

    model = settings.openai_model


    def configured(self) -> bool:

        return bool(
            settings.openai_api_key
            and settings.openai_api_key.strip()
        )


    def _input(
        self,
        messages: list[ChatMessage],
    ):

        result = []

        for message in messages:

            role = message.role

            if role == "tool":
                role = "user"

            result.append(
                {
                    "role": role,
                    "content": message.content,
                }
            )

        return result


    def _extract_text(
        self,
        payload: dict,
    ) -> str:

        pieces = []

        for item in payload.get(
            "output",
            [],
        ):

            if item.get("type") != "message":
                continue

            for part in item.get(
                "content",
                [],
            ):

                if (
                    part.get("type")
                    == "output_text"
                ):

                    text = part.get(
                        "text",
                        "",
                    )

                    if text:
                        pieces.append(text)

        content = "".join(pieces).strip()

        if not content:

            raise ProviderError(
                "OpenAI returned no text."
            )

        return content


    async def chat(
        self,
        messages: list[ChatMessage],
    ) -> ProviderResult:

        if not self.configured():

            raise ProviderUnavailable(
                "OpenAI is not configured."
            )

        headers = {
            "Authorization":
                f"Bearer {settings.openai_api_key}",
            "Content-Type":
                "application/json",
        }

        body = {
            "model": self.model,
            "input": self._input(messages),
        }

        try:

            async with httpx.AsyncClient(
                timeout=settings
                .request_timeout_seconds
            ) as client:

                response = await client.post(
                    "https://api.openai.com/v1/responses",
                    headers=headers,
                    json=body,
                )

            response.raise_for_status()

        except httpx.HTTPStatusError as exc:

            detail = (
                exc.response.text[:1000]
            )

            raise ProviderError(
                "OpenAI request failed: "
                + detail
            ) from exc

        except httpx.HTTPError as exc:

            raise ProviderError(
                "OpenAI connection failed."
            ) from exc


        content = self._extract_text(
            response.json()
        )

        return ProviderResult(
            provider=self.name,
            model=self.model,
            content=content,
        )
