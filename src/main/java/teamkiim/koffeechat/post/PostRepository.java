package teamkiim.koffeechat.post;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

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
        Post post = em.find(Post.class, postId);
        if (post == null) throw new CustomException(ErrorCode.POST_NOT_FOUND);
        return post;
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
    public void deleteById(Long postId, Long requestMemberId) {

        Post post = findOne(postId);  //삭제할 게시글이 있는지 확인
        // 게시글 삭제 권한이 있는지 확인하기
        if (post.getMember().getId() != requestMemberId) throw new CustomException(ErrorCode.DELETE_FORBIDDEN);

        //게시글에 대한 좋아요(post_like) 데이터 삭제
        em.createQuery("delete from PostLike pl where pl.post.id=:postId")
                .setParameter("postId", postId)
                .executeUpdate();

        //게시글 삭제
        em.createQuery("delete from Post where id=:postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }
}
