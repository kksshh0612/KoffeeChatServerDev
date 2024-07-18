package teamkiim.koffeechat.post.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.vote.domain.Vote;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long id);

}
