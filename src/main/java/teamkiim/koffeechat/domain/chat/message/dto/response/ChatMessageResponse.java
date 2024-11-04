package teamkiim.koffeechat.domain.chat.message.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
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
    private boolean isLoginMember;
    private LocalDateTime createdTime;

    public static ChatMessageResponse of(ChatMessage chatMessage, List<Member> joinMembers, String senderId,
                                         Long loginMemberId) {

        Member sender = joinMembers.stream().filter(m -> m.getId().equals(chatMessage.getSenderId())).findFirst()
                .orElse(null);

        boolean isLoginMember = chatMessage.getSenderId().equals(loginMemberId);

        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .senderId(senderId)
                .senderNickname(sender.getNickname())
                .profileImageUrl(sender.getProfileImageUrl())
                .isLoginMember(isLoginMember)
                .createdTime(chatMessage.getCreatedTime())
                .build();

    }

    public static ChatMessageResponse of(ChatMessage chatMessage, String senderId, Member sender) {
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .senderId(senderId)
                .senderNickname(sender.getNickname())
                .profileImageUrl(sender.getProfileImageUrl())
                .isLoginMember(true)
                .createdTime(chatMessage.getCreatedTime())
                .build();
    }
}
