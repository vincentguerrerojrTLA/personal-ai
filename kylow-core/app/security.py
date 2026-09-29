$ErrorActionPreference = "Stop"

$root   = "C:\Users\Vince\personal-ai"
$core   = "$root\kylow-core"
$python = "$core\.venv\Scripts\python.exe"
$branchExpected = "kylow-secure-desktop-bridge-v1"

Set-Location $root

Write-Host "=========================================="
Write-Host " KYLOW CLOUD PUBLISH"
Write-Host "=========================================="

# --------------------------------------------------
# BRANCH SAFETY
# --------------------------------------------------

$branch = (git branch --show-current).Trim()

Write-Host "Current branch: $branch"

if ($branch -ne $branchExpected) {
    throw "Wrong branch. Expected $branchExpected"
}

$existingStaged = @(git diff --cached --name-only)

if ($existingStaged.Count -gt 0) {
    Write-Host "Existing staged files:"
    $existingStaged
    throw "Existing staged work detected. Nothing changed."
}

Write-Host "PASS: Git staging area clean"


# --------------------------------------------------
# VERIFY CORE EXISTS
# --------------------------------------------------

$required = @(
    "$core\app\main.py",
    "$core\app\config.py",
    "$core\requirements.txt"
)

foreach ($file in $required) {
    if (-not (Test-Path $file)) {
        throw "Missing required file: $file"
    }
}

Write-Host "PASS: Kylow Core found"


# --------------------------------------------------
# BACKUP
# --------------------------------------------------

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backup = "$root\automation\backups\cloud-publish\$stamp"

New-Item -ItemType Directory -Force $backup | Out-Null

foreach ($file in @(
    "$core\app\main.py",
    "$core\requirements.txt",
    "$root\.gitignore",
    "$root\render.yaml"
)) {
    if (Test-Path $file) {
        Copy-Item $file $backup -Force
    }
}

Write-Host "PASS: backup created"


# --------------------------------------------------
# GITIGNORE
# --------------------------------------------------

$gitignore = "$root\.gitignore"

if (-not (Test-Path $gitignore)) {
    New-Item -ItemType File $gitignore | Out-Null
}

$ignoreEntries = @(
    "kylow-core/.env",
    "kylow-core/.venv/",
    "kylow-core/*.db",
    "kylow-core/*.db-*",
    "kylow-core/**/__pycache__/",
    "kylow-core/.pytest_cache/",
    "automation/secrets/"
)

$currentIgnore = @(Get-Content $gitignore -ErrorAction SilentlyContinue)

foreach ($entry in $ignoreEntries) {
    if ($currentIgnore -notcontains $entry) {
        Add-Content $gitignore $entry
    }
}

Write-Host "PASS: local secrets protected"


# --------------------------------------------------
# CLOUD API AUTH
# --------------------------------------------------

@'
import hmac
import os

from fastapi import Request
from starlette.responses import JSONResponse


def authorize_api_request(
    path: str,
    authorization: str | None,
):
    if not path.startswith("/v1/"):
        return None

    environment = (
        os.getenv(
            "KYLOW_ENVIRONMENT",
            "development",
        )
        .strip()
        .lower()
    )

    token = (
        os.getenv(
            "KYLOW_API_TOKEN",
            "",
        )
        .strip()
    )

    if not token:
        if environment == "production":
            return (
                503,
                "Kylow API authentication is not configured.",
            )

        return None

    authorization = (
        authorization or ""
    ).strip()

    if not authorization.startswith("Bearer "):
        return (
            401,
            "Authentication required.",
        )

    supplied = authorization[7:].strip()

    if not supplied:
        return (
            401,
            "Authentication required.",
        )

    if not hmac.compare_digest(
        supplied,
        token,
    ):
        return (
            401,
            "Invalid authentication token.",
        )

    return None


async def enforce_api_auth(
    request: Request,
):
    result = authorize_api_request(
        request.url.path,
        request.headers.get("authorization"),
    )

    if result is None:
        return None

    status_code, detail = result

    return JSONResponse(
        status_code=status_code,
        content={
            "detail": detail,
        },
    )
