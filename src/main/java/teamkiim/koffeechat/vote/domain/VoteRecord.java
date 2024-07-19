package teamkiim.koffeechat.vote.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.Member;

/**
 * member가 투표한 항목을 저장하는 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
public class VoteRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vote_record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vote_item_id")
    private VoteItem voteItem;                                  //어떤 투표 항목에 대한 기록인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;                                      //투표한 멤버

    @Builder
    public VoteRecord(Member member, VoteItem voteItem) {
        this.member = member;
        this.voteItem = voteItem;
    }

    public static VoteRecord create(Member member, VoteItem voteItem) {
        return VoteRecord.builder()
                .member(member)
                .voteItem(voteItem)
                .build();
    }
}
