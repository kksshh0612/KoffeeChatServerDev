package teamkiim.koffeechat.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.comment.domain.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findById(Long id);
}
