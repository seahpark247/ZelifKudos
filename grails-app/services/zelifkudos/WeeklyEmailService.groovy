package zelifkudos

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Scheduled
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class WeeklyEmailService {

    static final List<String> MESSAGES = [
        "You know, you are amazing!",
        "The world is better because you're in it.",
        "You make a difference, every single day.",
        "Someone out there is grateful for you right now.",
        "You are enough, exactly as you are.",
        "Your kindness matters more than you know.",
        "Keep going — you're doing great things.",
        "You bring out the best in people around you.",
        "Don't forget: you're somebody's reason to smile.",
        "You are stronger than you think.",
        "Your hard work doesn't go unnoticed.",
        "You deserve every good thing coming your way.",
        "The effort you put in truly matters.",
        "You light up every room you walk into.",
        "Be proud of how far you've come.",
        "You inspire people without even trying.",
        "Your presence makes the team better.",
        "You are doing better than you think you are.",
        "Never underestimate your impact on others.",
        "You are worthy of all the good things in life.",
        "Your smile can change someone's whole day.",
        "You handle tough things with grace.",
        "The best is yet to come for you.",
        "You are a gift to everyone around you.",
        "Your energy is contagious — in the best way.",
        "You should be proud of yourself today.",
        "You bring so much value to everything you do.",
        "Someone looked up to you this week.",
        "You are braver than you believe.",
        "Your voice matters. Don't ever forget that.",
        "You turn ordinary moments into something special.",
        "You are exactly where you need to be.",
        "Great things are happening because of you.",
        "You have a heart of gold.",
        "The people around you are lucky to have you.",
        "You've already overcome so much — keep going.",
        "Your potential is limitless.",
        "You make hard things look easy.",
        "You are more loved than you realize.",
        "Every step you take is progress.",
        "You carry sunshine wherever you go.",
        "Your creativity and ideas matter.",
        "You are one of a kind — literally.",
        "The way you care about others is beautiful.",
        "You've got this. You always do.",
        "Your courage inspires those around you.",
        "You are a rockstar in ways you don't even see.",
        "Today is yours. Own it.",
        "You make the impossible feel possible.",
        "Your thoughtfulness never goes unnoticed.",
        "You are someone's favorite person.",
        "Keep being you — the world needs it.",
        "You radiate positivity.",
        "Your dedication is truly admirable.",
        "You have the power to make today amazing.",
        "People genuinely enjoy being around you.",
        "You are a walking reminder that good people exist.",
        "Your laugh is someone's favorite sound.",
        "You make even Mondays feel okay.",
        "You are built for greatness.",
        "The way you show up for others is remarkable.",
        "You leave things better than you found them.",
        "You are someone's answered prayer.",
        "Your patience is a superpower.",
        "You bring calm to the chaos.",
        "You deserve a standing ovation for this week.",
        "You are proof that good things take time.",
        "Your warmth makes cold days bearable.",
        "You have an incredible way of making people feel seen.",
        "You are tougher than any challenge ahead.",
        "Your positivity is a force of nature.",
        "You are the type of person the world needs more of.",
        "Everything you do has meaning.",
        "You turn setbacks into comebacks.",
        "Your spirit is unbreakable.",
        "You make people feel like they belong.",
        "You are a masterpiece in progress.",
        "Your generosity changes lives.",
        "You show up when it matters most.",
        "You are someone's hero — believe it.",
        "Your perspective is valuable and unique.",
        "You have a way of making everything better.",
        "You are the definition of resilience.",
        "Your compassion makes the world softer.",
        "You deserve all the kudos in the world.",
        "You are living proof that hard work pays off.",
        "Your optimism is inspiring.",
        "You make people want to be better.",
        "You are a breath of fresh air.",
        "Your story is far from over — the best chapters are ahead.",
        "You are worth celebrating every single day.",
        "Your kindness creates ripples you'll never see.",
        "You have an amazing ability to lift others up.",
        "You are the secret ingredient to this team.",
        "Your determination is something special.",
        "You make the world a little brighter just by existing.",
        "You are capable of extraordinary things.",
        "Your heart is your greatest strength.",
        "You are not just surviving — you are thriving.",
        "Never forget: you are absolutely, undeniably awesome.",
    ]

    @Autowired
    JavaMailSender javaMailSender

    GrailsApplication grailsApplication
    KudosService kudosService

    @Scheduled(cron = "0 0 18 ? * FRI", zone = "America/Chicago")
    void sendWeeklyEmails() {
        log.info("Starting weekly kudos email job")

        Date lastReset = kudosService.getLastResetDate() ?: new Date(0)
        List<Map> topReceivers = kudosService.getTopReceivers()
        Map<Long, Integer> kudosCounts = kudosService.countKudosForAllUsers()
        boolean anyKudos = topReceivers.size() > 0

        // Build top 3 ranking (up to 3 distinct rank levels, dense ranking)
        List<Map> top3 = buildTop3(topReceivers)

        // Self-esteem message based on reset count (cycles through 100 messages)
        int resetCount = KudosReset.count()
        String selfEsteemMessage = MESSAGES[(int)(resetCount % MESSAGES.size())]

        // Determine recipients: activated=true always, activated=false only if kudos >= 1
        List<User> allUsers = User.list()
        List<User> recipients = allUsers.findAll { user ->
            user.activated || (kudosCounts[user.id] ?: 0) > 0
        }

        String fromEmail = grailsApplication.config.getProperty('spring.mail.username')

        // Send emails, track failures
        List<User> failed = []
        for (User user : recipients) {
            try {
                int userKudos = kudosCounts[user.id] ?: 0
                List<String> messages = userKudos > 0
                    ? kudosService.getMessagesForUser(user.id, lastReset)
                    : []
                String html = buildEmailHtml(user, top3, userKudos, messages, anyKudos, selfEsteemMessage)
                sendEmail(fromEmail, user.email, "Your Weekly Kudos Report", html)
                log.info("Sent weekly email to ${user.email}")
            } catch (Exception e) {
                log.error("Failed to send weekly email to ${user.email}", e)
                failed << user
            }
        }

        // Retry failed emails once
        if (failed) {
            log.info("Retrying ${failed.size()} failed emails")
            List<User> stillFailed = []
            for (User user : failed) {
                try {
                    int userKudos = kudosCounts[user.id] ?: 0
                    List<String> messages = userKudos > 0
                        ? kudosService.getMessagesForUser(user.id, lastReset)
                        : []
                    String html = buildEmailHtml(user, top3, userKudos, messages, anyKudos, selfEsteemMessage)
                    sendEmail(fromEmail, user.email, "Your Weekly Kudos Report", html)
                    log.info("Retry succeeded for ${user.email}")
                } catch (Exception e) {
                    log.error("Retry failed for ${user.email}", e)
                    stillFailed << user
                }
            }
            if (stillFailed) {
                log.error("${stillFailed.size()} emails still failed after retry: ${stillFailed*.email}")
                return
            }
        }

        // All emails sent successfully — reset
        kudosService.markKudosReset(null)
        log.info("Weekly kudos reset complete (system)")
    }

    /**
     * Build top 3 ranking with dense ranking (shared ranks, up to 3 people-levels).
     * Returns list of maps: [rank: int, users: List<User>, count: int]
     */
    List<Map> buildTop3(List<Map> topReceivers) {
        if (!topReceivers) return []

        List<Map> result = []
        int currentRank = 1
        int peopleCount = 0

        int i = 0
        while (i < topReceivers.size() && peopleCount < 3) {
            int count = topReceivers[i].count
            List<User> usersAtRank = []

            // Gather all users with the same count
            while (i < topReceivers.size() && topReceivers[i].count == count) {
                usersAtRank << User.get(topReceivers[i].userId)
                i++
            }

            result << [rank: currentRank, users: usersAtRank, count: count]
            peopleCount += usersAtRank.size()
            currentRank++
        }

        return result
    }

    String buildEmailHtml(User recipient, List<Map> top3, int userKudos,
                          List<String> messages, boolean anyKudos, String selfEsteemMessage) {
        String siteUrl = "https://zelifkudos.ddnsking.com"
        StringBuilder sb = new StringBuilder()

        sb.append("""
<div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 500px; margin: 0 auto; background: #f9f9f9; border: 1px solid #ddd; border-radius: 8px; padding: 24px;">
    <h2 style="color: #333; margin-top: 0;">ZelifKudos Weekly Report</h2>
    <p style="color: #555;">Hey ${recipient.name.capitalize()}!</p>
""")

        if (anyKudos) {
            // Top Stars section
            sb.append("""
    <div style="background: #fff; border: 1px solid #e0e0e0; border-radius: 6px; padding: 16px; margin: 16px 0;">
        <h3 style="margin-top: 0; color: #444;">This Week's Top Stars</h3>
""")
            String[] medals = ["🥇", "🥈", "🥉"]
            for (Map entry : top3) {
                int rank = entry.rank as int
                String medal = rank <= 3 ? medals[rank - 1] : ""
                List<User> users = entry.users as List<User>
                String rankLabel = ordinal(rank)

                List<String> nameList = users.collect { User u ->
                    if (u.id == recipient.id) {
                        "<b>${u.name.capitalize()}</b>"
                    } else {
                        u.name.capitalize()
                    }
                }

                sb.append("        <p style=\"margin: 4px 0;\">${medal} ${rankLabel}: ${nameList.join(', ')}</p>\n")
            }

            sb.append("    </div>\n")

            // Your Week section (only if user has kudos)
            if (userKudos > 0) {
                sb.append("""
    <div style="background: #fff; border: 1px solid #e0e0e0; border-radius: 6px; padding: 16px; margin: 16px 0;">
        <h3 style="margin-top: 0; color: #444;">Your Week</h3>
        <p>You received <b>${userKudos}</b> kudos this week!</p>
""")
                if (messages) {
                    sb.append("        <p><b>Messages for you:</b></p>\n")
                    sb.append("        <ul style=\"padding-left: 20px;\">\n")
                    for (String msg : messages) {
                        sb.append("            <li style=\"margin: 4px 0; color: #555;\">&ldquo;${msg.encodeAsHTML()}&rdquo;</li>\n")
                    }
                    sb.append("        </ul>\n")
                }

                sb.append("    </div>\n")
            }
        } else {
            // No kudos this week
            sb.append("""
    <div style="background: #fff; border: 1px solid #e0e0e0; border-radius: 6px; padding: 16px; margin: 16px 0; text-align: center;">
        <p style="color: #888;">No kudos were sent this week.</p>
        <p style="color: #888;">Send some love next week!</p>
    </div>
""")
        }

        // Self-esteem message + footer
        sb.append("""
    <div style="text-align: center; margin-top: 24px; padding-top: 16px; border-top: 1px solid #e0e0e0;">
        <p style="color: #666; font-style: italic;">&ldquo;${selfEsteemMessage}&rdquo;</p>
        <p style="color: #555;">Have a great weekend!</p>
        <p style="color: #888; font-size: 13px;">with love, Seah</p>
        <a href="${siteUrl}" style="color: #4a90d9; font-size: 13px;">${siteUrl}</a>
    </div>
</div>
""")

        return sb.toString()
    }

    private void sendEmail(String from, String to, String subject, String html) {
        def message = javaMailSender.createMimeMessage()
        def helper = new MimeMessageHelper(message, true)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setFrom(from)
        helper.setText(html, true)
        javaMailSender.send(message)
    }

    private static String ordinal(int rank) {
        switch (rank) {
            case 1: return "1st"
            case 2: return "2nd"
            case 3: return "3rd"
            default: return "${rank}th"
        }
    }
}
