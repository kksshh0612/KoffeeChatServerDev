package teamkiim.koffeechat.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.vote.domain.Vote;
import teamkiim.koffeechat.vote.domain.VoteItem;
import teamkiim.koffeechat.vote.domain.VoteRecord;

import java.util.List;
import java.util.Optional;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    @Query("SELECT DISTINCT vr FROM VoteRecord vr JOIN vr.voteItem vi WHERE vi.vote=:vote AND vr.member =:member")
    List<VoteRecord> findByVoteAndMember(Vote vote, Member member);

    @Query("SELECT vr FROM VoteRecord vr WHERE vr.voteItem IN :newVoteItems")
    List<VoteRecord> findByVoteItems(List<VoteItem> newVoteItems);
}
