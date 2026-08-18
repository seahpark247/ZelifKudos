package kudos

class BadgeTagLib {

    static namespace = 'badge'
    static defaultEncodeAs = [taglib: 'none']

    BadgeService badgeService

    /**
     * The compact badge panel that sits beside the roster on every page.
     *
     * Read-only on purpose: awarding already happens when a kudo lands and when
     * the full badge page is opened, so the panel does not need to add queries to
     * every single page load.
     */
    def panel = { attrs ->
        User user = request.currentUser
        if (!user) return

        Set<String> earned = badgeService.earnedCodes(user)
        out << render(template: '/badge/panel',
                      model: [catalog: badgeService.visibleCatalogue(earned),
                              earned: earned])
    }
}
