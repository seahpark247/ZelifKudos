# Kudos

A peer recognition app for the team.

Whenever someone lends you a hand, brightens your day, or does something worth
celebrating, send them a kudo. It takes five seconds, but it can make someone's
whole day.

## Overview

Every week, teammates send each other kudos. On Friday evening, everyone gets a
personalized email with the week's top stars and the messages they received.
Then the counter resets and a new week starts.

## How It Works

**Login**: No passwords. You enter your company email, get a magic link, click
it, you're in. The link expires in 15 minutes. Only addresses on the configured
company domain are allowed, so authentication and access control happen in one
step.

**Kudos**: Pick a teammate, write an optional message, send. There's a daily
limit (5 per person) and a cooldown (10 min) to keep things genuine.

**Weekly Email**: Every Friday at 6 PM, a Quartz job sends each person an HTML
email with:
- Top 3 stars of the week (dense ranking, ties share the same spot)
- How many kudos you got and what people said
- A rotating self-esteem message

The system sends all emails first, then resets only if every email succeeds. If
one fails, nothing resets. No one gets skipped.

**Weekly Reset**: Every reset is logged with who triggered it (the system on
Fridays, or an admin manually). Full audit trail.

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│   Browser   │────>│  Controller  │────>│   Service   │
│  (GSP+JS)   │<────│ + Interceptor│<────│ (@Transact) │
└─────────────┘     └──────────────┘     └──────┬──────┘
                                                │
                                      GORM/HQL  │
                                                v
                                        ┌─────────────┐
                                        │ PostgreSQL  │
                                        │ + Liquibase │
                                        │ + Session   │
                                        └─────────────┘
       ┌─────────────┐
       │   Quartz    │──> WeeklyEmailService ──> SMTP
       │ (FRI 18:00) │
       └─────────────┘
```

## Tech Stack

Grails 7 (Groovy) on Spring Boot, PostgreSQL 16, Quartz for scheduled jobs,
Liquibase for DB migrations, Spring Session JDBC so sessions survive deploys.

Deployable on a single small VM via Docker. The UI is a Windows 98 retro theme,
800+ lines of pure CSS, mobile-responsive.

## Configuration

All environment-specific values are read from the environment — nothing about a
particular company is hardcoded. See `.env.example` for the full list.

| Variable | Required | Purpose |
| --- | --- | --- |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_NAME` | yes | PostgreSQL connection |
| `APP_NAME` | no | Product name shown in page titles, window chrome, PWA manifest and email (default `Kudos`). Quote values containing spaces |
| `COMPANY_EMAIL_DOMAIN` | yes | Only this email domain may request a magic link |
| `SERVER_URL` | yes in production | Absolute base URL used to build magic links. If unset, links point at `localhost` and nobody can log in |
| `SERVER_PORT` | no | Port to listen on (default 7777). Keep it below 32768 so it never collides with the ephemeral port range, and keep `SERVER_URL`'s port in sync |
| `SUPER_ADMIN_EMAIL` | no | The single account allowed to grant itself admin. Leave empty to disable self-promotion |
| `SMTP_*` | no | Without these the app still runs, but magic links and weekly emails won't send |
| `SERVER_BIND` | no | Address to bind (default `0.0.0.0`). Set `127.0.0.1` behind a TLS proxy |
| `SERVER_FORWARD_HEADERS` | no | `NATIVE` behind a reverse proxy so `X-Forwarded-*` is honoured, otherwise `NONE` (default) |

### Serving over HTTPS

The app speaks plain HTTP only; there is no TLS inside the JVM. That is fine
locally, but in production the magic link is a bearer token sent by email — over
HTTP anyone who observes the link can use it, so a deployment reachable outside a
trusted network needs TLS in front.

Let's Encrypt will not certify a bare IP address on ordinary terms, so a
hostname comes first. `deploy/Caddyfile` is the reverse proxy that obtains and
renews the certificate on its own.

The hostname currently comes from DuckDNS and its record is set by hand, so a
change of public IP does not heal itself: the site keeps resolving to the old
address until someone updates the record. Certificate renewal fails with it,
which turns a silent DNS drift into a site-wide expiry weeks later.

Once TLS is in front, three settings change together: `SERVER_URL` becomes the
`https://` hostname (magic links are built from it), `SERVER_BIND` becomes
`127.0.0.1`, and `SERVER_FORWARD_HEADERS` becomes `NATIVE`. Ports 80 and 443 must
be open to the internet — 80 carries the certificate validation — and 7777 should
be closed, since it is now reachable only from the proxy on loopback.

### Bootstrapping the first admin

Admin is a flag on the `user` table. Two ways to get the first one:

- Set `SUPER_ADMIN_EMAIL` to your address, log in, and use the toggle in the UI.
- Or set the flag directly: `UPDATE "user" SET admin = true WHERE email = '...';`

Leaving `SUPER_ADMIN_EMAIL` empty in steady state is the safer default.

## Running Locally

```bash
cp .env.example .env   # fill in your values
docker compose up -d   # start the database
./run.sh               # run the app (development)
./run.sh production    # run the app (production)
```

The app will be at `http://localhost:7777`.

Use `run.sh` rather than `./gradlew bootRun` directly: Spring Boot resolves
`${VAR}` placeholders from real environment variables and does **not** read
`.env`, so `.env` has to be exported before the JVM starts. `run.sh` does that.

## Project Structure

```
grails-app/
├── controllers/    # Auth, kudos, users (thin, logic lives in services)
├── domain/         # User, Kudos, KudosReset, Feeling, LoginToken, SelfEsteemMessage
├── services/       # All business logic: kudos, login, email, feeling
├── jobs/           # Quartz jobs (weekly email + reset)
├── views/          # Server-rendered GSP templates
└── conf/           # App config, interceptors

src/main/groovy/    # Session configuration
src/main/resources/ # Liquibase DB changelogs
```

All Groovy sources live in the `kudos` package.
