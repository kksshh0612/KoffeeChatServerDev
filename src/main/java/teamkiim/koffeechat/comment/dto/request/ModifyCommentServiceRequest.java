package teamkiim.koffeechat.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.comment.domain.Comment;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.domain.Post;

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
