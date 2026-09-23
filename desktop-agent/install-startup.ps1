$ErrorActionPreference="Stop"
$here=Split-Path -Parent $MyInvocation.MyCommand.Path
$script=Join-Path $here "start-kylow-agent.ps1"
$startup=Join-Path $env:APPDATA "Microsoft\Windows\Start Menu\Programs\Startup\Kylow-Agent.cmd"
$lines=@("@echo off","powershell.exe -NoProfile -ExecutionPolicy Bypass -File `"$script`"")
Set-Content -Path $startup -Value $lines -Encoding ASCII
Write-Host "Kylow startup installed for the current Windows user."
