package teamkiim.koffeechat.domain.post.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.post.common.domain.Post;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원이 북마크한 게시글 리스트 Response")
public class BookmarkPostListResponse {

    private String id;                              // PK
    private String title;                           // 제목
    private String bodyContent;                     // 본문
    private long viewCount;                         // 조회수
    private long likeCount;                         // 좋아요 수
    private long bookmarkCount;                     // 북마크 수
    private LocalDateTime createdTime;              // 작성 시간
    private LocalDateTime modifiedTime;             // 수정 시간
    private String nickname;                        // 작성자 닉네임
    private String profileImageUrl;                 // 작성자 프로필 이미지 경로
    private String contentImageUrl;                 // 게시글에 포함된 이미지 url

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
                .profileImageUrl(post.getMember().getProfileImageUrl())
                .contentImageUrl(null)
                .build();

        if (!post.getFileList().isEmpty()) {
            response.setImageInfo(post.getFileList().get(0));
        }

        return response;
    }

    private void setImageInfo(File file) {

        this.contentImageUrl = file.getUrl();
    }
}
