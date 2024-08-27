package teamkiim.koffeechat.domain.post.common.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "내가 작성한 게시글 Response")
public class MyPostListResponse {

    private Long id;                                // PK
    private String title;                           // 제목
    private String bodyContent;                     // 본문
    private long viewCount;                         // 조회수
    private long likeCount;                         // 좋아요 수
    private long bookmarkCount;                     // 북마크 수
    private LocalDateTime createdTime;              // 작성 시간
    private LocalDateTime modifiedTime;             // 수정 시간
    private String nickname;                        // 작성자 닉네임
    private String profileImagePath;                // 작성자 프로필 이미지 path
    private String profileImageName;                // 작성자 프로필 이미지 이름

    private String imagePath;                       // 이미지 경로
    private String imageName;                       // 이미지 이름

    public static MyPostListResponse of(Post post) {
        return MyPostListResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .bodyContent(post.getBodyContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .bookmarkCount(post.getBookmarkCount())
                .createdTime(post.getCreatedTime())
                .nickname(post.getMember().getNickname())
                .profileImagePath(post.getMember().getProfileImagePath())
                .profileImageName(post.getMember().getProfileImageName())
                .imagePath(null)
                .imageName(null)
                .build();
    }

}
