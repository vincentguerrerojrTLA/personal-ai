$ErrorActionPreference="Stop"
if (-not $env:KYLOW_AGENT_TOKEN) { throw "KYLOW_AGENT_TOKEN is not set. Pair Kylow before starting the agent." }
$here=Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $here
if (-not (Test-Path ".venv")) { py -3 -m venv .venv }
& .\.venv\Scripts\python.exe -m pip install -r requirements.txt
& .\.venv\Scripts\python.exe -m uvicorn kylow_agent:app --host 127.0.0.1 --port 8765
