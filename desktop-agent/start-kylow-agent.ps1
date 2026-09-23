$ErrorActionPreference="Stop"
$here=Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $here
$tokenPath=Join-Path $HOME ".kylow-agent\agent-token"
if (-not $env:KYLOW_AGENT_TOKEN -and (Test-Path $tokenPath)) { $env:KYLOW_AGENT_TOKEN=(Get-Content $tokenPath -Raw).Trim() }
if (-not $env:KYLOW_AGENT_TOKEN) { throw "Kylow agent token is unavailable. Pair Kylow first." }
if (-not (Test-Path ".venv\Scripts\python.exe")) { py -3.12 -m venv .venv }
& .\.venv\Scripts\python.exe -m pip install -r requirements.txt
& .\.venv\Scripts\python.exe -m uvicorn kylow_agent:app --host 127.0.0.1 --port 8765
