# Changelog

All notable changes to ZelifKudos will be documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/).

## [1.5] - 2026-03-17

### Added
- Kudos count notification for logged-in users ("You received N kudos this week!")
- Login session persistence to database
- Direct login link support for better UX

### Changed
- User list sorted by total kudos sent (most active users first)
- Flash messages now use distinct icons by severity (`i` info, `!` warning, `✕` error)
- Disabled automatic schema updates (`dbCreate: none`) in production for safety
- Removed admin auto-detection logic; admin is now managed via database seeding

### Refactored
- Moved `KudosLimitException` to `src/main/groovy` to prevent service bean scanning
- Changed `KudosLimitException` to checked exception to avoid unnecessary transaction rollback
- Made `checkLimit` method private
- Renamed `resetAllKudos` to `markKudosReset` to reflect actual behavior
- Consolidated user validation into `AuthInterceptor` to eliminate duplicate null checks
- Removed unused Bootstrap, Bootstrap Icons, and grails CSS imports
- Removed auto-generated test stubs with no real test logic

## [1.4] - 2026-03-17

### Added
- User `activated` field to track first login status
- Users seeded via database with `activated = false` until first login
- `KudosLimitException` for structured rate limit error handling

## [1.3] - 2026-03-16

### Added
- Optional message when sending kudos
- Rate limiting: 5 kudos per person per day with 10-minute cooldown
- Active button styling

### Changed
- Extended login session to 30 days
- Simplified login flow: removed login success page, direct redirect to user list

## [1.2] - 2026-03-14

### Fixed
- Additional user name capitalization fix

## [1.1] - 2026-03-14

### Changed
- Restricted kudos count visibility to admins only
- Updated admin user list
- Capitalized user names in views

## [1.0] - 2026-03-13

### Added
- Initial release
- Email-based magic link authentication (restricted to `@zelifcam.net`)
- Send kudos to coworkers
- Admin panel with kudos reset functionality
- Windows 98 retro theme UI
- PostgreSQL 16 database with Docker Compose
- Deployed on Oracle Cloud VM
