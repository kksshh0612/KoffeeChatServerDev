package teamkiim.koffeechat.domain.chat.message.controller.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.member.domain.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private String messageId;
    private MessageType messageType;
    private String content;
    private String senderId;
    private String senderNickname;
    private String profileImageUrl;
    private LocalDateTime createdTime;

    public static ChatMessageResponse of(ChatMessageServiceRequest chatMessageServiceRequest, String senderId,
                                         Member sendMember) {

        return ChatMessageResponse.builder()
                .messageId(chatMessageServiceRequest.getMessageId())
                .messageType(chatMessageServiceRequest.getMessageType())
                .content(chatMessageServiceRequest.getContent())
                .senderId(senderId)
                .senderNickname(sendMember.getNickname())
                .profileImageUrl(sendMember.getProfileImageUrl())
                .createdTime(chatMessageServiceRequest.getCreatedTime())
                .build();
    }
}
