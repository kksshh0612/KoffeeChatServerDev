package teamkiim.koffeechat.postlike;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 좋아요 기능 관련 리포지토리
 */
@Repository
@RequiredArgsConstructor
public class PostLikeRepository {

    private final EntityManager em;

    /**
     * 좋아요 생성
     */
    public void save(PostLike like) {
        em.persist(like);
    }

    /**
     * memberId, postId로 좋아요 삭제
     */
    public void delete(PostLike like) {
        em.remove(like);
    }

    /**
     * memberId, postId로 좋아요 찾기
     */
    public Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId) {
        List<PostLike> postLike= em.createQuery("select pl from PostLike pl where pl.member.id=:memberId and pl.post.id=:postId", PostLike.class)
                .setParameter("memberId", memberId)
                .setParameter("postId", postId)
                .getResultList();
        if (!postLike.isEmpty()) {
            return Optional.of(postLike.get(0));
        }else{
            return Optional.empty();
        }
    }
}
