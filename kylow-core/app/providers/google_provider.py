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


class GoogleProvider(AIProvider):

    name = "google"

    model = settings.google_model


    def configured(self) -> bool:

        return bool(
            settings.google_api_key
            and settings.google_api_key.strip()
        )


    def _prepare(
        self,
        messages: list[ChatMessage],
    ):

        system_parts = []

        contents = []


        for message in messages:

            if message.role == "system":

                system_parts.append(
                    message.content
                )

                continue


            role = (
                "model"
                if message.role
                == "assistant"
                else "user"
            )


            contents.append(
                {
                    "role": role,
                    "parts": [
                        {
                            "text":
                                message.content
                        }
                    ],
                }
            )


        return (
            "\n\n".join(system_parts),
            contents,
        )


    async def chat(
        self,
        messages: list[ChatMessage],
    ) -> ProviderResult:

        if not self.configured():

            raise ProviderUnavailable(
                "Google is not configured."
            )


        system_prompt, contents = (
            self._prepare(messages)
        )


        body = {
            "contents": contents,
        }


        if system_prompt:

            body["systemInstruction"] = {
                "parts": [
                    {
                        "text":
                            system_prompt
                    }
                ]
            }


        url = (
            "https://generativelanguage.googleapis.com/"
            "v1beta/models/"
            f"{self.model}:generateContent"
        )


        headers = {
            "x-goog-api-key":
                settings.google_api_key,
            "Content-Type":
                "application/json",
        }


        try:

            async with httpx.AsyncClient(
                timeout=settings
                .request_timeout_seconds
            ) as client:

                response = await client.post(
                    url,
                    headers=headers,
                    json=body,
                )

            response.raise_for_status()

        except httpx.HTTPStatusError as exc:

            detail = (
                exc.response.text[:1000]
            )

            raise ProviderError(
                "Google request failed: "
                + detail
            ) from exc

        except httpx.HTTPError as exc:

            raise ProviderError(
                "Google connection failed."
            ) from exc


        payload = response.json()

        candidates = payload.get(
            "candidates",
            [],
        )


        if not candidates:

            raise ProviderError(
                "Google returned no candidates."
            )


        parts = (
            candidates[0]
            .get("content", {})
            .get("parts", [])
        )


        content = "".join(
            part.get("text", "")
            for part in parts
            if "text" in part
        ).strip()


        if not content:

            raise ProviderError(
                "Google returned no text."
            )


        return ProviderResult(
            provider=self.name,
            model=self.model,
            content=content,
        )
