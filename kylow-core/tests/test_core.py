import os
from pathlib import Path


TEST_DB = Path(
    "test-kylow.db"
)


if TEST_DB.exists():
    TEST_DB.unlink()


os.environ[
    "KYLOW_DATABASE_URL"
] = (
    "sqlite+aiosqlite:///./"
    "test-kylow.db"
)

os.environ[
    "KYLOW_PROVIDER"
] = "mock"

os.environ[
    "KYLOW_OPENAI_API_KEY"
] = ""

os.environ[
    "KYLOW_ANTHROPIC_API_KEY"
] = ""

os.environ[
    "KYLOW_GOOGLE_API_KEY"
] = ""


from fastapi.testclient import (
    TestClient,
)

from app.main import app


def test_health():

    with TestClient(app) as client:

        response = client.get(
            "/health"
        )

        assert response.status_code == 200

        body = response.json()

        assert body["status"] == "ok"

        assert (
            body["database"]
            == "ready"
        )


def test_provider_registry():

    with TestClient(app) as client:

        response = client.get(
            "/v1/providers"
        )

        assert response.status_code == 200

        body = response.json()

        names = {
            item["provider"]
            for item in body
        }

        assert names == {
            "groq",
            "openai",
            "anthropic",
            "google",
            "mock",
        }


def test_mock_provider_available():

    with TestClient(app) as client:

        response = client.get(
            "/v1/providers"
        )

        body = response.json()

        mock = next(
            item
            for item in body
            if item["provider"]
            == "mock"
        )

        assert (
            mock["configured"]
            is True
        )


def test_chat_persists():

    with TestClient(app) as client:

        response = client.post(
            "/v1/chat",
            json={
                "provider": "mock",
                "messages": [
                    {
                        "role": "user",
                        "content":
                            "Hello Kylow",
                    }
                ],
            },
        )

        assert response.status_code == 200

        body = response.json()

        assert body["provider"] == "mock"

        conversation_id = (
            body["conversation_id"]
        )

        history = client.get(
            "/v1/conversations/"
            + conversation_id
        )

        assert history.status_code == 200

        messages = (
            history.json()["messages"]
        )

        assert len(messages) == 2


def test_conversation_list():

    with TestClient(app) as client:

        response = client.get(
            "/v1/conversations"
        )

        assert response.status_code == 200

        assert isinstance(
            response.json(),
            list,
        )


def test_stream():

    with TestClient(app) as client:

        with client.stream(
            "POST",
            "/v1/chat/stream",
            json={
                "provider": "mock",
                "messages": [
                    {
                        "role": "user",
                        "content":
                            "Stream this",
                    }
                ],
            },
        ) as response:

            assert response.status_code == 200

            text = "".join(
                response.iter_text()
            )

            assert (
                "event: metadata"
                in text
            )

            assert (
                "event: token"
                in text
            )

            assert (
                "event: done"
                in text
            )



