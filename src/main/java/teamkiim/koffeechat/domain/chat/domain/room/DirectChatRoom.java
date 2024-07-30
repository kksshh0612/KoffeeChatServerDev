package teamkiim.koffeechat.domain.chat.domain.room;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DirectChatRoom extends ChatRoom {


    @Builder
    public DirectChatRoom(ChatRoomType chatRoomType, String name, boolean activeStatus) {

        super(chatRoomType, name, activeStatus);
    }
}
