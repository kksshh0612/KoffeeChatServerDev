package teamkiim.koffeechat.domain.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentServiceRequest {

    private String content;
    private LocalDateTime currDateTime;

    public Comment toEntity(Post post, Member member){
        return Comment.builder()
                .post(post)
                .member(member)
                .content(this.content)
                .build();
    }
}
