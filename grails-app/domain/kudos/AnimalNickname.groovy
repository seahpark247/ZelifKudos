package kudos

class AnimalNickname {

    User user
    String animalName
    String colorHex
    Date dateCreated

    static constraints = {
        user unique: true
    }

    static mapping = {
        table 'animal_nickname'
    }
}
