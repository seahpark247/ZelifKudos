package zelifkudos

class LoginToken {

    String email
    String token
    Date expiryDate
    Date dateCreated
    boolean verified = false

    static constraints = {
        email blank: false
        token blank: false
        expiryDate nullable: false
    }

    static mapping = {
        token index: 'idx_login_token_token'
    }
}
