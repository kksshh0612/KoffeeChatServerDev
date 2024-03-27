package teamkiim.koffeechat.post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public abstract class PostRepository {

    protected final EntityManager em;

    public PostRepository(EntityManager em) {
        this.em = em;
    }

    /**
     * 게시글 저장
     */
    public void save(Post post) {
        em.persist(post);
    }

    /**
     * 게시글 한 개 조회
     */
    public Post findOne(Long id) {
        return em.find(Post.class, id);
    }

    /**
     * 게시글 리스트 조회
     */
    public List<Post> findAll() {
        return em.createQuery("select p from Post p", Post.class)
                .getResultList();
    }

    /**
     * 제목으로 게시글 조회
     */
    public List<Post> findByTitle(String title) {
        return em.createQuery("select p from Post p where p.title like concat('%', :title, '%')", Post.class)
                .setParameter("title", title)
                .getResultList();
    }

    /**
     * 게시글 삭제  : entity가 null이면 IllegalArgumentException 반환
     */
    public void deleteById(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("postId cannot be null");
        }

        int deletedCount = em.createQuery("delete from Post where id=:postId")
                .setParameter("postId", postId)
                .executeUpdate();


        if (deletedCount == 0) {
            throw new EntityNotFoundException(postId + " 게시글이 존재하지 않습니다.");
        }
    }
}
