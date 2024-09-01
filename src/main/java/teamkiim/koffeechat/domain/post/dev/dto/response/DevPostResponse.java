package teamkiim.koffeechat.domain.post.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.community.dto.response.CommentInfoDto;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevPostResponse {

    private Long id;
    private String title;
    private String bodyContent;
    private Long memberId;
    private String nickname;
    private String profileImagePath;
    private String profileImageName;
    private boolean isMemberWritten;
    private boolean isMemberLiked;
    private boolean isMemberBookmarked;
    private long viewCount;
    private long likeCount;
    private long bookmarkCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private List<SkillCategory> skillCategoryList;
    private List<CommentInfoDto> commentInfoDtoList;

    public static DevPostResponse of(DevPost devPost, List<CommentInfoDto> commentInfoDtoList, boolean isMemberLiked, boolean isMemberBookmarked, boolean isMemberWritten) {

        return DevPostResponse.builder()
                .id(devPost.getId())
                .title(devPost.getTitle())
                .bodyContent(devPost.getBodyContent())
                .memberId(devPost.getMember().getId())
                .nickname(devPost.getMember().getNickname())
                .profileImagePath(devPost.getMember().getProfileImagePath())
                .profileImageName(devPost.getMember().getProfileImageName())
                .isMemberWritten(isMemberWritten)
                .isMemberLiked(isMemberLiked)
                .isMemberBookmarked(isMemberBookmarked)
                .viewCount(devPost.getViewCount())
                .likeCount(devPost.getLikeCount())
                .bookmarkCount(devPost.getBookmarkCount())
                .createdTime(devPost.getCreatedTime())
                .modifiedTime(devPost.getModifiedTime())
                .skillCategoryList(List.copyOf(devPost.getSkillCategoryList()))
                .commentInfoDtoList(commentInfoDtoList)
                .build();
    }
}
