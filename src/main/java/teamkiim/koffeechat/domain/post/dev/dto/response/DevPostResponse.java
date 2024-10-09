package teamkiim.koffeechat.domain.post.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.common.dto.response.CommentInfoDto;
import teamkiim.koffeechat.domain.post.common.dto.response.TagInfoDto;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevPostResponse {

    private String id;
    private String title;
    private String bodyContent;

    private String memberId;  //글 작성자의 암호화된 pk
    private String nickname;
    private String profileImagePath;
    private String profileImageName;

    private boolean isMemberWritten;
    private boolean isMemberLiked;
    private boolean isMemberBookmarked;

    private Long viewCount;
    private Long likeCount;
    private Long bookmarkCount;

    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    private String visualData;
    private List<SkillCategory> skillCategoryList;
    private List<TagInfoDto> tagInfoDtoList;
    private List<CommentInfoDto> commentInfoDtoList;

    public static DevPostResponse of(String postId, DevPost devPost, String memberId, List<TagInfoDto> tagInfoDtoList, List<CommentInfoDto> commentInfoDtoList,
                                     boolean isMemberLiked, boolean isMemberBookmarked, boolean isMemberWritten) {

        return DevPostResponse.builder()
                .id(postId)
                .title(devPost.getTitle())
                .bodyContent(devPost.getBodyContent())
                .memberId(memberId)
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
                .visualData(devPost.getVisualData())
                .skillCategoryList(List.copyOf(devPost.getSkillCategoryList()))
                .tagInfoDtoList(tagInfoDtoList)
                .commentInfoDtoList(commentInfoDtoList)
                .build();
    }
}
