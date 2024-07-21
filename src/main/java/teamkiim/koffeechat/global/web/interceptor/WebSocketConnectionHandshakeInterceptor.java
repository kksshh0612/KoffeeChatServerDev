package teamkiim.koffeechat.global.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import teamkiim.koffeechat.auth.service.AuthService;
import teamkiim.koffeechat.global.authentication.Authenticator;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;

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
