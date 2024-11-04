package teamkiim.koffeechat.domain.post.community.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.common.dto.response.CommentInfoDto;
import teamkiim.koffeechat.domain.post.common.dto.response.TagInfoDto;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostResponse {

    private String id;
    private String title;
    private String bodyContent;
    private VoteResponse voteResponse;

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

    private List<TagInfoDto> tagInfoDtoList;
    private List<CommentInfoDto> commentInfoDtoList;

    public static CommunityPostResponse of(String postId, CommunityPost communityPost, String memberId,
                                           List<TagInfoDto> tagInfoDtoList,
                                           List<CommentInfoDto> commentInfoDtoList,
                                           VoteResponse voteResponse, boolean isMemberLiked, boolean isMemberBookmarked,
                                           boolean isMemberWritten) {

        return CommunityPostResponse.builder()
                .id(postId)
                .title(communityPost.getTitle())
                .bodyContent(communityPost.getBodyContent())
                .voteResponse(voteResponse)
                .memberId(memberId)
                .nickname(communityPost.getMember().getNickname())
                .profileImageUrl(communityPost.getMember().getProfileImageUrl())
                .isMemberWritten(isMemberWritten)
                .isMemberLiked(isMemberLiked)
                .isMemberBookmarked(isMemberBookmarked)
                .viewCount(communityPost.getViewCount())
                .likeCount(communityPost.getLikeCount())
                .bookmarkCount(communityPost.getBookmarkCount())
                .createdTime(communityPost.getCreatedTime())
                .modifiedTime(communityPost.getModifiedTime())
                .tagInfoDtoList(tagInfoDtoList)
                .commentInfoDtoList(commentInfoDtoList)
                .build();
    }
}
