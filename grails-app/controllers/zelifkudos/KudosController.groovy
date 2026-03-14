package zelifkudos

class KudosController {

    static allowedMethods = [send: 'POST', reset: 'POST']

    KudosService kudosService

    def send() {
        Long receiverId = params.long('id')
        Kudos kudos = kudosService.sendKudos(session.userId as Long, receiverId)

        if (!kudos) {
            flash.message = "Invalid user"
        } else {
            flash.message = "Kudos sent to ${kudos.receiver.name.capitalize()}!"
        }

        redirect(controller: "user", action: "list")
    }

    def reset() {
        User currentUser = User.get(session.userId)
        if (!currentUser?.admin) {
            flash.message = "Access denied"
            redirect(controller: "user", action: "list")
            return
        }

        if (kudosService.countKudosSinceLastReset() == 0) {
            flash.message = "Nothing to reset, no kudos since the last reset."
            redirect(controller: "user", action: "list")
            return
        }

        kudosService.resetAllKudos(currentUser)
        flash.message = "All kudos have been reset."
        redirect(controller: "user", action: "list")
    }

    def list() {
        int max = 15
        int offset = Math.max(0, params.int('offset', 0))

        User currentUser = User.get(session.userId)
        if (!currentUser) {
            session.invalidate()
            redirect(controller: "login")
            return
        }
        Map result = kudosService.listKudos(currentUser, max, offset)
        int total = result.total
        int totalPages = total ? (int) Math.ceil((double) total / max) : 0
        int currentPage = (int)(offset / max) + 1
        [kudosList: result.list, total: total, resetDates: result.resetDates,
         max: max, offset: offset, totalPages: totalPages, currentPage: currentPage, currentUser: currentUser]
    }
}
