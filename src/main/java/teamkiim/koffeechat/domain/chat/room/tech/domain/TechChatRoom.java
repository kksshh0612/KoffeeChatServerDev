package teamkiim.koffeechat.domain.chat.room.tech.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class TechChatRoom extends ChatRoom {

    private int currentMemberSize;                // 채팅방 참여자 수

    private int maxMemberSize;             // 최대 참여자 수

    @Embedded
    private SkillCategory skillCategory;

    @Builder
    public TechChatRoom(ChatRoomType chatRoomType, String name, LocalDateTime lastMessageTime, int currentMemberSize,
                        int maxMemberSize, SkillCategory skillCategory) {
        super(chatRoomType, name, lastMessageTime);
        this.currentMemberSize = currentMemberSize;
        this.maxMemberSize = maxMemberSize;
        this.skillCategory = skillCategory;
    }

    //== 비지니스 로직 ==//

    /**
     * 채팅방 참여자 수 증가
     */
    public void increaseMemberCount(){
        currentMemberSize++;
    }

    /**
     * 채팅방 참여자 수 감소
     */
    public void decreaseMemberCount(){
        currentMemberSize--;
    }
}
