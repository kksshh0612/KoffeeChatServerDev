package teamkiim.koffeechat.post.dev.dto.response;

import lombok.Data;
import teamkiim.koffeechat.post.dev.domain.DevPost;
import teamkiim.koffeechat.skillcategory.dto.SkillCategoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 내용 조회 response
 */
@Data
public class DevPostViewResponse {
    private Long id;
    private String memberNickname;  // 회원 닉네임
    private String title;  //제목
    private String bodyContent;  //내용
    private Long viewCount;  //조회수
    private Long likeCount;  //좋아요 수
    private LocalDateTime createdTime;  // 작성 시간
    private LocalDateTime modifiedTime;  // 수정 시간
    private List<SkillCategoryDto> skillCategories;  // 해시태그


    public DevPostViewResponse(DevPost post) {
        this.id = post.getId();
        this.memberNickname = post.getMember().getNickname();
        this.title = post.getTitle();
        this.bodyContent = post.getBodyContent();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.createdTime = post.getCreatedTime();
        this.modifiedTime = post.getModifiedTime();
        this.skillCategories = post.getSkillCategoryList().stream()
                .map(skillCategory -> new SkillCategoryDto(skillCategory))
                .collect(Collectors.toList());
    }

}
