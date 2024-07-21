package teamkiim.koffeechat.comment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.common.domain.Post;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="post_id")
    private Post post;                                  // 연관 게시물

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id")
    private Member member;                              // 작성자

    private String content;                             // 댓글 내용

    private LocalDateTime createdTime;                  // 댓글 작성 시간

    private LocalDateTime modifiedTime;                 // 댓글 수정 시간

    @Builder
    public Comment(Post post, Member member, String content, LocalDateTime createdTime, LocalDateTime modifiedTime) {
        this.post = post;
        this.member = member;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    //== 연관관계 주입 매서드 ==//
    public void injectPost(Post post){
        this.post = post;
    }

    //== 비지니스 로직 ==//
    public void modify(String content, LocalDateTime modifiedTime){

        this.content = content;
        this.modifiedTime = modifiedTime;
    }
}
