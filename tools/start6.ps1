$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
if (!(Test-Path '.env')) { Copy-Item '.env.example' '.env' }
docker compose up -d --build
if ($LASTEXITCODE -ne 0) { throw 'Khong khoi dong duoc. Xem docker compose logs.' }
Write-Host 'Mo http://localhost:8080/restman6/login sau khi Tomcat khoi dong.'
