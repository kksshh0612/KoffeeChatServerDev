package teamkiim.koffeechat.domain.post.dev.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.common.domain.Post;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevPostListRelatedSkill {

    private String postId;
    private String title;
    private LocalDateTime createdTime;
    private String content;
    private String profileImageUrl;

    public static DevPostListRelatedSkill of(Post post, String encryptPostId, String profileImageUrl) {
        return DevPostListRelatedSkill.builder()
                .postId(encryptPostId)
                .title(post.getTitle())
                .createdTime(post.getCreatedTime())
                .content(post.getBodyContent())
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
