package zelifkudos

class Kudos {

    User sender
    User receiver
    Date dateCreated

    static constraints = {
        sender nullable: false
        receiver nullable: false
    }
}
