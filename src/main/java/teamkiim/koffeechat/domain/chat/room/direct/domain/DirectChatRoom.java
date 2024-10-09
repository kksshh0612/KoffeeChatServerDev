package teamkiim.koffeechat.domain.chat.room.direct.domain;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class DirectChatRoom extends ChatRoom {

    @Builder
    public DirectChatRoom(ChatRoomType chatRoomType, String name, LocalDateTime lastMessageTime) {

        super(chatRoomType, name, lastMessageTime);
    }
}
