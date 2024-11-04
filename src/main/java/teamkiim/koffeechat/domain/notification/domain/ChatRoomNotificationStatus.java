package teamkiim.koffeechat.domain.notification.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomNotificationStatus {

    private final Long chatRoomId;  // 채팅방 pk
    private boolean isAlert;  // 해당 채팅방에 대해 sse 알림 수신 여부

    @Builder
    public ChatRoomNotificationStatus(Long chatRoomId, boolean isAlert) {
        this.chatRoomId = chatRoomId;
        this.isAlert = isAlert;
    }

    // 채팅방 알림 sse 수신 o
    public void startSseAlert() {
        this.isAlert = true;
    }

    // 채팅방 알림 sse 수신 o
    public void stopSseAlert() {
        this.isAlert = false;
    }
}