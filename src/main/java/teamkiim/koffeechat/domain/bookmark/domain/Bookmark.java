package teamkiim.koffeechat.domain.bookmark.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class Bookmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bookmark_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id")
    private Member member;                                      //북마크한 회원

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="post_id")
    private Post post;                                          //회원이 북마크한 게시물

    @Builder
    public Bookmark(Member member, Post post) {
        this.member = member;
        this.post = post;
    }

    public static Bookmark create(Member member, Post post) {
        return Bookmark.builder()
                .member(member)
                .post(post)
                .build();
    }
}
