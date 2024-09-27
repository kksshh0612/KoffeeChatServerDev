package teamkiim.koffeechat.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "채팅 알림 생성 Request")
public class CreateChatNotificationRequest {

    private Long chatRoomId;                // 채팅방 id

    private String content;                 // 채팅 메세지

    private Long senderId;                  // 채팅을 작성한 회원

    private MessageType messageType;        // 메세지 타입 (입장, 채팅, 이미지)

    private LocalDateTime createdTime;

    public static CreateChatNotificationRequest of(ChatMessage chatMessage) {
        return CreateChatNotificationRequest.builder()
                .chatRoomId(chatMessage.getChatRoomId())
                .content(chatMessage.getContent())
                .senderId(chatMessage.getSenderId())
                .messageType(chatMessage.getMessageType())
                .createdTime(chatMessage.getCreatedTime())
                .build();
    }
}
