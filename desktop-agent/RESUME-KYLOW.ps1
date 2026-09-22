$stop=Join-Path (Join-Path $HOME ".kylow-agent") "STOP"
if(Test-Path $stop){Remove-Item $stop -Force}
Write-Host "Kylow remote control enabled."
