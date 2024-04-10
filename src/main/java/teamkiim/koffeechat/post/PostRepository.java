package teamkiim.koffeechat.post;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 관련 레파지토리
 * 좋아요 기능
 */
@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final EntityManager em;

    public Post findOne(Long postId) {
        return em.find(Post.class, postId);
    }
}
