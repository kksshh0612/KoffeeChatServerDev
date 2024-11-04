package teamkiim.koffeechat.domain.chat.interceptor;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import teamkiim.koffeechat.global.authentication.Authenticator;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketConnectionHandshakeInterceptor implements HandshakeInterceptor {

    private final Authenticator authenticator;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String validAccessToken = authenticator.verify(request, response);
        Long memberId = authenticator.getMemberIdFromValidAccessToken(validAccessToken);

        log.info("MemberPK : " + memberId + " 의 web socket HandShake started");

        // 클라이언트마다 웹소켓 세션 생성
        attributes.put("memberId", memberId);       // WebSocketSession의 attributes에 저장

        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

        log.info("web socket HandShake end");
    }
}
