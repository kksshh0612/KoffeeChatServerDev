package teamkiim.koffeechat.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyCommentServiceRequest {

    private Long id;
    private String content;
    private LocalDateTime currDateTime;
    
}
