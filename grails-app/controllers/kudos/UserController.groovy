package kudos

import grails.converters.JSON

class UserController {

    static allowedMethods = [updateFeeling: 'POST', toggleAdmin: 'POST']

    KudosService kudosService
    FeelingService feelingService
    UserService userService

    def list() {
        User currentUser = request.currentUser
        Map<Long, Integer> kudosCounts = kudosService.countKudosForAllUsers()
        Map<Long, Integer> sentCounts = kudosService.countSentForAllUsers()
        List<User> users = User.findAllByActivated(true).sort { -(sentCounts[it.id] ?: 0) }
        int myKudosCount = kudosCounts[currentUser.id] ?: 0
        List<Kudos> recentMessages = kudosService.getRecentKudosForUser(currentUser.id, 3).findAll { it.message }
        Map<Long, String> feelings = feelingService.getAllFeelings()
        [users: users, kudosCounts: kudosCounts, isAdmin: currentUser.admin, currentUserId: currentUser.id,
         myKudosCount: myKudosCount, recentMessages: recentMessages, feelings: feelings]
    }

    def updateFeeling() {
        User currentUser = request.currentUser
        String message = params.feeling?.trim()
        if (message) {
            feelingService.saveFeeling(currentUser, message)
        } else {
            feelingService.deleteFeeling(currentUser)
        }
        redirect(action: 'list')
    }

    def toggleAdmin() {
        User currentUser = request.currentUser
        boolean toggled = false
        if (userService.isSuperAdmin(currentUser)) {
            userService.toggleAdmin(currentUser)
            toggled = true
        }

        // Answer with JSON rather than redirecting. A redirect reloads the page
        // for everyone who clicks the clock, which advertises that the clock does
        // something — the point is that it looks inert unless you're the one
        // account that can use it.
        render([toggled: toggled] as JSON)
    }

    def index() { redirect(action: 'list') }
}
