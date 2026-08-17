package kudos

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class BadgeService {

    GrailsApplication grailsApplication

    /**
     * The badge catalogue, in display order: the two you are given, then the ones
     * you earn, roughly by difficulty.
     *
     * Kept in code rather than a table. A badge is a condition, not a row — adding
     * one means writing the condition anyway, so a table would only add a place
     * for the two to disagree.
     *
     * `icon` names a file under src/main/resources/static/badges/.
     */
    static final List<Map> CATALOG = [
        [code: 'cofounder',      name: 'Cofounder',   description: 'Here from the beginning',        icon: 'cofounder.png'],
        [code: 'staff',          name: 'Staff',       description: 'Keeps the lights on',            icon: 'staff.png'],
        [code: 'first_sent',     name: 'First Words', description: 'Send your first kudo',           icon: 'first_sent.png'],
        [code: 'first_received', name: 'Noticed',     description: 'Receive your first kudo',        icon: 'first_received.png'],
        [code: 'generous',       name: 'Generous',    description: 'Send 10 kudos',                  icon: 'generous.png'],
        [code: 'beloved',        name: 'Beloved',     description: 'Receive 10 kudos',               icon: 'beloved.png'],
        [code: 'all_hands',      name: 'All Hands',   description: 'Send a kudo to every teammate',  icon: 'all_hands.png'],
        [code: 'patron',         name: 'Patron',      description: 'Send 50 kudos',                  icon: 'patron.png'],
        [code: 'star',           name: 'Star',        description: 'Receive 50 kudos',               icon: 'star.png'],
    ]

    @Transactional(readOnly = true)
    Set<String> earnedCodes(User user) {
        user ? UserBadge.findAllByUser(user)*.code as Set : [] as Set
    }

    /** code -> when it was earned, for the tooltip on the badge wall. */
    @Transactional(readOnly = true)
    Map<String, Date> earnedDates(User user) {
        user ? UserBadge.findAllByUser(user).collectEntries { [(it.code): it.dateCreated] } : [:]
    }

    /**
     * Award anything newly qualified.
     *
     * Badges are permanent once earned — losing the admin flag does not take
     * Staff back, and a reset does not take Generous back. That is the point:
     * everything else in this app resets on Friday, so badges are the only thing
     * that accumulates.
     */
    void evaluate(User user) {
        if (!user) return

        Set<String> have = earnedCodes(user)
        Set<String> missing = qualifyingCodes(user) - have
        if (!missing) return

        missing.each { String code ->
            try {
                new UserBadge(user: user, code: code).save(failOnError: true, flush: true)
                log.info("Badge '{}' earned by {}", code, user.email)
            } catch (Exception e) {
                // The unique constraint is the arbiter: a concurrent request got
                // there first. Nothing to do.
                log.debug("Badge '{}' already held by {}", code, user.email)
            }
        }
    }

    @Transactional(readOnly = true)
    protected Set<String> qualifyingCodes(User user) {
        Set<String> codes = [] as Set

        if (user.email?.toLowerCase() in cofounderEmails()) codes << 'cofounder'
        if (user.admin) codes << 'staff'

        // All-time, deliberately: every other count in this app is "since the
        // last reset", which would make cumulative badges unwinnable.
        int sent = Kudos.countBySender(user)
        int received = Kudos.countByReceiver(user)

        if (sent >= 1) codes << 'first_sent'
        if (sent >= 10) codes << 'generous'
        if (sent >= 50) codes << 'patron'

        if (received >= 1) codes << 'first_received'
        if (received >= 10) codes << 'beloved'
        if (received >= 50) codes << 'star'

        if (hasSentToEveryone(user)) codes << 'all_hands'

        codes
    }

    /**
     * "Everyone" means every activated teammate other than yourself, measured
     * now — so the badge gets harder as the team grows, and someone who earned it
     * keeps it.
     */
    private boolean hasSentToEveryone(User user) {
        int teammates = User.executeQuery(
            "select count(u) from User u where u.activated = true and u.id != :id",
            [id: user.id])[0] as int
        if (teammates < 1) return false

        int reached = Kudos.executeQuery(
            "select count(distinct k.receiver.id) from Kudos k where k.sender = :u",
            [u: user])[0] as int

        reached >= teammates
    }

    private Set<String> cofounderEmails() {
        String raw = grailsApplication.config.getProperty('app.badges.cofounderEmails') ?: ''
        raw.split(',').collect { it.trim().toLowerCase() }.findAll { it } as Set
    }
}
