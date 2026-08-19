package kudos

import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j
class WeeklyEmailJob {

    WeeklyEmailService weeklyEmailService

    // Quartz reads this static block, so grailsApplication cannot be injected —
    // Holders is the way to reach config from here.
    static triggers = {
        cron name: 'weeklyEmailTrigger',
             cronExpression: Holders.config.getProperty('app.schedule.weeklyEmailCron', String, '0 0 18 ? * FRI'),
             timeZone: TimeZone.getTimeZone(
                 Holders.config.getProperty('app.schedule.timeZone', String, 'UTC'))
    }

    def execute() {
        log.info("========== Weekly email job triggered ==========")
        weeklyEmailService.sendWeeklyEmails()
        log.info("========== Weekly email job completed ==========")
    }
}
