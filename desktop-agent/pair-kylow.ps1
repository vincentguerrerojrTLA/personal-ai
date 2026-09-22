$ErrorActionPreference="Stop"
$here=Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $here
if(-not(Test-Path ".venv")){py -3 -m venv .venv}
& .\.venv\Scripts\python.exe pairing.py
