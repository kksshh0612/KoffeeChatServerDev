package teamkiim.koffeechat.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.vote.domain.Vote;
import teamkiim.koffeechat.vote.domain.VoteItem;

import java.util.List;
import java.util.Optional;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {

    @Query("SELECT vi.vote FROM VoteItem vi WHERE vi.id= :voteItemId")
    Optional<Vote> findVoteByVoteItemId(Long voteItemId);

    List<VoteItem> findByVote(Vote vote);

    @Query("SELECT vi FROM VoteItem vi WHERE vi.vote.post=:post AND vi.id IN :items")
    List<VoteItem> findAllByPostAndIds(Post post, List<Long> items);
}
