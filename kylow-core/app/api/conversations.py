from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
    Query,
)
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.session import get_session
from app.models import (
    ConversationResponse,
    ConversationSummary,
    StoredMessage,
)
from app.services.conversations import (
    conversation_repository,
)


router = APIRouter()


@router.get(
    "/conversations/{conversation_id}",
    response_model=ConversationResponse,
)
async def get_conversation(
    conversation_id: str,
    session: AsyncSession = Depends(
        get_session
    ),
):

    conversation = (
        await conversation_repository
        .get_conversation(
            session,
            conversation_id,
        )
    )

    if conversation is None:

        raise HTTPException(
            status_code=404,
            detail="Conversation not found.",
        )

    return ConversationResponse(
        id=conversation.id,
        created_at=conversation.created_at,
        updated_at=conversation.updated_at,
        messages=[
            StoredMessage(
                id=item.id,
                role=item.role,
                content=item.content,
                provider=item.provider,
                model=item.model,
                created_at=item.created_at,
            )
            for item in conversation.messages
        ],
    )


@router.get(
    "/conversations",
    response_model=list[ConversationSummary],
)
async def list_conversations(
    limit: int = Query(
        default=50,
        ge=1,
        le=100,
    ),
    session: AsyncSession = Depends(
        get_session
    ),
):

    rows = await (
        conversation_repository
        .list_conversations(
            session,
            limit,
        )
    )

    return [
        ConversationSummary(
            id=conversation.id,
            created_at=conversation.created_at,
            updated_at=conversation.updated_at,
            message_count=message_count,
        )
        for conversation, message_count
        in rows
    ]
