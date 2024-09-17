package teamkiim.koffeechat.domain.comment.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.comment.domain.Comment;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "내가 쓴 댓글 리스트 response")
public class MyCommentListResponse {

    @Schema(description = "게시글 pk")
    Long post_id;

    @Schema(description = "댓글 pk")
    Long comment_id;

    @Schema(description = "댓글 내용")
    String content;

    @Schema(description = "댓글을 작성한 시간")
    private LocalDateTime createdTime;

    public static MyCommentListResponse of(Comment comment, boolean isPostExist) {
        return MyCommentListResponse.builder()
                .post_id(isPostExist ? comment.getPost().getId() : null)
                .comment_id(comment.getId())
                .content(comment.getContent())
                .createdTime(comment.getCreatedTime())
                .build();
    }
}