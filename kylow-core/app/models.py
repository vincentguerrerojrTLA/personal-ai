from datetime import datetime
from typing import Literal

from pydantic import BaseModel, Field


Role = Literal[
    "system",
    "user",
    "assistant",
    "tool",
]


ProviderChoice = Literal[
    "auto",
    "openai",
    "anthropic",
    "google",
    "mock",
]


class ChatMessage(BaseModel):

    role: Role

    content: str = Field(
        min_length=1,
        max_length=100_000,
    )


class ChatRequest(BaseModel):

    conversation_id: str | None = None

    provider: ProviderChoice = "auto"

    messages: list[ChatMessage]


class ChatResponse(BaseModel):

    request_id: str

    conversation_id: str

    provider: str

    model: str

    message: ChatMessage


class StoredMessage(BaseModel):

    id: str

    role: str

    content: str

    provider: str | None = None

    model: str | None = None

    created_at: datetime


class ConversationResponse(BaseModel):

    id: str

    created_at: datetime

    updated_at: datetime

    messages: list[StoredMessage]


class ConversationSummary(BaseModel):

    id: str

    created_at: datetime

    updated_at: datetime

    message_count: int


class ProviderStatus(BaseModel):

    provider: str

    configured: bool

    model: str

    priority: int


class HealthResponse(BaseModel):

    status: str

    service: str

    environment: str

    provider: str

    database: str


