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

        Set<String> earned = badgeService.earnedCodes(currentUser)

        [catalog: badgeService.visibleCatalogue(earned),
         earned: earned,
         earnedDates: badgeService.earnedDates(currentUser),
         progress: badgeService.progress(currentUser)]
    }
}
