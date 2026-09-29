# Kylow Core

Kylow Core is the provider-independent backend for Kylow.

The Android application, future desktop application, web client,
and other endpoints connect to Kylow Core instead of depending on
a specific AI model or on the user's personal computer.

## Current Phase

CORE-01

Implemented:

- FastAPI application
- health endpoint
- versioned API
- provider abstraction
- provider router
- mock AI provider
- chat service
- conversation IDs
- environment configuration
- automated tests

## Architecture Principle

Kylow is the AI.

Individual models and providers are replaceable intelligence engines
used by Kylow.

## Development

Run:

python -m uvicorn app.main:app --reload

Then:

GET /health

POST /v1/chat
