package teamkiim.koffeechat.domain.chat.room.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResponse {

    private Long chatRoomId;
    private ChatRoomType chatRoomType;
    private String chatRoomName;
//    private String lastMessage;
//    private LocalDateTime lastMessageTime;
    private Long unreadMessageCount;

    public static ChatRoomListResponse of(ChatRoomInfoDto chatRoomInfo){

        String roomName = chatRoomInfo.getMemberChatRoom().getChatRoom().getName();

        if(chatRoomInfo.getMemberChatRoom().getChatRoom().getChatRoomType().equals(ChatRoomType.DIRECT)){
            roomName = chatRoomInfo.getMemberChatRoom().getRoomName();
        }

        return ChatRoomListResponse.builder()
                .chatRoomId(chatRoomInfo.getMemberChatRoom().getChatRoom().getId())
                .chatRoomType(chatRoomInfo.getMemberChatRoom().getChatRoom().getChatRoomType())
                .chatRoomName(roomName)
//                .lastMessageTime(chatRoomInfo.getMemberChatRoom().getCloseTime())
                .unreadMessageCount(chatRoomInfo.getUnreadCount())
                .build();
    }

}
