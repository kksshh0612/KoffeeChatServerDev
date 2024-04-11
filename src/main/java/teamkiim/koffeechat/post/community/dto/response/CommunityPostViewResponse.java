package teamkiim.koffeechat.post.community.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import teamkiim.koffeechat.post.community.domain.CommunityPost;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommunityPostViewResponse {
    private Long id;
    private String memberNickname;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdTime;  // 작성 시간
    private LocalDateTime modifiedTime; //수정 시간
    private List<String> skillCategories;  // 해시태그

    //fileList

    //comments

    public void set(CommunityPost post, List<String> skillCategories) {
        this.id = post.getId();
        this.memberNickname= post.getMember().getNickname();
        this.title = post.getTitle();
        this.bodyContent = post.getBodyContent();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.createdTime = post.getCreatedTime();
        this.modifiedTime = post.getModifiedTime();
        this.skillCategories= skillCategories;
    }
}
