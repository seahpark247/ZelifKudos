package kudos

class BadgeTagLib {

    static namespace = 'badge'
    static defaultEncodeAs = [taglib: 'none']

    BadgeService badgeService

    /**
     * The compact badge panel beside the roster: a trophy case, so it holds only
     * what has actually been won. Locked badges and their progress live on the
     * Badges page, which is the catalogue view.
     *
     * Nothing earned yet means no panel at all — an empty window reads as broken,
     * and the panel appearing with the first badge is its own small reward.
     *
     * Read-only: awarding happens when a kudo lands and when the Badges page is
     * opened, so this adds no writes to every page load.
     */
    def panel = { attrs ->
        User user = request.currentUser
        if (!user) return

        // Claimed before the panel renders so a badge earned by someone else's
        // kudo is announced on whatever page its holder loads next.
        List<String> freshCodes = badgeService.claimUnseen(user)
        List<Map> fresh = BadgeService.CATALOG.findAll { it.code in freshCodes }

        Set<String> earned = badgeService.earnedCodes(user)
        List<Map> held = BadgeService.CATALOG.findAll { it.code in earned }
        if (!held) return

        out << render(template: '/badge/panel',
                      model: [catalog: held,
                              earnedDates: badgeService.earnedDates(user),
                              fresh: fresh])
    }
}
