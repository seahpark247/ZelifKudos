# Changelog

All notable changes to Kudos will be documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/).
1. Added
2. Changed
3. Deprecated
4. Removed
5. Fixed
6. Security

## [4.1] - 2026-08-17

### Removed
- Remove the Water Cooler chat: room, WebSocket transport, animal nicknames and the nightly nickname reset job. The team already lives in Teams, so a second chat with no notifications was never going to reach the critical mass a chat needs, and it carried the largest share of the code for the smallest return.
- Remove `CHAT_COOLDOWN_MS`, `CHAT_DUPLICATE_WINDOW_MS` and `NICKNAME_RESET_CRON`.
- Drop the jsdelivr CDN dependency that came with SockJS and STOMP; the app no longer loads anything from a third-party host.

### Changed
- Stop the weekly and manual kudos resets clearing chat history, which no longer exists.

## [4.0] - 2026-08-17

### Added
- Add Liquibase baseline for the five tables that predated Liquibase adoption (`app_user`, `kudos`, `kudos_reset`, `login_token`, `self_esteem_message`). A fresh database now builds its whole schema.
- Add Liquibase changeset for the Spring Session tables, which `initialize-schema: never` never created.
- Add `APP_NAME`, `COMPANY_EMAIL_DOMAIN`, `SUPER_ADMIN_EMAIL`, `SERVER_URL`, and `SERVER_PORT` environment variables.
- Add an `<app:name/>` tag so the product name lives in one place instead of being spelled out across nine views, the manifest and two email templates.
- Add `run.sh`, which exports `.env` before starting the app (Spring Boot does not read `.env` itself).

### Changed
- Rename the Groovy package to `kudos`.
- Read the allowed login email domain from config instead of hardcoding it.
- Read the admin address from config instead of hardcoding it.
- Read the production `serverURL` from `SERVER_URL` instead of hardcoding a host.
- Replace the PWA and favicon icons.
- Run the baseline before 2.5 in `changelog-master`, so the `feeling` → `app_user` foreign key actually applies.
- Rewrite README: document every environment variable and how to bootstrap the first admin.

### Fixed
- Refuse login outright when no email domain is configured, instead of silently degrading to existing-users-only.
- Stop the weekly email footer linking to a hardcoded host; it now uses `serverURL`, and is omitted when that is unset.
- Serve the app on `SERVER_PORT` (default 7777) instead of hardcoded 8080, so it no longer fights other projects for the port.

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
