package teamkiim.koffeechat.domain.chat.room.common.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.member.domain.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResponse {

    private String memberId;
    private String chatRoomId;
    private ChatRoomType chatRoomType;
    private String chatRoomName;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private Long unreadMessageCount;
    private String oppositeMemberId;
    private String profileImageUrl;

    public static ChatRoomListResponse of(String memberId, String chatRoomId, String oppositeMemberId,
                                          Member oppositeMember, ChatRoomInfoDto chatRoomInfo) {

        String roomName = chatRoomInfo.getMemberChatRoom().getChatRoom().getName();

        if (chatRoomInfo.getMemberChatRoom().getChatRoom().getChatRoomType().equals(ChatRoomType.DIRECT)) {
            roomName = oppositeMember.getNickname();
        }

        return ChatRoomListResponse.builder()
                .memberId(memberId)
                .chatRoomId(chatRoomId)
                .chatRoomType(chatRoomInfo.getMemberChatRoom().getChatRoom().getChatRoomType())
                .chatRoomName(roomName)
                .lastMessageContent(chatRoomInfo.getLatestMessageContent())
                .lastMessageTime(chatRoomInfo.getLatestMessageTime())
                .unreadMessageCount(chatRoomInfo.getUnreadCount())
                .oppositeMemberId(oppositeMemberId)
                .profileImageUrl(oppositeMember.getProfileImageUrl())
                .build();
    }

}
