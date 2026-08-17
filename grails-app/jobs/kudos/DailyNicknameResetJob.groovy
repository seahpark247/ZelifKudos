package kudos

import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j
class DailyNicknameResetJob {

    ChatService chatService

    // Quartz reads this static block, so grailsApplication cannot be injected —
    // Holders is the way to reach config from here.
    static triggers = {
        cron name: 'dailyNicknameResetTrigger',
             cronExpression: Holders.config.getProperty('app.schedule.nicknameResetCron', String, '0 0 0 * * ?'),
             timeZone: TimeZone.getTimeZone(
                 Holders.config.getProperty('app.schedule.timeZone', String, 'UTC'))
    }

    def execute() {
        log.info("========== Daily nickname reset job triggered ==========")
        chatService.deleteAllNicknames()
        log.info("========== Daily nickname reset job completed ==========")
    }
}
