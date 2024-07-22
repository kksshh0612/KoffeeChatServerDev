package teamkiim.koffeechat.global.web.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import teamkiim.koffeechat.global.authentication.Authenticator;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketConnectionHandshakeInterceptor implements HandshakeInterceptor {

    private final Authenticator authenticator;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String validAccessToken = authenticator.verify(request, response);
        Long memberId = authenticator.getMemberIdFromValidAccessToken(validAccessToken);

        attributes.put("memberId", memberId);

        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

    }
}
