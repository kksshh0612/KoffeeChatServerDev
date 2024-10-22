package teamkiim.koffeechat.domain.chat.room.tech.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.tech.domain.TechChatRoom;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTechChatRoomServiceRequest {

    private ParentSkillCategory parentSkillCategory;
    private ChildSkillCategory childSkillCategory;

    public TechChatRoom toEntity(int maxMemberSize){
        return TechChatRoom.builder()
                .chatRoomType(ChatRoomType.TECH)
                .name(childSkillCategory.getName())
                .lastMessageTime(null)
                .currentMemberSize(0)
                .maxMemberSize(maxMemberSize)
                .skillCategory(new SkillCategory(parentSkillCategory, childSkillCategory))
                .build();
    }
}
