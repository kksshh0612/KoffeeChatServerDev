package teamkiim.koffeechat.bookmark.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.post.common.domain.Post;

import java.util.List;

public interface BookmarkPostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByIdIn(List<Long> ids, Pageable pageable);

}
