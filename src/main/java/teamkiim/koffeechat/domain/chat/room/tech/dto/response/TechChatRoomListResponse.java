package teamkiim.koffeechat.domain.chat.room.tech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.tech.domain.TechChatRoom;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechChatRoomListResponse {

    private String chatRoomId;
    private ParentSkillCategory parentSkillCategory;
    private ChildSkillCategory childSkillCategory;
    private int currentMemberSize;

    public static TechChatRoomListResponse of(TechChatRoom chatRoom, String encryptChatRoomId) {
        return TechChatRoomListResponse.builder()
                .chatRoomId(encryptChatRoomId)
                .parentSkillCategory(chatRoom.getSkillCategory().getParentSkillCategory())
                .childSkillCategory(chatRoom.getSkillCategory().getChildSkillCategory())
                .currentMemberSize(chatRoom.getCurrentMemberSize())
                .build();

    }
}
