package teamkiim.koffeechat.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;

@Getter
@Builder
@Schema(description = "채팅 알림 생성 Request")
public class CreateChatNotificationRequest {

    private String chatRoomId;                // 채팅방 id

    private String content;                 // 채팅 메세지

    private String senderId;                  // 채팅을 작성한 회원

    private MessageType messageType;        // 메세지 타입 (입장, 채팅, 이미지)

    private LocalDateTime createdTime;

    public static CreateChatNotificationRequest of(String chatRoomId, String senderId, ChatMessage chatMessage) {
        return CreateChatNotificationRequest.builder()
                .chatRoomId(chatRoomId)
                .content(chatMessage.getContent())
                .senderId(senderId)
                .messageType(chatMessage.getMessageType())
                .createdTime(chatMessage.getCreatedTime())
                .build();
    }
}