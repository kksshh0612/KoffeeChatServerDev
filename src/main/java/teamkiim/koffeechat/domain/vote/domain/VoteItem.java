package teamkiim.koffeechat.domain.vote.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 투표의 각 항목에 대한 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
public class VoteItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vote_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vote_id")
    private Vote vote;                                      //연관된 투표

    private String itemText;                                //투표 항목

    private Long voteCount;                                 //항목에 대한 투표수

    @OneToMany(mappedBy = "voteItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteRecord> voteRecords = new ArrayList<>();  //투표 기록


    public VoteItem(Vote vote, String itemText) {
        this.vote= vote;
        this.itemText=itemText;
        this.voteCount=0L;
    }

    //== 연관관계 주입 매서드 ==//
    public void injectVote(Vote vote) {
        this.vote=vote;
    }

    public void addVoteRecord(VoteRecord voteRecord) {
        this.voteRecords.add(voteRecord);
    }

    public void removeVoteRecord(VoteRecord voteRecord) {
        this.voteRecords.remove(voteRecord);
    }

    //== 비즈니스 로직 ==//
    public void addVoteCount() {
        this.voteCount++;
    }

    public void removeVoteCount() {
        this.voteCount--;
    }


}
