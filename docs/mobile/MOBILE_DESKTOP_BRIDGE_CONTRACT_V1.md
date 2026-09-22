# Kylow Mobile ↔ Desktop Bridge Contract v1

Status: **ACTIVE / JOINT CONTRACT**  
Date: 2026-09-22

This document is the public, sanitized interface contract used by the Android and Windows Kylow workstreams. It intentionally contains no private tokens, keys, customer data, local secrets, or sensitive recovery material.

## One Kylow rule

Windows, Android, and future iOS are endpoints of one Kylow identity. Device-local state exists for UI/cache/offline continuity; it must not silently fork identity, owner authority, permissions, memory/history, tasks, or capability definitions.

## Current joint baseline

- Desktop workstream: PR #26, branch `kylow-remote-desktop-control`, head `1702d2d` at contract creation.
- Mobile workstream: branch `kylow-mobile-desktop-bridge`, intentionally based on that Desktop head.
- Desktop remote-control foundation is currently localhost-only. Secure phone transport/pairing is not yet proven end-to-end.

## Protocol rules

- Version every mobile-facing endpoint.
- Unknown/incompatible major protocol versions fail closed.
- Pairing uses short-lived, one-use owner-visible material and returns a revocable device-specific credential.
- Mobile does not ask the owner to copy a permanent bearer token manually.
- Transport is encrypted and bound to the expected Desktop endpoint identity.
- Secrets are never committed, logged, placed in Notion, or displayed after enrollment.
- Requests use unique IDs/nonces; replay or stale-session behavior is rejected.
- Desktop emergency stop remains authoritative and remote execution fails closed while stopped.

## Joint v1 endpoint target

Unless both workstreams record a compatible revision before lock:

- `POST /kylow/v1/pair` — one-time pairing exchange.
- `GET /kylow/v1/health` — authenticated connectivity + stop-state check.
- `POST /kylow/v1/chat` — Kylow chat/task request with request ID, conversation ID, device ID, nonce, and timestamp.
- `POST /kylow/v1/action` — bounded, permission-gated PC action request.
- `POST /kylow/v1/unpair` — revoke this Android device credential.

## Truthful action lifecycle

Consequential operations are represented as:

`REQUESTED → AUTHORIZED → ATTEMPTED → VERIFIED SUCCESS / FAILED`

Sending a request is not proof that the action happened.

## Security boundary

- No router port forwarding requirement.
- No cleartext HTTP remote transport.
- No unauthenticated Internet-exposed control API.
- No arbitrary shell endpoint in the initial remote-control release.
- Remote actions remain scoped/allowlisted and auditable.
- Credentials are device-specific and revocable.
- Android stores sensitive pairing material with Android Keystore-backed encryption.
- Lost/stolen phone revocation must be possible from Desktop Kylow.

## Completion gate

The bridge is not COMPLETE until both sides implement the same contract, automated security/protocol tests pass, and a real Android device ↔ real Windows PC pairing/chat/revoke/reconnect test is recorded.
