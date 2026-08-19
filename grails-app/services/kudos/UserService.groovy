package kudos

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional

@Transactional
class UserService {

    GrailsApplication grailsApplication

    /**
     * The one account allowed to toggle its own admin flag. Compared
     * case-insensitively: stored emails are normalised to lowercase, so a
     * mixed-case SUPER_ADMIN_EMAIL would silently never match.
     */
    boolean isSuperAdmin(User user) {
        String superAdmin = grailsApplication.config.getProperty('app.superAdminEmail')?.trim()
        superAdmin && user?.email?.equalsIgnoreCase(superAdmin)
    }

    void toggleAdmin(User user) {
        user.admin = !user.admin
        user.save(failOnError: true)
    }
}
