package zelifkudos

import groovy.util.logging.Slf4j

@Slf4j
class WeeklyEmailJob {

    WeeklyEmailService weeklyEmailService

    static triggers = {
        // Every Friday at 6:00 PM Central Time
        cron name: 'weeklyEmailTrigger', cronExpression: "0 0 18 ? * FRI", timeZone: TimeZone.getTimeZone('America/Chicago')
    }

    def execute() {
        log.info("========== Weekly email job triggered ==========")
        weeklyEmailService.sendWeeklyEmails()
        log.info("========== Weekly email job completed ==========")
    }
}
