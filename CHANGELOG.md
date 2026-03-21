# Changelog

All notable changes to ZelifKudos will be documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/).

## [2.3] - 2026-03-20

### Added
- show client local time.
- secured URL as using https.

### Changed
- update no Kudos week email text.
- update email sending schedule job using Quartz plugin.

## [2.2] - 2026-03-18

### Fixed
- remove dead code.

## [2.1] - 2026-03-18

### Fixed
- Store self-esteem messages in database.
- Limit recent messages on Users page to 3 (was 5), remove "and N more...".
- Hide messages section when there are none.
- Hide empty white box when user has kudos but no messages for better UX.
- Fix date column getting squished by long messages in History and My Kudos.

### Changed
- Rename My Kudos label: "received" → "total".
- Move "Send kudos to climb the ranks!" to same line as Reset button.
- Use Windows 98 style bullet (▪) with single-line truncation for messages.
- Change My Kudos sender from "Someone sent you kudos" to "Anonymous".

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

### Refactored
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
- Add email magic link login (@zelifcam.net only).
- Send kudos to coworkers.
- Add admin panel with kudos reset.
- Use Windows 98 retro theme.
- Set up PostgreSQL 16 with Docker Compose.
- Deploy on Oracle Cloud VM.
