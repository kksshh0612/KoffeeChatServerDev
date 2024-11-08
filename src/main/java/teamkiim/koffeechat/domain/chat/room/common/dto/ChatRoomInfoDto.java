package teamkiim.koffeechat.domain.chat.room.common.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomInfoDto {

    private MemberChatRoom memberChatRoom;
    private String latestMessageContent;
    private LocalDateTime latestMessageTime;
    private long unreadCount;

}
