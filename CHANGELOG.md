# Changelog

All notable changes to Kudos will be documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/).
1. Added
2. Changed
3. Deprecated
4. Removed
5. Fixed
6. Security

## [4.4] - 2026-08-20

### Added
- Serve over HTTPS behind a TLS-terminating proxy; `deploy/Caddyfile` holds the config.
- Add `SERVER_BIND` to close the plain-HTTP port to everything but the proxy.
- Add `SERVER_FORWARD_HEADERS` to honour `X-Forwarded-*` only behind a trusted proxy.
- Give the demo a Badges page and its badge panel, so the tour no longer skips them.

### Changed
- Make the weekly email's footer link say what it does instead of printing the URL.
- Tell people to check their inbox on the waiting page, rather than claiming to check it for them.
- Hide the menu bar and unlink Start until you are signed in; from the waiting page they only led away from it.
- Focus OK on the new-badge dialog, so Enter dismisses it.

### Removed
- Drop the hourglass emoji from the login waiting page; the progress bar already says it.

### Fixed
- Stop the login email claiming the app is reachable only from the company network.
- Point the README's admin bootstrap SQL at `app_user`, the table that exists.

### Security
- Stop a plain GET from spending a magic link, so a mail scanner reaching the new
  public hostname can no longer sign the recipient in before they click.
- Log requests in Caddy, so who opened a login link is answerable.
- Ignore `.env.bak.*` so a backup of `.env` cannot be committed.
- Cap what one `/demo` session accumulates; the page takes no login.

## [4.3] - 2026-08-19

### Added
- Surface badges in the app: a panel beside the roster, and a modal when one lands.
- Seed the weekly email's rotating messages.

### Changed
- Rename the Cofounder badge to Founder, and `COFOUNDER_EMAILS` to `FOUNDER_EMAILS`.

### Removed
- Drop jQuery and Bootstrap.

## [4.2] - 2026-08-17

### Added
- Add badges: nine collectible badges on their own page, locked ones greyed out.
- Add `COFOUNDER_EMAILS` to assign the Cofounder badge.

### Fixed
- Reject path traversal in the PWA icon route.

## [4.1] - 2026-08-17

### Removed
- Remove the Water Cooler chat, animal nicknames, and the nightly nickname reset job.
- Remove `CHAT_COOLDOWN_MS`, `CHAT_DUPLICATE_WINDOW_MS`, and `NICKNAME_RESET_CRON`.
- Drop the SockJS and STOMP CDN dependency.

### Changed
- Make the kudos message and status fields resizable.

## [4.0] - 2026-08-17

### Added
- Add Liquibase baseline so an empty database builds its whole schema.
- Add Liquibase changeset for the Spring Session tables.
- Add `APP_NAME`, `APP_TIMEZONE`, `COMPANY_EMAIL_DOMAIN`, `SUPER_ADMIN_EMAIL`, `SERVER_URL`, and `SERVER_PORT`.
- Add an `<app:name/>` tag so the product name lives in one place.
- Add `run.sh`, `serve.sh`, and a systemd unit.

### Changed
- Rename the Groovy package to `kudos`.
- Replace the PWA and favicon icons.
- Default the schedule timezone to UTC.
- Move the app to `SERVER_PORT` (default 7777).

### Fixed
- Fix Spring Session JDBC never engaging, which logged everyone out on every restart.
- Fix the waiting page spinning forever on a magic link that could never verify.
- Fix the weekly email footer linking to a hardcoded host.
- Fix the root logger hiding every log line the app writes.
- Fix the login email being sent inside the request, costing 3.2s per attempt.
- Fix scheduled jobs running an hour off from the intended timezone.
- Fix case-sensitive config comparisons failing silently on mixed case.
- Refuse login outright when no email domain is configured.

## [3.4] - 2026-05-01

### Added
- Daily chat nickname reset at midnight.

### Changed
- Hide deactivated users from the main user list.
- Make feeling bubbles persist until the user changes them.
- Edit email recipient scope.

## [3.3] - 2026-04-06

### Fixed
- Fix PWA support to work in production.

## [3.2] - 2026-04-06

### Added
- Add PWA support with a pixel-art icon.
- Add random animal nicknames with colors to chat.
- Reset thought bubbles on weekly email and manual reset.

### Changed
- Simplify login flow.
- Rename chat room to "Random Chat".
- Change timestamp format to MM-DD-YYYY HH:mm.
- Change thought bubble placeholder to "What's on your mind?"

### Fixed
- Fix chat room width.

## [3.1] - 2026-03-28

### Fixed
- Fix liquibase bug and websocket bug in production.

## [3.0] - 2026-03-28

### Added
- Add thought bubble feature for setting a short status message.
- Add Water Cooler anonymous chat room with WebSocket support.
- Implement database migration tool, liquibase.

### Fixed
- Fix kudos reset flag bug.

## [2.4] - 2026-03-22

### Added
- Add automatic reset feature of user list sorting.

## [2.3] - 2026-03-20

### Added
- Show client local time.
- Secured URL as using https.

### Changed
- Update no Kudos week email text.
- Update email sending schedule job using Quartz plugin.

## [2.2] - 2026-03-18

### Fixed
- Remove dead code.

## [2.1] - 2026-03-18

### Changed
- Rename My Kudos label: "received" → "total".
- Move "Send kudos to climb the ranks!" to same line as Reset button.
- Use Windows 98 style bullet (▪) with single-line truncation for messages.
- Change My Kudos sender from "Someone sent you kudos" to "Anonymous".

### Fixed
- Store self-esteem messages in database.
- Limit recent messages on Users page to 3 (was 5), remove "and N more...".
- Hide messages section when there are none.
- Hide empty white box when user has kudos but no messages for better UX.
- Fix date column getting squished by long messages in History and My Kudos.

## [2.0] - 2026-03-18

### Added
- Add My Kudos page: see all kudos you've received with pagination and reset dividers.
- Show recent messages preview on Users page (up to 5).
- Add "Send kudos to climb the ranks!" subtitle on Employee Roster.
- Highlight your row in yellow on the list.
- Set up weekly email service.
- Add top receivers lookup and per-user kudos count/messages.

### Changed
- Rename menu: Kudos → History, add My Kudos tab.
- Allow reset to happen automatically (not just by admin).
- Bump version to 2.0.

## [1.5] - 2026-03-17

### Added
- Show "You received N kudos this week!" notification on login.
- Save login session to database (survives server restart).
- Support direct login link.

### Changed
- Sort user list by most kudos sent.
- Show different icons for info, warning, and error messages.
- Disable automatic database changes in production.
- Set admin in database instead of auto-detecting.
- Clean up error handling code.
- Combine duplicate login checks into one place.
- Remove unused CSS and empty test files.

## [1.4] - 2026-03-17

### Added
- Track whether user has logged in before (activated status).
- Show better error when hitting kudos send limit.

## [1.3] - 2026-03-16

### Added
- Add optional message when sending kudos.
- Limit to 5 kudos per person per day with 10-minute cooldown.
- Add active button styling.

### Changed
- Extend login session to 30 days.
- Go straight to user list after login (remove success page).

## [1.2] - 2026-03-14

### Fixed
- Fix name capitalization.

## [1.1] - 2026-03-14

### Changed
- Show kudos counts to admins only.
- Update admin user list.
- Capitalize user names.

## [1.0] - 2026-03-13

### Added
- Initial release.
- Add email magic link login (company email domain only).
- Send kudos to coworkers.
- Add admin panel with kudos reset.
- Use Windows 98 retro theme.
- Set up PostgreSQL 16 with Docker Compose.
- Deploy on Oracle Cloud VM.
