package teamkiim.koffeechat.domain.chat.domain.room;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

@Entity
@Getter
@NoArgsConstructor
public class TechChatRoom extends ChatRoom {

    @Embedded
    private SkillCategory skillCategory;

    @Builder
    public TechChatRoom(ChatRoomType chatRoomType, String name, boolean activeStatus, SkillCategory skillCategory) {

        super(chatRoomType, name, activeStatus);
        this.skillCategory = skillCategory;
    }
}
