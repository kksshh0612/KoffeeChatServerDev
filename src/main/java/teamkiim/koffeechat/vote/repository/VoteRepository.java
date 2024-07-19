package teamkiim.koffeechat.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.post.community.domain.CommunityPost;
import teamkiim.koffeechat.vote.domain.Vote;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByPost(Post post);
}
