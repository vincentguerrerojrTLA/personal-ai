from app.providers.groq_provider import (
    GroqProvider,
)
from app.providers.anthropic_provider import (
    AnthropicProvider,
)
from app.providers.base import AIProvider
from app.providers.errors import (
    ProviderError,
    ProviderUnavailable,
)
from app.providers.google_provider import (
    GoogleProvider,
)
from app.providers.mock import MockProvider
from app.providers.openai_provider import (
    OpenAIProvider,
)


class ProviderRouter:

    def __init__(self):

        self.providers = {
            "groq":
                GroqProvider(),
            "openai":
                OpenAIProvider(),
            "anthropic":
                AnthropicProvider(),
            "google":
                GoogleProvider(),
            "mock":
                MockProvider(),
        }


        self.auto_priority = [
            "groq",
            "google",
            "openai",
            "anthropic",
            "mock",
        ]


    def get_provider(
        self,
        requested: str = "auto",
    ) -> AIProvider:

        requested = (
            requested.lower().strip()
        )


        if requested != "auto":

            provider = self.providers.get(
                requested
            )

            if provider is None:

                raise ProviderUnavailable(
                    "Unknown provider: "
                    + requested
                )

            if not provider.configured():

                raise ProviderUnavailable(
                    requested
                    + " is not configured."
                )

            return provider


        for name in self.auto_priority:

            provider = self.providers[name]

            if provider.configured():

                return provider


        raise ProviderUnavailable(
            "No AI provider is available."
        )


    def status(self):

        results = []

        for priority, name in enumerate(
            self.auto_priority,
            start=1,
        ):

            provider = self.providers[name]

            results.append(
                {
                    "provider": name,
                    "configured":
                        provider.configured(),
                    "model":
                        provider.model,
                    "priority":
                        priority,
                }
            )

        return results


    async def chat(
        self,
        messages,
        requested: str = "auto",
    ):

        if requested != "auto":

            provider = self.get_provider(
                requested
            )

            return await provider.chat(
                messages
            )


        failures = []


        for name in self.auto_priority:

            provider = self.providers[name]

            if not provider.configured():

                continue


            try:

                return await provider.chat(
                    messages
                )

            except ProviderError as exc:

                failures.append(
                    f"{name}: {exc}"
                )


        raise ProviderError(
            "All configured providers failed. "
            + " | ".join(failures)
        )


provider_router = ProviderRouter()


