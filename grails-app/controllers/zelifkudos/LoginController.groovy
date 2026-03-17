package zelifkudos

import grails.converters.JSON

class LoginController {

    static allowedMethods = [sendLink: 'POST', checkToken: 'POST']

    LoginService loginService

    def index() {
        if (session.userId) {
            redirect(controller: "user", action: "list")
            return
        }
    }

    def sendLink() {
        String email = params.email

        if (!email?.endsWith("@zelifcam.net")) {
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
        String loginLink = g.createLink(controller: "login", action: "verify", absolute: true) + "?token=${token}"

        try {
            loginService.sendLoginEmail(email, loginLink)
        } catch (Exception e) {
            log.error("Failed to send login email to ${email}", e)
            flash.error = "Failed to send email. Please try again."
            redirect(action: "index")
            return
        }

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

        User user = loginService.checkTokenVerified(token)

        if (user) {
            session.userId = user.id
            session.removeAttribute("pendingToken")
            render([status: "verified"] as JSON)
        } else {
            render([status: "pending"] as JSON)
        }
    }

    def verify() {
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
