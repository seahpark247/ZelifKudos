package zelifkudos

class UserController {

    KudosService kudosService

    def list() {
        Map<Long, Integer> kudosCounts = kudosService.countKudosForAllUsers()
        Map<Long, Integer> sentCounts = kudosService.countSentForAllUsers()
        List<User> users = User.list().sort { -(sentCounts[it.id] ?: 0) }
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
