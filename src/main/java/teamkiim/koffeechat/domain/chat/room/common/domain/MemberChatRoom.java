package teamkiim.koffeechat.domain.chat.room.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor
public class MemberChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Boolean active;

    @Builder
    public MemberChatRoom(Member member, ChatRoom chatRoom, String roomName, LocalDateTime closeTime, boolean active) {
        this.member = member;
        this.chatRoom = chatRoom;
        this.roomName = roomName;
        this.closeTime = closeTime;
        this.active = active;
    }

    public static MemberChatRoom of(Member member, ChatRoom chatRoom, String roomName, LocalDateTime closeTime,
                                    boolean active) {

        return MemberChatRoom.builder()
                .member(member)
                .chatRoom(chatRoom)
                .roomName(roomName)
                .closeTime(closeTime)
                .active(active)
                .build();
    }

    //== 비지니스 로직 ==//
    public void updateCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public void exit() {
        this.active = false;
    }

    public void enter() {
        this.active = true;
    }
}
