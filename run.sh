#!/usr/bin/env bash
# Run the app locally with .env loaded into the environment.
#
# Spring Boot resolves ${VAR} placeholders from real environment variables, not
# from a .env file, so .env has to be exported before the JVM starts.
#
#   ./run.sh                        # development profile (default)
#   ./run.sh production             # production profile
#   ./run.sh development 7778       # dev instance beside the systemd service
#
# Development mode reloads changed classes and GSPs without a restart, which the
# packaged WAR the service runs cannot do — its contents are sealed at build time.
set -euo pipefail
cd "$(dirname "$0")"

if [[ ! -f .env ]]; then
    echo "error: .env not found — copy .env.example to .env and fill it in" >&2
    exit 1
fi

set -a
# shellcheck disable=SC1091
source ./.env
set +a

ENV="${1:-development}"

# Optional port override so a hot-reloading dev instance can run alongside the
# systemd service without fighting it for the port:
#     ./run.sh development 7778
if [[ -n "${2:-}" ]]; then
    export SERVER_PORT="$2"
fi

echo "starting Kudos [${ENV}] on port ${SERVER_PORT} — db=${DB_URL}"
exec ./gradlew bootRun -Dgrails.env="${ENV}"
