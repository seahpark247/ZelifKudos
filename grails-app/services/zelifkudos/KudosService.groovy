package zelifkudos

import grails.gorm.transactions.Transactional

@Transactional
class KudosService {

    static final int DAILY_LIMIT_PER_RECEIVER = 5
    static final int COOLDOWN_MINUTES = 10

    Kudos sendKudos(Long senderId, Long receiverId, String message) {
        User sender = User.get(senderId)
        User receiver = User.get(receiverId)

        if (!sender || !receiver || sender.id == receiver.id) {
            return null
        }

        String limitMsg = checkLimit(sender, receiver)
        if (limitMsg) {
            throw new KudosLimitException(limitMsg)
        }

        new Kudos(sender: sender, receiver: receiver, message: message?.trim() ?: null).save(failOnError: true)
    }

    private String checkLimit(User sender, User receiver) {
        Date startOfDay = new Date().clearTime()
        int dailyCount = Kudos.countBySenderAndReceiverAndDateCreatedGreaterThan(sender, receiver, startOfDay)
        if (dailyCount >= DAILY_LIMIT_PER_RECEIVER) {
            return "You've already sent ${DAILY_LIMIT_PER_RECEIVER} kudos to ${receiver.name.capitalize()} today. Send again tomorrow!"
        }

        Date cooldownTime = new Date(System.currentTimeMillis() - COOLDOWN_MINUTES * 60 * 1000)
        int recentCount = Kudos.countBySenderAndReceiverAndDateCreatedGreaterThan(sender, receiver, cooldownTime)
        if (recentCount > 0) {
            return "Please wait a few minutes before sending kudos to ${receiver.name.capitalize()} again."
        }

        return null
    }

    int countKudosSinceLastReset() {
        Date lastReset = getLastResetDate()
        lastReset ? Kudos.countByDateCreatedGreaterThan(lastReset) : Kudos.count()
    }

    void markKudosReset(User resetBy) {
        new KudosReset(resetBy: resetBy).save(failOnError: true)
    }

    Date getLastResetDate() {
        KudosReset.executeQuery("select max(kr.dateCreated) from KudosReset kr")[0] as Date
    }

    List<Date> getAllResetDates() {
        KudosReset.executeQuery("select kr.dateCreated from KudosReset kr order by kr.dateCreated desc") as List<Date>
    }

    Map<Long, Integer> countSentForAllUsers() {
        List results = Kudos.executeQuery("select k.sender.id, count(k) from Kudos k group by k.sender.id")
        results.collectEntries { [(it[0]): it[1] as int] }
    }

    Map<Long, Integer> countKudosForAllUsers() {
        Date lastReset = getLastResetDate()
        List results = lastReset
            ? Kudos.executeQuery("select k.receiver.id, count(k) from Kudos k where k.dateCreated > :reset group by k.receiver.id", [reset: lastReset])
            : Kudos.executeQuery("select k.receiver.id, count(k) from Kudos k group by k.receiver.id")
        results.collectEntries { [(it[0]): it[1] as int] }
    }

    Map listKudos(User user, int max, int offset) {
        List<Date> resetDates = getAllResetDates()
        if (user.admin) {
            [list: Kudos.list(sort: "dateCreated", order: "desc", max: max, offset: offset),
             total: Kudos.count(),
             resetDates: resetDates]
        } else {
            [list: Kudos.findAllBySender(user, [sort: "dateCreated", order: "desc", max: max, offset: offset]),
             total: Kudos.countBySender(user),
             resetDates: resetDates]
        }
    }
}
