package teamkiim.koffeechat.comment.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.comment.dto.request.CommentServiceRequest;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "댓글을 작성해주세요.")
    String content;

    public CommentServiceRequest toServiceRequest(LocalDateTime currDateTime){
        return CommentServiceRequest.builder()
                .content(this.content)
                .currDateTime(currDateTime)
                .build();
    }
}
