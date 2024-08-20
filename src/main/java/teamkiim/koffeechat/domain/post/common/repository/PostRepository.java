package teamkiim.koffeechat.domain.post.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long id);

    @Query("select p from Post p join fetch p.commentList where p.id = :id")
    Optional<Post> findByIdWithComments(@Param("id") Long id);

}
