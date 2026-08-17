#!/usr/bin/env bash
# Run the packaged production WAR. This is what the systemd unit executes.
#
# Build it first:
#   ./gradlew assemble -Dgrails.env=production
#
# systemd supplies the environment through EnvironmentFile=.env; sourcing .env
# here as well keeps the script usable by hand.
set -euo pipefail
cd "$(dirname "$0")"

if [[ -f .env && -z "${DB_URL:-}" ]]; then
    set -a
    # shellcheck disable=SC1091
    source ./.env
    set +a
fi

# The build emits two WARs: the Boot-repackaged one, and a "-plain" library WAR
# with no embedded server. Only the former can be run with java -jar.
WAR="$(ls -t build/libs/*.war 2>/dev/null | grep -v -- '-plain\.war$' | head -1 || true)"
if [[ -z "${WAR}" ]]; then
    echo "error: no runnable WAR in build/libs — run: ./gradlew assemble -Dgrails.env=production" >&2
    exit 1
fi

# Run from a private copy. `gradle assemble` overwrites build/libs in place, and a
# live JVM whose WAR changes underneath it dies with NoClassDefFoundError the next
# time it lazily loads a class. Copying decouples building from running.
mkdir -p run
cp -f "${WAR}" run/kudos-running.war

# Word splitting on JAVA_OPTS is deliberate.
# shellcheck disable=SC2086
exec java ${JAVA_OPTS:--Xmx512m} -Dgrails.env=production -jar run/kudos-running.war
