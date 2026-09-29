from uuid import uuid4

from sqlalchemy.ext.asyncio import AsyncSession

from app.models import (
    ChatMessage,
    ChatRequest,
    ChatResponse,
)
from app.providers.router import (
    provider_router,
)
from app.services.identity import kylow_messages
from app.services.conversations import (
    conversation_repository,
)


class ChatService:

    async def chat(
        self,
        session: AsyncSession,
        request: ChatRequest,
    ) -> ChatResponse:

        if not request.messages:

            raise ValueError(
                "At least one message is required."
            )


        conversation = (
            await conversation_repository
            .ensure_conversation(
                session,
                request.conversation_id,
            )
        )


        latest_user = next(
            (
                item
                for item in reversed(
                    request.messages
                )
                if item.role == "user"
            ),
            None,
        )


        if latest_user:

            await (
                conversation_repository
                .add_message(
                    session,
                    conversation.id,
                    "user",
                    latest_user.content,
                )
            )


        result = await provider_router.chat(
            kylow_messages(
                request.messages
            ),
            request.provider,
        )


        await conversation_repository.add_message(
            session,
            conversation.id,
            "assistant",
            result.content,
            result.provider,
            result.model,
        )


        await session.commit()


        return ChatResponse(
            request_id=str(uuid4()),
            conversation_id=
                conversation.id,
            provider=result.provider,
            model=result.model,
            message=ChatMessage(
                role="assistant",
                content=result.content,
            ),
        )


chat_service = ChatService()

