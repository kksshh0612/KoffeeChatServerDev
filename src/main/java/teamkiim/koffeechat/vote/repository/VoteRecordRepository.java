package teamkiim.koffeechat.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.vote.domain.VoteRecord;

import java.util.Optional;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    @Query("SELECT vr FROM VoteRecord vr JOIN vr.voteItem vi WHERE vi.vote.id=:voteId AND vr.member.id =:memberId")
    Optional<VoteRecord> findByVoteIdAndMemberId(Long voteId, Long memberId);
}
