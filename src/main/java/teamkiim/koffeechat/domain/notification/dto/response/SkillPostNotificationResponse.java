package teamkiim.koffeechat.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;

@Getter
@Builder
public class SkillPostNotificationResponse {

    private String receiverId;  //알림을 받는 회원

    private String title;

    private String url;

    private PostCategory postCategory;

    private NotificationType notificationType;

    public static SkillPostNotificationResponse of(ChildSkillCategory childSkillCategory, String receiverId,
                                                   String postId, Post post) {
        return SkillPostNotificationResponse.builder()
                .receiverId(receiverId)
                .title(childSkillCategory.toString())  //기술 이름
                .url(postId)
                .postCategory(post.getPostCategory())
                .notificationType(NotificationType.TECH_POST)
                .build();
    }
}
