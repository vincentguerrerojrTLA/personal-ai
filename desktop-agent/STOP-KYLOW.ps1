$root=Join-Path $HOME ".kylow-agent"
New-Item -ItemType Directory -Force -Path $root | Out-Null
New-Item -ItemType File -Force -Path (Join-Path $root "STOP") | Out-Null
Write-Host "KYLOW REMOTE CONTROL STOPPED."
