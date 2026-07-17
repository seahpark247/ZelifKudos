# Zelif Kudos

I genuinely love our team I work with, a.k.a my Zelifgangs.

Every day, I see my colleagues putting in real effort, helping each other out, going the extra mile, and making work a better place to be. I wanted a simple way to say "hey, I noticed, and I appreciate you." That's why I built this app.

Whenever someone lends you a hand, brightens your day, or does something worth celebrating, send them a kudo. It takes five seconds, but it can make someone's whole day.

Here's to a team that lifts each other up. Let's keep it going.

With love,
Seah

<img width="1470" height="835" alt="Screenshot 2026-03-28 at 7 20 23 PM" src="https://github.com/user-attachments/assets/e03c2e4e-4730-4333-9066-7ecd4d9758d8" />
<details>
<summary>Screenshots</summary>
<img width="648" height="479" alt="Screenshot 2026-03-18 at 4 37 57 PM" src="https://github.com/user-attachments/assets/5da13304-b634-4ffc-ac3e-7812444d3cb9" />
<img width="647" height="389" alt="Screenshot 2026-03-18 at 4 42 14 PM" src="https://github.com/user-attachments/assets/167e69e6-03f2-4619-b883-c7586c8536a7" />
<img width="646" height="250" alt="Screenshot 2026-03-18 at 4 42 24 PM" src="https://github.com/user-attachments/assets/e1eeb352-4779-44e6-8e67-0ac32f002161" />
</details>

## Overview

A peer recognition app I built solo for my team. From idea to production, designed, coded, and deployed by me. Running live with real users.

Every week, teammates send each other kudos. On Friday evening, everyone gets a personalized email with the week's top stars and messages they received. Then the counter resets and a new week starts.

## Why I Built It

Verbal praise gets forgotten. Slack messages disappear. I wanted a simple way to say "I noticed you, and I appreciate you." Something that sticks around and actually feels good to use.

Nothing out there did what I wanted, so I made it myself.

## How It Works

**Login**: No passwords. You enter your company email, get a magic link, click it, you're in. The link expires in 15 minutes. Only `@zelifcam.net` emails are allowed, so authentication and access control happen in one step.

**Kudos**: Pick a teammate, write an optional message, send. There's a daily limit (5 per person) and a cooldown (10 min) to keep things genuine.

**Chat**: Real-time team chat via WebSocket. Messages are rate-limited (3-second cooldown, duplicate blocking) to prevent spam.

**Weekly Email**: Every Friday at 6 PM, a Quartz job sends each person an HTML email with:
- Top 3 stars of the week (dense ranking, ties share the same spot)
- How many kudos you got and what people said
- A rotating self-esteem message

The system sends all emails first, then resets only if every email succeeds. If one fails, nothing resets. No one gets skipped.

**Weekly Reset**: Every reset is logged with who triggered it (the system on Fridays, or an admin manually). Full audit trail.

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│   Browser   │────>│  Controller  │────>│   Service   │
│  (GSP+JS)   │<────│ + Interceptor│<────│ (@Transact) │
└──────┬──────┘     └──────────────┘     └──────┬──────┘
       │                                        │
       │ WebSocket                    GORM/HQL  │
       │ (SockJS+STOMP)                         │
       v                                        v
┌─────────────┐                        ┌─────────────┐
│  ChatWS     │──────────────────────> │ PostgreSQL  │
│  Controller │     ChatService        │ + Liquibase │
└─────────────┘                        │ + Session   │
                                       └─────────────┘
       ┌─────────────┐
       │   Quartz    │──> WeeklyEmailService ──> SMTP
       │ (FRI 18:00) │
       └─────────────┘
```

## Tech Stack

Grails 6 (Groovy) on Spring Boot 2, PostgreSQL 16, WebSocket (SockJS + STOMP) for real-time chat, Quartz for scheduled jobs, Liquibase for DB migrations, Spring Session JDBC so sessions survive deploys.

Deployed on an Oracle Cloud Always Free VM via Docker. Total hosting cost: **$0/month**.

The UI is a Windows 98 retro theme, 800+ lines of pure CSS. Draggable chat window, mobile-responsive. People used it because it was fun.

## What I Learned

- **Passwordless auth is underrated.** Simpler than password hashing + reset flows, better UX, and more secure for a small team
- **Rate limiting belongs at the right layer.** HTTP limits can't catch WebSocket spam, service limits can't catch duplicate chat messages. Each defense has to sit where it can see the problem
- **Dense ranking has edge cases.** "Top 3" means top 3 rank levels, not top 3 people. 5 people tied for 1st? That's 5 gold medals and no silver
- **Free-tier constraints force good decisions.** 256MB heap limit meant no waste. Spring Session JDBC solved "deploy kills all sessions" without needing Redis

## Running Locally

```bash
cp .env.example .env   # fill in your values
docker compose up -d   # start the database
./gradlew bootRun      # run the app
```

The app will be at `http://localhost:8080`.

## Project Structure

```
grails-app/
├── controllers/    # Auth, kudos, chat, users (thin, logic lives in services)
├── domain/         # 7 entities: User, Kudos, ChatMessage, Feeling, LoginToken, ...
├── services/       # All business logic: kudos, login, email, chat, feeling
├── jobs/           # Quartz jobs (weekly email + reset)
├── views/          # Server-rendered GSP templates
└── conf/           # App config, interceptors

src/main/groovy/    # WebSocket config, chat controller, auth interceptor
src/main/resources/ # Liquibase DB changelogs
```
