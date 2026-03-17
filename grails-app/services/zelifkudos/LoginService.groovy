package zelifkudos

import grails.core.GrailsApplication
import grails.gorm.transactions.NotTransactional
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper

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
        def message = javaMailSender.createMimeMessage()
        def helper = new MimeMessageHelper(message, true)
        helper.setTo(email)
        helper.setSubject("ZelifKudos Login Link")
        helper.setFrom(grailsApplication.config.getProperty('spring.mail.username'))
        helper.setText("""
            <p>Hello!</p>

            <p>Click the link below to login:</p>

            <a href="${loginLink}">
            Login to ZelifKudos
            </a>

            <p>This link expires in 15 minutes.</p>
            """, true)
        javaMailSender.send(message)
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
     * Check if a token has been verified (called by polling).
     * Returns the User if verified, null otherwise.
     */
    User checkTokenVerified(String token) {
        LoginToken lt = LoginToken.findByToken(token)

        if (!lt || lt.expiryDate < new Date() || !lt.verified) {
            return null
        }

        User user = findOrCreateUser(lt.email)
        lt.delete()
        return user
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
