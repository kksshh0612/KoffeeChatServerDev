package teamkiim.koffeechat.post.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.post.community.domain.CommunityPost;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findById(Long id);

    @Query("select p from CommunityPost p where p.isEditing = false")
    Page<CommunityPost> findAllCompletePost(Pageable pageable);
}
