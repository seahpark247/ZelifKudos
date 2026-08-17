package kudos

class KudosReset {

    User resetBy
    Date dateCreated

    static constraints = {
        resetBy nullable: true
    }
}
