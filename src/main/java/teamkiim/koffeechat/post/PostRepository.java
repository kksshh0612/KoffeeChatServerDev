package teamkiim.koffeechat.post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글 관련 레파지토리
 * 게시글 삭제, 좋아요 기능
 */
@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final EntityManager em;

    /**
     * id로 게시글 조회
     */
    public Post findOne(Long postId) {
        return em.find(Post.class, postId);
    }

    /**
     * 키워드(제목, 내용)으로 게시글 조회
     */
    public List<Post> findByTitleAndContent(String keyword) {
        return em.createQuery("select p from Post p where p.title like concat('%', :keyword, '%') or p.bodyContent like concat('%', :keyword, '%') ", Post.class)
                .setParameter("keyword", keyword)
                .getResultList();
    }

    /**
     * 게시글 삭제  : entity가 null이면 IllegalArgumentException 반환
     */
    public void deleteById(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("postId cannot be null");
        }

        //게시글에 대한 좋아요(post_like) 데이터 삭제
        em.createQuery("delete from PostLike pl where pl.post.id=:postId")
                .setParameter("postId",postId)
                .executeUpdate();

        //게시글 삭제
        int deletedCount = em.createQuery("delete from Post where id=:postId")
                .setParameter("postId", postId)
                .executeUpdate();

        if (deletedCount == 0) {
            throw new EntityNotFoundException(postId + " 게시글이 존재하지 않습니다.");
        }
    }
}
