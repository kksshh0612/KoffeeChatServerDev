package teamkiim.koffeechat.postlike;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.Member;
import teamkiim.koffeechat.post.Post;

/**
 * 게시글 좋아요
 */
@Entity
@Getter
@NoArgsConstructor
public class PostLike {

    @Id
    @GeneratedValue
    @Column(name = "post_like_id")
    private Long id;  //post_like_id

    //회원은 게시글마다 좋아요를 표현할 수 있다.
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 좋아요 생성 값 세팅
     */
    public void create(Member member, Post post) {
        this.member=member;
        this.post=post;
    }
}
