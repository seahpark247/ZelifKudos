#!/usr/bin/env bash
# Run the app locally with .env loaded into the environment.
#
# Spring Boot resolves ${VAR} placeholders from real environment variables, not
# from a .env file, so .env has to be exported before the JVM starts.
#
#   ./run.sh              # development profile (default)
#   ./run.sh production   # production profile
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
echo "starting Kudos [${ENV}] — db=${DB_URL}"
exec ./gradlew bootRun -Dgrails.env="${ENV}"
