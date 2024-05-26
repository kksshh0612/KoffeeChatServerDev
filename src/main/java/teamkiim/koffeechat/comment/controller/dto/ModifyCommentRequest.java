package teamkiim.koffeechat.comment.controller.dto;

import jakarta.validation.constraints.NotBlank;
import teamkiim.koffeechat.comment.dto.request.ModifyCommentServiceRequest;

import java.time.LocalDateTime;

public class ModifyCommentRequest {

    private Long id;
    @NotBlank(message = "댓글을 작성해주세요.")
    String content;

    public ModifyCommentServiceRequest toServiceRequest(LocalDateTime currDateTime){
        return ModifyCommentServiceRequest.builder()
                .id(this.id)
                .content(this.content)
                .currDateTime(currDateTime)
                .build();
    }
}
