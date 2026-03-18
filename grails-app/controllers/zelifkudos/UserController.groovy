package zelifkudos

class UserController {

    KudosService kudosService

    def list() {
        User currentUser = request.currentUser
        Map<Long, Integer> kudosCounts = kudosService.countKudosForAllUsers()
        Map<Long, Integer> sentCounts = kudosService.countSentForAllUsers()
        List<User> users = User.list().sort { -(sentCounts[it.id] ?: 0) }
        int myKudosCount = kudosCounts[currentUser.id] ?: 0
        List<Kudos> recentMessages = kudosService.getRecentKudosForUser(currentUser.id, 5)
        int totalKudosForMe = kudosService.countKudosForUser(currentUser.id)
        [users: users, kudosCounts: kudosCounts, isAdmin: currentUser.admin, currentUserId: currentUser.id,
         myKudosCount: myKudosCount, recentMessages: recentMessages, totalKudosForMe: totalKudosForMe]
    }

    def index() { redirect(action: 'list') }
}
