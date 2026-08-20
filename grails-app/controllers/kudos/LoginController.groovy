package kudos

import grails.converters.JSON
import grails.core.GrailsApplication

class LoginController {

    static allowedMethods = [sendLink: 'POST', checkToken: 'POST', confirm: 'POST']

    LoginService loginService
    GrailsApplication grailsApplication

    def index() {
        if (session.userId) {
            redirect(controller: "user", action: "list")
            return
        }
        [emailDomain: grailsApplication.config.getProperty('app.emailDomain')?.trim()?.toLowerCase()]
    }

    def sendLink() {
        // Normalise before any lookup. Postgres compares strings case-sensitively,
        // so "Name@example.com" and "name@example.com" would otherwise be two
        // different people and findOrCreateUser would happily create the duplicate.
        String email = params.email?.trim()?.toLowerCase()
        // Lowercase the domain too — a mixed-case COMPANY_EMAIL_DOMAIN would
        // otherwise never match the normalised address and lock everyone out.
        String allowedDomain = grailsApplication.config.getProperty('app.emailDomain')?.trim()?.toLowerCase()

        if (!allowedDomain) {
            log.error("app.emailDomain is not configured - set COMPANY_EMAIL_DOMAIN")
            flash.error = "Login is not configured yet. Please contact your administrator."
            redirect(action: "index")
            return
        }

        if (!email?.endsWith("@${allowedDomain}") && !User.findByEmail(email)) {
            flash.warning = "Please use our company email!"
            redirect(action: "index")
            return
        }

        if (loginService.hasRecentToken(email)) {
            flash.warning = "A login link was already sent. Please wait a moment before trying again."
            redirect(action: "index")
            return
        }

        String token = loginService.createLoginToken(email)
        String verifyUrl = g.createLink(controller: "login", action: "verify", absolute: true)
        String loginLink = "${verifyUrl}?token=${token}"

        // Base URL only, never the token. An emailed link is immutable once sent,
        // so if serverURL is wrong every link is silently dead — this makes that
        // visible instead of looking like a stuck waiting page.
        log.info("Login link for ${email} points at ${verifyUrl}")

        // Fire and forget. The SMTP round trip costs seconds and nothing here
        // depends on it: the token is stored and the waiting page polls. A failed
        // send deletes the token, which the poll reports as an expired link.
        loginService.sendLoginEmailAsync(email, loginLink, token)

        session.pendingToken = token
        redirect(action: "waiting")
    }

    def waiting() {
        if (!session.pendingToken) {
            redirect(action: "index")
            return
        }
        [email: LoginToken.findByToken(session.pendingToken)?.email]
    }

    def checkToken() {
        String token = session.pendingToken

        if (!token) {
            render([status: "no_token"] as JSON)
            return
        }

        Map result = loginService.pollToken(token)

        if (result.status == 'verified') {
            session.userId = result.user.id
            session.removeAttribute("pendingToken")
        } else if (result.status == 'expired') {
            // Drop it so a reload lands on the login form instead of a waiting
            // page that can never finish.
            session.removeAttribute("pendingToken")
        }

        render([status: result.status] as JSON)
    }

    /**
     * The page the emailed link opens. It shows a button and nothing else.
     *
     * Logging in from this GET handed the session to whatever opened the mail
     * first: scanners fetch every URL in a message during delivery, which spent
     * the link before it reached the inbox and left the scanner authenticated.
     * A GET now changes nothing; the POST behind the button does the work.
     */
    def verify() {
        LoginToken lt = loginService.peekToken(params.token)

        if (!lt) {
            flash.error = "Invalid or expired token"
            redirect(action: "index")
            return
        }

        [token: lt.token, email: lt.email]
    }

    def confirm() {
        User user = loginService.markTokenVerified(params.token)

        if (!user) {
            flash.error = "Invalid or expired token"
            redirect(action: "index")
            return
        }

        session.userId = user.id
        redirect(controller: "user", action: "list")
    }
}
