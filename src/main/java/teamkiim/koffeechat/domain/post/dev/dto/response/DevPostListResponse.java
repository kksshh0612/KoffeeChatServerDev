package teamkiim.koffeechat.domain.post.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.post.common.dto.response.TagInfoDto;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@AllArgsConstructor
@Builder
public class DevPostListResponse {

    private Long id;                                // PK
    private String title;                           // 제목
    private String bodyContent;                     // 본문
    private List<TagInfoDto> tagDtoList;            //태그 리스트
    private List<SkillCategory> skillCategoryList;  //기술 카테고리 리스트

    private long viewCount;                         // 조회수
    private long likeCount;                         // 좋아요 수
    private long bookmarkCount;                     // 북마크 수

    private LocalDateTime createdTime;              // 작성 시간
    private LocalDateTime modifiedTime;

    private String nickname;                        // 작성자 닉네임
    private String profileImageUrl;
    private String contentImageUrl;

    public static DevPostListResponse of(DevPost devPost) {

        DevPostListResponse response = DevPostListResponse.builder()
                .id(devPost.getId())
                .title(devPost.getTitle())
                .bodyContent(devPost.getBodyContent())
                .tagDtoList(devPost.getPostTagList().stream().map(postTag -> TagInfoDto.of(postTag.getTag())).toList())
                .skillCategoryList(devPost.getSkillCategoryList())
                .viewCount(devPost.getViewCount())
                .likeCount(devPost.getLikeCount())
                .bookmarkCount(devPost.getBookmarkCount())
                .createdTime(devPost.getCreatedTime())
                .modifiedTime(devPost.getModifiedTime())
                .nickname(devPost.getMember().getNickname())
                .profileImageUrl(devPost.getMember().getProfileImageUrl())
                .contentImageUrl(null)
                .build();

        if (!devPost.getFileList().isEmpty()) {
            response.setImageInfo(devPost.getFileList().get(0));
        }

        return response;
    }

    private void setImageInfo(File file) {

        this.contentImageUrl = file.getUrl();
    }
}
