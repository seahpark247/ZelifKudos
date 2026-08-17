package kudos

import grails.gorm.transactions.Transactional

@Transactional
class UserService {

    void toggleAdmin(User user) {
        user.admin = !user.admin
        user.save(failOnError: true)
    }
}
