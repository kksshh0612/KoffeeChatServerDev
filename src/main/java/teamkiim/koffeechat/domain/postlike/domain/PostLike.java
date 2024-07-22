package teamkiim.koffeechat.domain.postlike.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;

@Entity
@Getter
@NoArgsConstructor
public class PostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;                          // 좋아요 누른 회원

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;                              // 연관 게시물

    @Builder
    public PostLike(Member member, Post post) {
        this.member = member;
        this.post = post;
    }

    public static PostLike create(Member member, Post post){
        return PostLike.builder()
                .member(member)
                .post(post)
                .build();
    }
}
