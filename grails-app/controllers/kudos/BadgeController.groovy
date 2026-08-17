package kudos

class BadgeController {

    BadgeService badgeService

    def index() { redirect(action: 'list') }

    def list() {
        User currentUser = request.currentUser

        // Evaluated on view rather than by a scheduled sweep: it is a handful of
        // counts for one user, and it means a badge is waiting the next time you
        // look instead of whenever a job happens to run.
        badgeService.evaluate(currentUser)

        [catalog: BadgeService.CATALOG,
         earned: badgeService.earnedCodes(currentUser),
         earnedDates: badgeService.earnedDates(currentUser)]
    }
}
