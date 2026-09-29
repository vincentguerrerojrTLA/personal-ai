import json
from uuid import uuid4

from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
)
from fastapi.responses import (
    StreamingResponse,
)
from sqlalchemy.ext.asyncio import (
    AsyncSession,
)

from app.db.session import (
    SessionLocal,
    get_session,
)
from app.models import (
    ChatRequest,
    ChatResponse,
)
from app.providers.errors import (
    ProviderError,
)
from app.providers.router import (
    provider_router,
)
from app.services.chat import chat_service
from app.services.identity import kylow_messages
from app.services.conversations import (
    conversation_repository,
)


router = APIRouter()


@router.post(
    "/chat",
    response_model=ChatResponse,
)
async def chat(
    request: ChatRequest,
    session: AsyncSession = Depends(
        get_session
    ),
):

    try:

        return await chat_service.chat(
            session,
            request,
        )

    except ValueError as exc:

        raise HTTPException(
            status_code=400,
            detail=str(exc),
        ) from exc

    except ProviderError as exc:

        raise HTTPException(
            status_code=503,
            detail=str(exc),
        ) from exc


@router.post(
    "/chat/stream",
)
async def stream_chat(
    request: ChatRequest,
    session: AsyncSession = Depends(
        get_session
    ),
):

    if not request.messages:

        raise HTTPException(
            status_code=400,
            detail=(
                "At least one message "
                "is required."
            ),
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

        await conversation_repository.add_message(
            session,
            conversation.id,
            "user",
            latest_user.content,
        )


    await session.commit()


    try:

        provider = (
            provider_router.get_provider(
                request.provider
            )
        )

    except ProviderError as exc:

        raise HTTPException(
            status_code=503,
            detail=str(exc),
        ) from exc


    request_id = str(uuid4())


    async def event_generator():

        chunks = []

        yield (
            "event: metadata\n"
            "data: "
            + json.dumps(
                {
                    "request_id":
                        request_id,
                    "conversation_id":
                        conversation.id,
                    "provider":
                        provider.name,
                    "model":
                        provider.model,
                }
            )
            + "\n\n"
        )


        try:

            async for chunk in provider.stream(
                kylow_messages(
                    request.messages
                )
            ):

                chunks.append(chunk)

                yield (
                    "event: token\n"
                    "data: "
                    + json.dumps(
                        {
                            "text": chunk
                        }
                    )
                    + "\n\n"
                )


            content = "".join(chunks)


            async with SessionLocal() as write_session:

                await (
                    conversation_repository
                    .add_message(
                        write_session,
                        conversation.id,
                        "assistant",
                        content,
                        provider.name,
                        provider.model,
                    )
                )

                await write_session.commit()


            yield (
                "event: done\n"
                "data: "
                + json.dumps(
                    {
                        "status":
                            "complete"
                    }
                )
                + "\n\n"
            )


        except Exception as exc:

            yield (
                "event: error\n"
                "data: "
                + json.dumps(
                    {
                        "message":
                            str(exc)
                    }
                )
                + "\n\n"
            )


    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "X-Accel-Buffering": "no",
        },
    )

