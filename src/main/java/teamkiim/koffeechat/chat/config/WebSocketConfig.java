package teamkiim.koffeechat.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 클라이언트 -> 서버 메세지 보낼 때 사용할 경로
        // ex) /pub/chat 으로 메세지 전송 -> @Controller의 @MessageMapping("/chat) 으로 라우팅
        registry.setApplicationDestinationPrefixes("/pub");
        // 스프링 내장 메세지 브로커 활성화. subscribe, publish
        // ex) 클라이언트가 /sub/chat 경로를 subscribe 하면 서버가 /sub/chat 경로로 메세지를 publish 한다.
        registry.enableSimpleBroker("/sub");
    }
}
