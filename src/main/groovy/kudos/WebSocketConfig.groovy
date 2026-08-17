package kudos

import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.HandshakeInterceptor

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }

    @Override
    void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
            .addInterceptors(new SessionHandshakeInterceptor())
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    static class SessionHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Map<String, Object> attributes) {
            if (request instanceof ServletServerHttpRequest) {
                def session = request.servletRequest.getSession(false)
                def userId = session?.getAttribute('userId')
                if (!userId) return false
                attributes['userId'] = userId
            }
            return true
        }

        @Override
        void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                            WebSocketHandler wsHandler, Exception exception) {}
    }
}
