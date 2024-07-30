package teamkiim.koffeechat.domain.chat.domain.room;

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

    private boolean activeStatus;                                   // 활성화 상태

    protected ChatRoom(ChatRoomType chatRoomType, String name, boolean activeStatus) {
        this.chatRoomType = chatRoomType;
        this.name = name;
        this.activeStatus = activeStatus;
    }
}
