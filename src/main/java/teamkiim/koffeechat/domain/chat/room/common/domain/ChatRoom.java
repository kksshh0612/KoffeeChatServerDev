package teamkiim.koffeechat.domain.chat.room.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private LocalDateTime lastMessageTime;                          // 마지막 메세지 전송 시간

    protected ChatRoom(ChatRoomType chatRoomType, String name, LocalDateTime lastMessageTime) {
        this.chatRoomType = chatRoomType;
        this.name = name;
        this.lastMessageTime = lastMessageTime;
    }

    //== 비지니스 로직 ==//
    public void updateLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
