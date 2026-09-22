# Kylow Production Rules

## Approved Art = Production Asset
When the owner explicitly approves artwork, that exact asset becomes a locked production asset. It must be inserted into every already-defined app location it belongs to during the active development cycle. Do not leave an approved asset represented by a placeholder for a later cleanup pass.

If the exact approved binary is unavailable to the build pipeline, mark that location BLOCKED-ASSET rather than claiming integration is complete.

## Large-Batch Production
Prefer substantial compatible vertical slices over intentionally small releases. Build, test, fix, merge, then immediately advance to the next coherent production block.
