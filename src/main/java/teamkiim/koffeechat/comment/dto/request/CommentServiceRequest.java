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
public class CommentServiceRequest {

    private String content;
    private LocalDateTime currDateTime;

    public Comment toEntity(Post post, Member member){
        return Comment.builder()
                .post(post)
                .member(member)
                .content(this.content)
                .createdTime(this.currDateTime)
                .modifiedTime(null)
                .build();
    }
}
