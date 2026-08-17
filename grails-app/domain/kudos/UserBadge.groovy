package kudos

/**
 * A badge a user has earned. The catalogue itself lives in BadgeService as code
 * rather than a table: badges are defined by conditions, not by data, and adding
 * one is a code change either way.
 */
class UserBadge {

    User user
    String code
    Date dateCreated

    static constraints = {
        code blank: false, maxSize: 40
    }
}
