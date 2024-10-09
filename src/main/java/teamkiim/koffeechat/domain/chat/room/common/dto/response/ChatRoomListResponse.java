package teamkiim.koffeechat.domain.chat.room.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResponse {

    private Long chatRoomId;
    private ChatRoomType chatRoomType;
    private String chatRoomName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadMessageCount;
    private Long oppositeMemberId;
    private String profileImageUrl;


    public static ChatRoomListResponse of(ChatRoomInfoDto chatRoomInfo, Member oppositeMember) {

        String roomName = chatRoomInfo.getMemberChatRoom().getChatRoom().getName();

        if (chatRoomInfo.getMemberChatRoom().getChatRoom().getChatRoomType().equals(ChatRoomType.DIRECT)) {
            roomName = oppositeMember.getNickname();
        }

        return ChatRoomListResponse.builder()
                .chatRoomId(chatRoomInfo.getMemberChatRoom().getChatRoom().getId())
                .chatRoomType(chatRoomInfo.getMemberChatRoom().getChatRoom().getChatRoomType())
                .chatRoomName(roomName)
                .lastMessage("마지막 메세지")
                .lastMessageTime(LocalDateTime.of(2024, 9, 20, 13, 0))
                .unreadMessageCount(chatRoomInfo.getUnreadCount())
                .oppositeMemberId(oppositeMember.getId())
                .profileImageUrl(oppositeMember.getProfileImageUrl())
                .build();
    }

}
