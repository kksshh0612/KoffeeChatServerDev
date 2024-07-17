package teamkiim.koffeechat.post.community.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.common.domain.Post;

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
    private List<CommentInfoDto> commentInfoDtoList;
    private VoteResponse voteResponse;

    public static CommunityPostResponse of(Post communityPost, List<CommentInfoDto> commentInfoDtoList, VoteResponse voteResponse, Long loginMemberId, boolean isMemberLiked, boolean isMemberBookmarked){

        if(loginMemberId == communityPost.getMember().getId()){
            return CommunityPostResponse.builder()
                    .id(communityPost.getId())
                    .title(communityPost.getTitle())
                    .bodyContent(communityPost.getBodyContent())
                    .nickname(communityPost.getMember().getNickname())
                    .profileImagePath(communityPost.getMember().getProfileImagePath())
                    .profileImageName(communityPost.getMember().getProfileImageName())
                    .isMemberWritten(true)
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
        else{
            return CommunityPostResponse.builder()
                    .id(communityPost.getId())
                    .title(communityPost.getTitle())
                    .bodyContent(communityPost.getBodyContent())
                    .nickname(communityPost.getMember().getNickname())
                    .profileImagePath(communityPost.getMember().getProfileImagePath())
                    .profileImageName(communityPost.getMember().getProfileImageName())
                    .isMemberWritten(false)
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
}
