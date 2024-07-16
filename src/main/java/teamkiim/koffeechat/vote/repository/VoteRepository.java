package teamkiim.koffeechat.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.vote.domain.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
