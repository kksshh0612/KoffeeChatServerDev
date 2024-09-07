package teamkiim.koffeechat.domain.chat.room.common.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class MemberChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_chat_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private String roomName;                    // 일대일 채팅방의 경우 저장할 이름

    private LocalDateTime closeTime;

    @Builder
    public MemberChatRoom(Member member, ChatRoom chatRoom, String roomName, LocalDateTime closeTime) {
        this.member = member;
        this.chatRoom = chatRoom;
        this.roomName = roomName;
        this.closeTime = closeTime;
    }

    //== 비지니스 로직 ==//
    public void updateCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }
}
