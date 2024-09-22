package teamkiim.koffeechat.domain.chat.room.tech.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.tech.domain.TechChatRoom;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTechChatRoomServiceRequest {

    private String name;
    private ParentSkillCategory parentSkillCategory;
    private ChildSkillCategory childSkillCategory;

    public TechChatRoom toEntity(){
        return TechChatRoom.builder()
                .name(name)
                .memberCount(1)
                .skillCategory(new SkillCategory(parentSkillCategory, childSkillCategory))
                .build();
    }
}
