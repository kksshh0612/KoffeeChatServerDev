package teamkiim.koffeechat.domain.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.vote.domain.Vote;
import teamkiim.koffeechat.domain.vote.domain.VoteRecord;

import java.util.List;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    @Query("SELECT DISTINCT vr FROM VoteRecord vr JOIN vr.voteItem vi WHERE vi.vote=:vote AND vr.member =:member")
    List<VoteRecord> findByVoteAndMember(@Param("vote") Vote vote, @Param("member") Member member);

}
