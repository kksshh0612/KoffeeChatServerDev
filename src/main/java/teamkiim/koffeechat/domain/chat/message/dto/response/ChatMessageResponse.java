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

    private MessageType messageType;
    private Long senderId;
    private String senderNickname;
    private String content;
    private boolean isLoginMember;
    private LocalDateTime createdTime;

    public static ChatMessageResponse of(ChatMessage chatMessage, List<Member> members, Long memberId) {

        String senderNickname = members.stream()
                .filter(member -> member.getId().equals(chatMessage.getSenderId()))
                .map(Member::getNickname)
                .findFirst()
                .orElse("(Unknown)");

        boolean isLoginMember = chatMessage.getSenderId().equals(memberId);

        return ChatMessageResponse.builder()
                .messageType(chatMessage.getMessageType())
                .senderId(chatMessage.getSenderId())
                .senderNickname(senderNickname)
                .content(chatMessage.getContent())
                .isLoginMember(isLoginMember)
                .createdTime(chatMessage.getCreatedTime())
                .build();

    }
}
