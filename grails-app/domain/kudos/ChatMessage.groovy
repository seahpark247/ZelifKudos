package kudos

class ChatMessage {

    String content
    Long userId
    String nickname
    String colorHex
    Date dateCreated

    static constraints = {
        content maxSize: 500
        userId nullable: true
        nickname nullable: true
        colorHex nullable: true
    }
}
