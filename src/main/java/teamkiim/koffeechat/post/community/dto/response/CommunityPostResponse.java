package teamkiim.koffeechat.post.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.post.dev.dto.response.ImageFileInfoDto;
import teamkiim.koffeechat.post.domain.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostResponse {

    private Long id;
    private String title;
    private String bodyContent;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private List<ImageFileInfoDto> imageFileInfoDtoList;
    private List<CommentInfoDto> commentInfoDtoList;

    public static CommunityPostResponse of(Post communityPost,
                                           List<ImageFileInfoDto> imageFileInfoDtoList,
                                           List<CommentInfoDto> commentInfoDtoList){

        return CommunityPostResponse.builder()
                .id(communityPost.getId())
                .title(communityPost.getTitle())
                .bodyContent(communityPost.getBodyContent())
                .viewCount(communityPost.getViewCount())
                .likeCount(communityPost.getLikeCount())
                .createdTime(communityPost.getCreatedTime())
                .modifiedTime(communityPost.getModifiedTime())
                .imageFileInfoDtoList(imageFileInfoDtoList)
                .commentInfoDtoList(commentInfoDtoList)
                .build();
    }
}
