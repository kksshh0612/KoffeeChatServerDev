package teamkiim.koffeechat.domain.chat.room.tech.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinTechChatRoomListResponse {

    private String memberId;
    private String chatRoomId;
    private ChatRoomType chatRoomType;
    private String chatRoomName;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private Long unreadMessageCount;

    public static JoinTechChatRoomListResponse of(String memberId, String chatRoomId, ChatRoomInfoDto chatRoomInfo) {
        return JoinTechChatRoomListResponse.builder()
                .memberId(memberId)
                .chatRoomId(chatRoomId)
                .chatRoomType(chatRoomInfo.getMemberChatRoom().getChatRoom().getChatRoomType())
                .chatRoomName(chatRoomInfo.getMemberChatRoom().getChatRoom().getName())
                .lastMessageContent(chatRoomInfo.getLatestMessageContent())
                .lastMessageTime(chatRoomInfo.getLatestMessageTime())
                .unreadMessageCount(chatRoomInfo.getUnreadCount())
                .build();
    }
}
