package teamkiim.koffeechat.domain.post.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostResponse {

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
    private List<CommentInfoDto> commentInfoDtoList;
    private VoteResponse voteResponse;

    public static CommunityPostResponse of(CommunityPost communityPost, List<CommentInfoDto> commentInfoDtoList,
                                           VoteResponse voteResponse, boolean isMemberLiked, boolean isMemberBookmarked, boolean isMemberWritten) {

        return CommunityPostResponse.builder()
                .id(communityPost.getId())
                .title(communityPost.getTitle())
                .bodyContent(communityPost.getBodyContent())
                .memberId(communityPost.getMember().getId())
                .nickname(communityPost.getMember().getNickname())
                .profileImagePath(communityPost.getMember().getProfileImagePath())
                .profileImageName(communityPost.getMember().getProfileImageName())
                .isMemberWritten(isMemberWritten)
                .isMemberLiked(isMemberLiked)
                .isMemberBookmarked(isMemberBookmarked)
                .viewCount(communityPost.getViewCount())
                .likeCount(communityPost.getLikeCount())
                .bookmarkCount(communityPost.getBookmarkCount())
                .createdTime(communityPost.getCreatedTime())
                .modifiedTime(communityPost.getModifiedTime())
                .commentInfoDtoList(commentInfoDtoList)
                .voteResponse(voteResponse)
                .build();
    }
}
