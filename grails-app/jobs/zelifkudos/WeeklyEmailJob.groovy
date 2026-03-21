package zelifkudos

import groovy.util.logging.Slf4j

@Slf4j
class WeeklyEmailJob {

    WeeklyEmailService weeklyEmailService

    static triggers = {
        // Every Friday at 11:30 PM Central Time
        cron name: 'weeklyEmailTrigger', cronExpression: "0 30 23 ? * FRI", timeZone: TimeZone.getTimeZone('America/Chicago')
    }

    def execute() {
        log.info("========== Weekly email job triggered ==========")
        weeklyEmailService.sendWeeklyEmails()
        log.info("========== Weekly email job completed ==========")
    }
}
