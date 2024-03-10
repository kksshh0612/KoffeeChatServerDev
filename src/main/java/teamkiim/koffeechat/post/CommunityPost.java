package teamkiim.koffeechat.post;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Community")
@Getter
public class CommunityPost extends Post{

    //게시글 댓글 리스트
    @OneToMany
    @JoinColumn(name="comment_id")
    private List<Comment> commentList = new ArrayList<>();
}
