package kudos

import grails.core.GrailsApplication
import groovy.util.logging.Slf4j

@Slf4j
class BootStrap {

    GrailsApplication grailsApplication

    def init = {
        // Log the resolved schedule so a wrong timezone is caught at boot rather
        // than a week later when the email lands an hour off.
        log.info("Schedule: timeZone={} weeklyEmail='{}'",
                 grailsApplication.config.getProperty('app.schedule.timeZone'),
                 grailsApplication.config.getProperty('app.schedule.weeklyEmailCron'))

        LoginToken.withNewSession {
            LoginToken.withTransaction {
                LoginToken.executeUpdate("delete from LoginToken where expiryDate < :now", [now: new Date()])
            }
        }

    }

    def destroy = {
    }

}
