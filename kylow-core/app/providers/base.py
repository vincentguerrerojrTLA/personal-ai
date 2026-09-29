from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import AsyncIterator

from app.models import ChatMessage


@dataclass
class ProviderResult:

    provider: str

    model: str

    content: str


class AIProvider(ABC):

    name: str = "unknown"

    model: str = "unknown"


    @abstractmethod
    def configured(self) -> bool:
        raise NotImplementedError


    @abstractmethod
    async def chat(
        self,
        messages: list[ChatMessage],
    ) -> ProviderResult:
        raise NotImplementedError


    async def stream(
        self,
        messages: list[ChatMessage],
    ) -> AsyncIterator[str]:

        result = await self.chat(messages)

        words = result.content.split(" ")

        for index, word in enumerate(words):

            if index:
                yield " "

            yield word
