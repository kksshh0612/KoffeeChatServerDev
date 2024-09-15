package teamkiim.koffeechat.domain.notification.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
public class SseEmitterWrapper {

    private SseEmitter sseEmitter;
    private boolean isReceive;

    @Builder
    public SseEmitterWrapper(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
        this.isReceive = true;
    }

    public void startReceiving() {
        this.isReceive = true;
    }

    public void stopReceiving() {
        this.isReceive = false;
    }
}
