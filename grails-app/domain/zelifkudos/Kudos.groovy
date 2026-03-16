package zelifkudos

class Kudos {

    User sender
    User receiver
    String message
    Date dateCreated

    static constraints = {
        sender nullable: false
        receiver nullable: false
        message nullable: true, maxSize: 200
    }
}
