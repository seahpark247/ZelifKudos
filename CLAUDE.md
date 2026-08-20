# CLAUDE.md

Grails 7 (Groovy) / PostgreSQL 16 peer recognition app. Architecture, config
variables and feature behaviour live in `README.md` — read that first and don't
duplicate it here.

## Running

This VM runs the app as a systemd service (`deploy/kudos.service`), not from a
terminal. It is enabled, so it survives crashes (`Restart=always`) and reboots.

| Task | Command |
| --- | --- |
| Start / stop / restart | `systemctl {start,stop,restart} kudos` (needs root) |
| Status | `systemctl status kudos` |
| Logs (live) | `journalctl -u kudos -f` |
| Deploy a code change | `./gradlew assemble -Dgrails.env=production`, then restart the service |
| Dev instance, hot reload, port 7778 | `./run.sh development 7778` |

`./run.sh` is for development only — it dies with the terminal. Never use it to
"start the app" on this box; the service is already listening on 7777.

Run a dev instance on 7778 rather than 7777 so it doesn't fight the service for
the port. Development mode reloads changed classes and GSPs; the packaged WAR
the service runs cannot, so any production check needs the assemble + restart
cycle above.

`serve.sh` copies the WAR to `run/` before executing it. That is deliberate:
`gradle assemble` overwrites `build/libs` in place, and a live JVM whose WAR
changes underneath it dies on the next lazy class load. So building while the
service runs is safe — only the restart swaps the running code.

## Serving

The app speaks plain HTTP on `SERVER_PORT`; there is no TLS inside the JVM. A
browser that reaches it over `https://` fails with `ERR_SSL_PROTOCOL_ERROR` —
that is the expected answer from a plaintext port, not a broken service.

Production puts Caddy in front to terminate TLS, with the hostname supplied by
DuckDNS. `deploy/Caddyfile` is a template — this repo is public, so the real
hostname lives only in `/etc/caddy/Caddyfile` and in `.env`. That DNS record is maintained by hand — nothing on
this box updates it, so a new public IP has to be entered at duckdns.org or the
site and its certificate renewal both fail. Behind the proxy three settings move
together and are only correct as a set:

- `SERVER_URL` — the `https://` hostname, since magic links are built from it
- `SERVER_BIND=127.0.0.1` — so the plain-HTTP port is reachable only by the proxy
- `SERVER_FORWARD_HEADERS=NATIVE` — so `X-Forwarded-*` from Caddy is honoured

Leave `SERVER_FORWARD_HEADERS` at `NONE` whenever the app is exposed directly;
those headers are attacker-controlled without a trusted proxy setting them.

Caddy must be enabled alongside `kudos.service`. It renews the certificate on its
own, but only while it is running and port 80 stays open — a stopped proxy or a
closed port surfaces as a site-wide expiry warning weeks later, not immediately.

## Environment

`.env` is gitignored and holds real credentials. Both `run.sh` (via `source`)
and systemd (via `EnvironmentFile=`) read the same file, so quote any value
containing spaces or it breaks shell parsing. Spring Boot resolves `${VAR}` from
the real environment and never reads `.env` itself — that is why `./gradlew
bootRun` directly does not work.

## Scheduled work

A Quartz job sends the weekly email at `WEEKLY_EMAIL_CRON` (default Friday 18:00)
in `APP_TIMEZONE`, then resets the week. If the app is down at that moment the
week's send is skipped entirely, which is the practical reason the service stays
up 24/7. Emails all send before anything resets: one failure means no reset.

## Conventions

- All Groovy sources are in the `kudos` package.
- Controllers stay thin; business logic lives in `grails-app/services` under
  `@Transactional`.
- Schema changes go through Liquibase changelogs in `src/main/resources`, which
  run at startup — never hand-edit a deployed schema.
- Nothing company-specific is hardcoded; new environment-dependent values get an
  entry in `.env.example` and a row in the README config table.
