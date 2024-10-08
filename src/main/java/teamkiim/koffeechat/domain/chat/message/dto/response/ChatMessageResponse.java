package teamkiim.koffeechat.domain.chat.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;

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
    private boolean isLoginMember;
    private LocalDateTime createdTime;

    public static ChatMessageResponse of(ChatMessage chatMessage, List<Member> joinMembers, Long loginMemberId) {

        Member sender = joinMembers.stream().filter(m -> m.getId().equals(chatMessage.getSenderId())).findFirst().orElse(null);

//        String senderNickname = members.stream()
//                .filter(member -> member.getId().equals(chatMessage.getSenderId()))
//                .map(Member::getNickname)
//                .findFirst()
//                .orElse("(Unknown)");

        boolean isLoginMember = chatMessage.getSenderId().equals(loginMemberId);

        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .senderId(chatMessage.getSenderId())
                .senderNickname(sender.getNickname())
                .profileImageUrl(sender.getProfileImageUrl())
                .isLoginMember(isLoginMember)
                .createdTime(chatMessage.getCreatedTime())
                .build();

    }
}
