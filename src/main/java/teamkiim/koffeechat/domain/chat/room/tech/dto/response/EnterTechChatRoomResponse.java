package teamkiim.koffeechat.domain.chat.room.tech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.tech.domain.TechChatRoom;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnterTechChatRoomResponse {

    private ChildSkillCategory childSkillCategory;
    private int currentMemberSize;
    private String memberId;

    public static EnterTechChatRoomResponse of(TechChatRoom techChatRoom, String memberId) {
        return EnterTechChatRoomResponse.builder()
                .childSkillCategory(techChatRoom.getSkillCategory().getChildSkillCategory())
                .currentMemberSize(techChatRoom.getCurrentMemberSize())
                .memberId(memberId)
                .build();
    }
}
