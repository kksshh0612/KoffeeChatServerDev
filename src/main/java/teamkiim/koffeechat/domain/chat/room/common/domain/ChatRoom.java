package teamkiim.koffeechat.domain.chat.room.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@NoArgsConstructor
public abstract class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;                              // 채팅방 타입

    private String name;                                            // 채팅방 이름

    protected ChatRoom(ChatRoomType chatRoomType, String name) {
        this.chatRoomType = chatRoomType;
        this.name = name;
    }
}
