package kudos

import grails.core.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Controller
class ChatWebSocketController {

    @Autowired
    SimpMessagingTemplate messagingTemplate

    @Autowired
    ChatService chatService

    @Autowired
    GrailsApplication grailsApplication

    private final Map<String, Long> lastSendTime = [:].asSynchronized()
    private final Map<String, String> lastMessage = [:].asSynchronized()

    @MessageMapping("/chat.send")
    void sendMessage(@Payload Map message, SimpMessageHeaderAccessor headerAccessor) {
        String content = message.content?.toString()?.trim()
        if (!content || content.length() > 500) return

        String sessionId = headerAccessor.sessionId
        long now = System.currentTimeMillis()

        int cooldownMs = grailsApplication.config.getProperty('app.chat.cooldownMs', Integer, 3000)
        int duplicateWindowMs = grailsApplication.config.getProperty('app.chat.duplicateWindowMs', Integer, 10000)

        // Cooldown. Note lastSendTime only advances on an accepted message, so a
        // rejected one does not push the window out.
        Long lastTime = lastSendTime.get(sessionId)
        if (lastTime && now - lastTime < cooldownMs) return

        // Block an identical repeat within the duplicate window.
        String lastMsg = lastMessage.get(sessionId)
        if (lastMsg == content && lastTime && now - lastTime < duplicateWindowMs) return

        lastSendTime.put(sessionId, now)
        lastMessage.put(sessionId, content)

        Long userId = headerAccessor.sessionAttributes?.get('userId') as Long

        // Get or assign animal nickname on first chat
        AnimalNickname nick = userId ? chatService.getOrAssignNickname(userId) : null
        String nickname = nick?.animalName ?: "Anonymous"
        String colorHex = nick?.colorHex ?: "#CCCCCC"

        ChatMessage saved = chatService.saveMessage(content, userId, nickname, colorHex)

        messagingTemplate.convertAndSend("/topic/chat", [
            content: saved.content,
            timestamp: saved.dateCreated.time,
            nickname: nickname,
            color: colorHex
        ])
    }

    @EventListener
    void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.sessionId
        lastSendTime.remove(sessionId)
        lastMessage.remove(sessionId)
    }
}
