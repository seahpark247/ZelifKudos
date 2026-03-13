package zelifkudos

class KudosReset {

    User resetBy
    Date dateCreated

    static constraints = {
        resetBy nullable: false
    }
}
