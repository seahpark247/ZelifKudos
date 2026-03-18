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

        // Self-esteem message from DB (cycles through messages by reset count)
        int resetCount = KudosReset.count()
        int totalMessages = SelfEsteemMessage.count() ?: 1
        SelfEsteemMessage sem = SelfEsteemMessage.findBySortOrder((int)(resetCount % totalMessages))
        String selfEsteemMessage = sem?.message ?: "You are awesome!"

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
