package kudos

class AuthInterceptor {

    AuthInterceptor() {
        matchAll().excludes(controller: 'login').excludes(controller: 'pwa').excludes(controller: 'demo').excludes(uri: '/ws/**')
    }

    boolean before() {
        if (!session.userId) {
            redirect(controller: 'login')
            return false
        }

        User currentUser = User.get(session.userId)
        if (!currentUser) {
            session.invalidate()
            redirect(controller: 'login')
            return false
        }

        request.currentUser = currentUser
        true
    }
}
