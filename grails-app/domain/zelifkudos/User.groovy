package zelifkudos

class User {
    String email
    String name
    Boolean admin = false

    Date dateCreated

    static constraints = {
        email unique: true
    }

    static mapping = {
        table 'app_user'
    }
}
