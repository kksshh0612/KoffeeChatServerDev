package teamkiim.koffeechat.domain.comment.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.comment.dto.request.ModifyCommentServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 수정 Request")
public class ModifyCommentRequest {

    @Schema(description = "댓글 pk", example = "1")
    private String id;

    @Schema(description = "댓글 내용", example = "댓글 내용 수정입니다.")
    @NotBlank(message = "댓글을 작성해주세요.")
    private String content;

    public ModifyCommentServiceRequest toServiceRequest(Long commentId) {
        return ModifyCommentServiceRequest.builder()
                .id(commentId)
                .content(this.content)
                .build();
    }
}
