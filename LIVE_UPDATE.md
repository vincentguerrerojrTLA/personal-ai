# Live roadmap update bridge

The public command center treats `data/roadmap.json` on the `main` branch as its public source of truth.

## How it works

1. The website loads its bundled `data/roadmap.json` immediately.
2. It then polls the raw GitHub `main` copy every 15 seconds.
3. When the Personal AI build advances, update only the public milestone fields in `data/roadmap.json`.
4. Run `scripts/publish-roadmap.ps1` from a local clone of this repository.
5. The script validates the JSON, refuses to publish if unrelated files are dirty, stages only the roadmap file, commits it, and pushes `main`.
6. The live command center detects the updated timestamp/data on its next poll.

## Privacy rule

Only milestone-level public information belongs in `data/roadmap.json`.

Do **not** publish:

- secrets, tokens, passwords, or credentials
- private local paths or recovery artifacts
- owner-only notes
- private project data
- raw memory stores
- unpublished external-project content

## Personal AI integration target

The local Personal AI can eventually treat `scripts/publish-roadmap.ps1` as the final public-status step after a milestone has passed its deterministic tests, full regression, protected-core verification, checkpoint, and clean-state requirements.

A failed or incomplete milestone must not be marked `COMPLETE` merely because implementation started.
