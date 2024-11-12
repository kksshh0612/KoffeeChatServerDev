package teamkiim.koffeechat.domain.comment.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.global.auditing.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;                                  // 연관 게시물

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;                              // 작성자

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;                             // 댓글 내용

    @Builder
    public Comment(Post post, Member member, String content) {
        this.post = post;
        this.member = member;
        this.content = content;
    }

    //== 연관관계 주입 매서드 ==//
    public void injectPost(Post post) {
        this.post = post;
    }

    //== 비지니스 로직 ==//
    public void modify(String content) {

        this.content = content;
    }
}
