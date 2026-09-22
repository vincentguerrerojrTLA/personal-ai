$ErrorActionPreference="Stop"
$here=Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $here
if (-not (Test-Path ".venv")) { py -3 -m venv .venv }
& .\.venv\Scripts\python.exe -m pip install -r requirements.txt
& .\.venv\Scripts\python.exe -c "from tls_identity import ensure_identity; ensure_identity()"
$cert=Join-Path $HOME ".kylow-agent\tls-cert.pem"
$key=Join-Path $HOME ".kylow-agent\tls-key.pem"
& .\.venv\Scripts\python.exe -m uvicorn kylow_agent:app --host 127.0.0.1 --port 8765 --ssl-certfile $cert --ssl-keyfile $key
