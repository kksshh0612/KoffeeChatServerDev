package teamkiim.koffeechat.post.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.post.dev.domain.DevPost;
import teamkiim.koffeechat.post.dev.domain.SkillCategory;

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
    private String nickname;
    private String profileImagePath;
    private String profileImageName;
    private boolean isMemberWritten;
    private boolean isMemberLiked;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private List<SkillCategory> skillCategoryList;

    public static DevPostResponse of(DevPost devPost, Long loginMemberId, boolean isMemberLiked){

        boolean isMemberWritten = loginMemberId.equals(devPost.getMember().getId());

        return DevPostResponse.builder()
                .id(devPost.getId())
                .title(devPost.getTitle())
                .bodyContent(devPost.getBodyContent())
                .nickname(devPost.getMember().getNickname())
                .profileImagePath(devPost.getMember().getProfileImagePath())
                .profileImageName(devPost.getMember().getProfileImageName())
                .isMemberWritten(isMemberWritten)
                .isMemberLiked(isMemberLiked)
                .viewCount(devPost.getViewCount())
                .likeCount(devPost.getLikeCount())
                .createdTime(devPost.getCreatedTime())
                .modifiedTime(devPost.getModifiedTime())
                .skillCategoryList(List.copyOf(devPost.getSkillCategoryList()))
                .build();
    }

}
