package teamkiim.koffeechat.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.post.domain.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long id);
}
