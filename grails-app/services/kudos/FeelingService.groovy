package kudos

import grails.gorm.transactions.Transactional

@Transactional
class FeelingService {

    Feeling saveFeeling(User user, String message) {
        Feeling feeling = Feeling.findByUser(user)
        if (feeling) {
            feeling.message = message.trim()
        } else {
            feeling = new Feeling(user: user, message: message.trim())
        }
        feeling.save(failOnError: true)
    }

    void deleteFeeling(User user) {
        Feeling.findByUser(user)?.delete()
    }

    Map<Long, String> getAllFeelings() {
        Feeling.list().collectEntries { [(it.user.id): it.message] }
    }
}
