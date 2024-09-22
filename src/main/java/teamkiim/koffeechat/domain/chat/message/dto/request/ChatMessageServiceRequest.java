package teamkiim.koffeechat.domain.chat.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageServiceRequest {

    private MessageType messageType;
    private String content;
    private LocalDateTime createdTime;

    public ChatMessage toEntity(Long chatRoomId, Long senderId){
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .content(this.content)
                .senderId(senderId)
                .messageType(this.messageType)
                .createdTime(this.createdTime)
                .build();
    }
}
