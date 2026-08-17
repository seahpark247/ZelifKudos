package kudos

import grails.converters.JSON

class ChatController {

    ChatService chatService

    def recent() {
        List<ChatMessage> messages = chatService.getRecentMessages(50)
        render messages.collect {
            [content: it.content, timestamp: it.dateCreated.time,
             nickname: it.nickname ?: "Anonymous", color: it.colorHex ?: "#CCCCCC"]
        } as JSON
    }

    def myNickname() {
        if (!session.userId) {
            render([nickname: null] as JSON)
            return
        }
        AnimalNickname nick = AnimalNickname.findByUser(User.get(session.userId))
        render([nickname: nick?.animalName] as JSON)
    }
}
