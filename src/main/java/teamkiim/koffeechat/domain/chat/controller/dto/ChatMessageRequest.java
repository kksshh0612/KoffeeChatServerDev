package teamkiim.koffeechat.domain.chat.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.domain.message.MessageType;
import teamkiim.koffeechat.domain.chat.dto.request.ChatMessageServiceRequest;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    private MessageType messageType;
    private Long chatRoomId;
    private Long senderId;
    private String senderNickname;
    private String content;

    public void setContent(String content) {
        this.content = content;
    }

    public ChatMessageServiceRequest toServiceRequest(LocalDateTime currDateTime) {
        return ChatMessageServiceRequest.builder()
                .messageType(this.messageType)
                .chatRoomId(this.chatRoomId)
                .senderId(this.senderId)
                .senderNickname(this.senderNickname)
                .content(this.content)
                .createdTime(currDateTime)
                .build();
    }
}
