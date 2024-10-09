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
@Schema(description = "회원이 북마크한 게시글 리스트 Response")
public class BookmarkPostListResponse {

    private String id;                                // PK
    private String title;                           // 제목
    private String bodyContent;                     // 본문
    private Long viewCount;                         // 조회수
    private Long likeCount;                         // 좋아요 수
    private Long bookmarkCount;                     // 북마크 수
    private LocalDateTime createdTime;              // 작성 시간
    private LocalDateTime modifiedTime;             // 수정 시간
    private String nickname;                        // 작성자 닉네임
    private String profileImagePath;                // 작성자 프로필 이미지 path
    private String profileImageName;                // 작성자 프로필 이미지 이름

    private String imagePath;                       // 이미지 경로
    private String imageName;                       // 이미지 이름

    public static BookmarkPostListResponse of(String postId, Post post) {
        BookmarkPostListResponse response = BookmarkPostListResponse.builder()
                .id(postId)
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

        return response;
    }

}
