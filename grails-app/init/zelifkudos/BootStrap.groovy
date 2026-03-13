package zelifkudos

class BootStrap {

    def init = {
        LoginToken.withNewSession {
            LoginToken.withTransaction {
                LoginToken.executeUpdate("delete from LoginToken where expiryDate < :now", [now: new Date()])
            }
        }
    }

    def destroy = {
    }

}
