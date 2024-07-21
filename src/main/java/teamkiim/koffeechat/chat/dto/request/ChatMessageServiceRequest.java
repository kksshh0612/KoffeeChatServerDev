package teamkiim.koffeechat.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.chat.domain.message.ChatMessage;
import teamkiim.koffeechat.chat.domain.message.MessageType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageServiceRequest {

    private MessageType messageType;
    private Long chatRoomId;
    private Long senderId;
    private String senderNickname;
    private String content;
    private LocalDateTime createdTime;

    public ChatMessage toEntity(){
        return ChatMessage.builder()
                .chatRoomId(this.chatRoomId)
                .content(this.content)
                .senderId(this.senderId)
                .senderNickname(this.senderNickname)
                .messageType(this.messageType)
                .createdTime(this.createdTime)
                .build();
    }
}
