package zelifkudos

import grails.gorm.transactions.Transactional

@Transactional
class KudosService {

    Kudos sendKudos(Long senderId, Long receiverId, String message) {
        User sender = User.get(senderId)
        User receiver = User.get(receiverId)

        if (!sender || !receiver || sender.id == receiver.id) {
            return null
        }

        new Kudos(sender: sender, receiver: receiver, message: message?.trim() ?: null).save(failOnError: true)
    }

    int countKudosSinceLastReset() {
        Date lastReset = getLastResetDate()
        lastReset ? Kudos.countByDateCreatedGreaterThan(lastReset) : Kudos.count()
    }

    void resetAllKudos(User resetBy) {
        new KudosReset(resetBy: resetBy).save(failOnError: true)
    }

    Date getLastResetDate() {
        KudosReset.executeQuery("select max(kr.dateCreated) from KudosReset kr")[0] as Date
    }

    List<Date> getAllResetDates() {
        KudosReset.executeQuery("select kr.dateCreated from KudosReset kr order by kr.dateCreated desc") as List<Date>
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
