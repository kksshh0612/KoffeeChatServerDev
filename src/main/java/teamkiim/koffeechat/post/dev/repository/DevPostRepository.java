package teamkiim.koffeechat.post.dev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.post.dev.domain.DevPost;

import java.util.Optional;

/**
 * 개발 게시글 Repository
 */
public interface DevPostRepository extends JpaRepository<DevPost, Long> {

    Optional<DevPost> findById(Long id);

    Page<DevPost> findAll(Pageable pageable);

}
