package teamkiim.koffeechat.post.community.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.file.domain.File;
import teamkiim.koffeechat.post.community.domain.CommunityPost;
import teamkiim.koffeechat.post.dev.domain.DevPost;
import teamkiim.koffeechat.post.dev.dto.response.DevPostListResponse;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostListResponse {

    private Long id;                                // PK
    private String title;                           // 제목
    private String bodyContent;                     // 본문
    private Long viewCount;                         // 조회수
    private Long likeCount;                         // 좋아요 수
    private Long bookmarkCount;                     // 북마크 수
    private LocalDateTime createdTime;              // 작성 시간
    private String nickname;                        // 작성자 닉네임
    private String profileImagePath;                // 작성자 프로필 이미지 path
    private String profileImageName;                // 작성자 프로필 이미지 이름

    private String imagePath;                       // 이미지 경로
    private String imageName;                       // 이미지 이름

    @JsonIgnore
    private File imageInfo;

    public static CommunityPostListResponse of(CommunityPost communityPost){

        CommunityPostListResponse response = CommunityPostListResponse.builder()
                .id(communityPost.getId())
                .title(communityPost.getTitle())
                .bodyContent(communityPost.getBodyContent())
                .viewCount(communityPost.getViewCount())
                .likeCount(communityPost.getLikeCount())
                .bookmarkCount(communityPost.getBookmarkCount())
                .createdTime(communityPost.getCreatedTime())
                .nickname(communityPost.getMember().getNickname())
                .profileImagePath(communityPost.getMember().getProfileImagePath())
                .profileImageName(communityPost.getMember().getProfileImageName())
                .imagePath(null)
                .imageName(null)
                .build();

        if(!communityPost.getFileList().isEmpty()){
            response.setImageInfo(communityPost.getFileList().get(0));
        }

        return response;
    }

    private void setImageInfo(File file){

        this.imagePath = file.getPath();
        this.imageName = file.getName();
    }
}
