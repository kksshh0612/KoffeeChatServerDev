package teamkiim.koffeechat.post.community.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.comment.Comment;
import teamkiim.koffeechat.post.Post;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Community")
@Getter
@NoArgsConstructor
public class CommunityPost extends Post {

    //게시글 댓글 리스트
    @OneToMany
    @JoinColumn(name="comment_id")
    private List<Comment> commentList = new ArrayList<>();
}
