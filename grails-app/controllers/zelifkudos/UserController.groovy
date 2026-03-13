package zelifkudos

class UserController {

    KudosService kudosService

    def list() {
        List<User> users = User.list()
        Map<Long, Integer> kudosCounts = kudosService.countKudosForAllUsers()
        User currentUser = User.get(session.userId)
        if (!currentUser) {
            session.invalidate()
            redirect(controller: "login")
            return
        }
        [users: users, kudosCounts: kudosCounts, isAdmin: currentUser.admin]
    }

    def index() { redirect(action: 'list') }
}
