# PERSONAL AI — Command Center

GitHub-hosted development observatory and interactive roadmap for the owner-controlled Personal AI build.

## Structure

- `index.html` — public command-center interface
- `assets/styles.css` — blue/black interface system
- `assets/app.js` — interactive roadmap, workspaces, filtering, activity feed, and live polling
- `data/roadmap.json` — public single source of truth for roadmap/status data
- `.nojekyll` — serve the repository as a plain static GitHub Pages site

## Update model

The website reads `data/roadmap.json` and periodically refreshes it from the repository. When the Personal AI build advances, update that JSON file and commit it to `main`; the public command center will pick up the new state automatically.

## Current build state

- Foundation Gates 00–15: COMPLETE
- PERSONAL AI v1 Step 1 — Unified Runtime Foundation: COMPLETE
- PERSONAL AI v1 Step 2 — Self-Improvement Engine: COMPLETE
- PERSONAL AI v1 Step 3 — Adaptive Hardware Optimizer: COMPLETE
- PERSONAL AI v1 Step 4 — Unified PersonalAI.exe v1: NEXT

THE LONG AFTER remains an external project integration. Direct writes to the real game project remain disabled.
