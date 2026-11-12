#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || cp .env.example .env
docker compose up -d --build
printf '\nMở http://localhost:8080/restman6/login sau khi Tomcat khởi động.\n'
