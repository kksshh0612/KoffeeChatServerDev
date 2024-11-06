package teamkiim.koffeechat.domain.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import teamkiim.koffeechat.domain.chat.interceptor.WebSocketConnectionHandshakeInterceptor;
import teamkiim.koffeechat.global.authentication.Authenticator;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final Authenticator authenticator;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/wss")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketConnectionHandshakeInterceptor(authenticator))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 서버에서 해당 경로로 subscribe한 클라이언트에게
        // ex) /sub/chat/채팅방id 로 subscribe한 클라이언트에게 메세지를 publish
        registry.enableSimpleBroker("/sub");

        // 클라이언트 -> 서버 메세지 보낼 때 사용할 경로
        // ex) 클라이언트에서 /pub/chat 으로 메세지 전송 -> 서버 @Controller의 @MessageMapping("/chat) 으로 라우팅
        registry.setApplicationDestinationPrefixes("/pub");
    }

}
