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
        int myKudosCount = kudosCounts[currentUser.id] ?: 0
        [users: users, kudosCounts: kudosCounts, isAdmin: currentUser.admin, currentUserId: currentUser.id, myKudosCount: myKudosCount]
    }

    def index() { redirect(action: 'list') }
}
