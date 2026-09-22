$ErrorActionPreference="Stop"
$here=Split-Path -Parent $MyInvocation.MyCommand.Path
$taskName="KylowDesktopAgent"
$script=Join-Path $here "start-kylow-agent.ps1"
$action=New-ScheduledTaskAction -Execute "powershell.exe" -Argument "-NoProfile -ExecutionPolicy Bypass -File `"$script`""
$trigger=New-ScheduledTaskTrigger -AtLogOn
Register-ScheduledTask -TaskName $taskName -Action $action -Trigger $trigger -Description "Owner-controlled Kylow desktop agent" -Force | Out-Null
Write-Host "Kylow startup task installed."
