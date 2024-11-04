package teamkiim.koffeechat.domain.post.dev.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.common.dto.response.CommentInfoDto;
import teamkiim.koffeechat.domain.post.common.dto.response.TagInfoDto;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevPostResponse {

    private String id;
    private String title;
    private String bodyContent;

    private String memberId;
    private String nickname;
    private String profileImageUrl;
    private boolean isMemberWritten;
    private boolean isMemberLiked;
    private boolean isMemberBookmarked;

    private long viewCount;
    private long likeCount;
    private long bookmarkCount;

    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    private String visualData;
    private List<SkillCategory> skillCategoryList;
    private List<TagInfoDto> tagInfoDtoList;
    private List<CommentInfoDto> commentInfoDtoList;

    public static DevPostResponse of(String postId, DevPost devPost, String memberId, List<TagInfoDto> tagInfoDtoList,
                                     List<CommentInfoDto> commentInfoDtoList,
                                     boolean isMemberLiked, boolean isMemberBookmarked, boolean isMemberWritten) {

        return DevPostResponse.builder()
                .id(postId)
                .title(devPost.getTitle())
                .bodyContent(devPost.getBodyContent())
                .memberId(memberId)
                .nickname(devPost.getMember().getNickname())
                .profileImageUrl(devPost.getMember().getProfileImageUrl())
                .isMemberWritten(isMemberWritten)
                .isMemberLiked(isMemberLiked)
                .isMemberBookmarked(isMemberBookmarked)
                .viewCount(devPost.getViewCount())
                .likeCount(devPost.getLikeCount())
                .bookmarkCount(devPost.getBookmarkCount())
                .createdTime(devPost.getCreatedTime())
                .modifiedTime(devPost.getModifiedTime())
                .visualData(devPost.getVisualData())
                .skillCategoryList(List.copyOf(devPost.getSkillCategoryList()))
                .tagInfoDtoList(tagInfoDtoList)
                .commentInfoDtoList(commentInfoDtoList)
                .build();
    }
}
