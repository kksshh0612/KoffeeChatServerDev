package teamkiim.koffeechat.domain.vote.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

/**
 * community 게시판 투표 기능에 대한 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vote_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="post_id")
    private Post post;                                     //연관 게시물

    private String title;                                  //투표 제목

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteItem> voteItems = new ArrayList<>();            //투표 항목

    public Vote(Post post, String title) {
        this.post=post;
        this.title=title;
    }

    //== 연관관계 주입 매서드 ==//
    public void injectPost(Post post) {
        this.post=post;
    }

    //== 연관관계 편의 매서드 ==//
    public void addVoteItem(VoteItem voteItem) {                    //항목 추가
        this.voteItems.add(voteItem);
        voteItem.injectVote(this);
    }

    //===================================//

    //== 비즈니스 로직 ==//
    public void modify(String title) {
        this.title=title;
    }

}
