package teamkiim.koffeechat.domain.chat.message.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    private MessageType messageType;
    private String content;

    public ChatMessageServiceRequest toServiceRequest(LocalDateTime currDateTime) {
        return ChatMessageServiceRequest.builder()
                .messageType(this.messageType)
                .content(this.content)
                .createdTime(currDateTime)
                .build();
    }
}
