package teamkiim.koffeechat.post.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.post.community.domain.CommunityPost;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findById(Long id);
}
