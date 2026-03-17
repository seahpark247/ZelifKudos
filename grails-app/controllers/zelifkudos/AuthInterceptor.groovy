package zelifkudos

class AuthInterceptor {

    AuthInterceptor() {
        matchAll().excludes(controller: 'login')
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
