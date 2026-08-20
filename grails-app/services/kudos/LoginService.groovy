package kudos

import grails.core.GrailsApplication
import grails.gorm.transactions.NotTransactional
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async

@Transactional
class LoginService {

    @Autowired
    JavaMailSender javaMailSender

    GrailsApplication grailsApplication

    boolean hasRecentToken(String email) {
        LoginToken.findByEmailAndDateCreatedGreaterThan(
            email, new Date(System.currentTimeMillis() - 2 * 60 * 1000)) != null
    }

    String createLoginToken(String email) {
        // Clean up expired tokens
        LoginToken.executeUpdate("delete from LoginToken where expiryDate < :now", [now: new Date()])

        String token = UUID.randomUUID().toString()
        Date expiry = new Date(System.currentTimeMillis() + 15 * 60 * 1000)
        new LoginToken(email: email, token: token, expiryDate: expiry).save(failOnError: true)
        return token
    }

    @NotTransactional
    void sendLoginEmail(String email, String loginLink) {
        String appName = grailsApplication.config.getProperty('app.displayName') ?: 'Kudos'
        def message = javaMailSender.createMimeMessage()
        def helper = new MimeMessageHelper(message, true)
        helper.setTo(email)
        helper.setSubject("${appName} Login Link")
        helper.setFrom(grailsApplication.config.getProperty('spring.mail.username'))
        helper.setText("""
            <p>Hello!</p>

            <p>Click the link below to login:</p>

            <a href="${loginLink}">
            Login to ${appName}
            </a>

            <p>This link expires in 15 minutes.</p>
            """, true)
        javaMailSender.send(message)
    }

    /**
     * Send the login email off the request thread.
     *
     * A full SMTP transaction to an external provider costs seconds — measured
     * at ~3.2s against Gmail — and nothing depends on it finishing: the token is
     * already stored and the waiting page polls for verification.
     *
     * If the send fails the token is deleted, so the waiting page reports an
     * expired link within one poll instead of spinning for 15 minutes.
     */
    @Async
    @NotTransactional
    void sendLoginEmailAsync(String email, String loginLink, String token) {
        try {
            sendLoginEmail(email, loginLink)
            log.info("Login email sent to ${email}")
        } catch (Exception e) {
            log.error("Failed to send login email to ${email} — dropping token", e)
            LoginToken.withNewSession {
                LoginToken.withTransaction {
                    LoginToken.findByToken(token)?.delete(flush: true)
                }
            }
        }
    }

    /** Is this token still usable? Read-only — see LoginController.verify. */
    @Transactional(readOnly = true)
    LoginToken peekToken(String token) {
        LoginToken lt = token ? LoginToken.findByToken(token) : null
        (lt && lt.expiryDate > new Date()) ? lt : null
    }

    /**
     * Mark token as verified (called when user clicks email link).
     * Returns the User if valid, null otherwise.
     */
    User markTokenVerified(String token) {
        LoginToken lt = LoginToken.findByToken(token)

        if (!lt || lt.expiryDate < new Date()) {
            return null
        }

        lt.verified = true
        lt.save(failOnError: true)

        return findOrCreateUser(lt.email)
    }

    /**
     * Poll a pending magic link (called by the waiting page).
     *
     * Returns one of:
     *   [status: 'verified', user: User]  the link was clicked
     *   [status: 'pending']               still waiting on the click
     *   [status: 'expired']               the link timed out, or is already gone
     *
     * 'expired' and 'pending' have to stay distinguishable: collapsing them
     * leaves the waiting page spinning forever on a link that can never verify.
     */
    Map pollToken(String token) {
        LoginToken lt = LoginToken.findByToken(token)

        if (!lt || lt.expiryDate < new Date()) {
            return [status: 'expired']
        }

        if (!lt.verified) {
            return [status: 'pending']
        }

        User user = findOrCreateUser(lt.email)
        lt.delete()
        return [status: 'verified', user: user]
    }

    private User findOrCreateUser(String email) {
        User user = User.findByEmail(email)
        if (!user) {
            String name = email.split("@")[0]
            user = new User(email: email, name: name, activated: true).save(failOnError: true)
        } else if (!user.activated) {
            user.activated = true
            user.save(failOnError: true)
        }
        return user
    }
}
