param(
    [string]$RepoRoot = (Split-Path -Parent $PSScriptRoot),
    [string]$Message = "Update Personal AI live roadmap"
)

$ErrorActionPreference = "Stop"
Set-Location $RepoRoot

if (-not (Test-Path ".\data\roadmap.json")) {
    throw "data/roadmap.json was not found in $RepoRoot"
}

# Validate the public data before publishing.
Get-Content ".\data\roadmap.json" -Raw | ConvertFrom-Json | Out-Null

$Unexpected = @(git status --porcelain --untracked-files=all | Where-Object {
    $name = $_.Substring(3).Trim().Replace("\", "/")
    $name -ne "data/roadmap.json"
})

if ($Unexpected.Count -gt 0) {
    Write-Host "Unexpected repository changes detected. Roadmap publish stopped:" -ForegroundColor Red
    $Unexpected | ForEach-Object { Write-Host $_ }
    exit 1
}

git add -- "data/roadmap.json"
if ($LASTEXITCODE -ne 0) { throw "Could not stage roadmap data." }

$staged = @(git diff --cached --name-only)
if ($staged.Count -eq 0) {
    Write-Host "No roadmap changes to publish." -ForegroundColor Yellow
    exit 0
}

if ($staged.Count -ne 1 -or $staged[0].Replace("\", "/") -ne "data/roadmap.json") {
    throw "Publish guard failed: only data/roadmap.json may be staged by this script."
}

git commit -m $Message
if ($LASTEXITCODE -ne 0) { throw "Roadmap commit failed." }

git push origin main
if ($LASTEXITCODE -ne 0) { throw "Roadmap push failed." }

Write-Host "Personal AI roadmap published to GitHub." -ForegroundColor Cyan
Write-Host "The command center polls the GitHub data source every 15 seconds." -ForegroundColor Cyan
