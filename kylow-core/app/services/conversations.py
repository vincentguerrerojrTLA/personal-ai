from uuid import uuid4

from sqlalchemy import (
    func,
    select,
)
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from app.db.models import (
    ConversationRecord,
    MessageRecord,
)


class ConversationRepository:

    async def ensure_conversation(
        self,
        session: AsyncSession,
        conversation_id: str | None,
    ) -> ConversationRecord:

        if conversation_id:

            existing = await session.get(
                ConversationRecord,
                conversation_id,
            )

            if existing:
                return existing

        conversation = ConversationRecord(
            id=conversation_id or str(uuid4())
        )

        session.add(conversation)

        await session.flush()

        return conversation


    async def add_message(
        self,
        session: AsyncSession,
        conversation_id: str,
        role: str,
        content: str,
        provider: str | None = None,
        model: str | None = None,
    ) -> MessageRecord:

        message = MessageRecord(
            id=str(uuid4()),
            conversation_id=conversation_id,
            role=role,
            content=content,
            provider=provider,
            model=model,
        )

        session.add(message)

        await session.flush()

        return message


    async def get_conversation(
        self,
        session: AsyncSession,
        conversation_id: str,
    ) -> ConversationRecord | None:

        result = await session.execute(
            select(ConversationRecord)
            .options(
                selectinload(
                    ConversationRecord.messages
                )
            )
            .where(
                ConversationRecord.id
                == conversation_id
            )
        )

        return result.scalar_one_or_none()


    async def list_conversations(
        self,
        session: AsyncSession,
        limit: int = 50,
    ):

        message_count = (
            select(
                MessageRecord.conversation_id,
                func.count(
                    MessageRecord.id
                ).label("message_count"),
            )
            .group_by(
                MessageRecord.conversation_id
            )
            .subquery()
        )

        result = await session.execute(
            select(
                ConversationRecord,
                func.coalesce(
                    message_count.c.message_count,
                    0,
                ),
            )
            .outerjoin(
                message_count,
                ConversationRecord.id
                == message_count.c.conversation_id,
            )
            .order_by(
                ConversationRecord.updated_at.desc()
            )
            .limit(limit)
        )

        return result.all()


conversation_repository = ConversationRepository()
