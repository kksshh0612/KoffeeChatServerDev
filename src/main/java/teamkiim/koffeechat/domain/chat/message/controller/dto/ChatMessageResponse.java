package teamkiim.koffeechat.domain.chat.message.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private String messageId;
    private MessageType messageType;
    private String content;
    private Long senderId;
    private String senderNickname;
    private String profileImageUrl;
    private LocalDateTime createdTime;

    public static ChatMessageResponse of(ChatMessageServiceRequest chatMessageServiceRequest, Member sendMember) {

        return ChatMessageResponse.builder()
                .messageId(chatMessageServiceRequest.getMessageId())
                .messageType(chatMessageServiceRequest.getMessageType())
                .content(chatMessageServiceRequest.getContent())
                .senderId(sendMember.getId())
                .senderNickname(sendMember.getNickname())
                .profileImageUrl(sendMember.getProfileImageUrl())
                .createdTime(chatMessageServiceRequest.getCreatedTime())
                .build();
    }
}
