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
        true
    }
}
