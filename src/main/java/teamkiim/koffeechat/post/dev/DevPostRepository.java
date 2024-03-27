package teamkiim.koffeechat.post.dev;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.post.PostRepository;

import java.util.List;

@Repository("devPostRepository")
public class DevPostRepository extends PostRepository {

    public DevPostRepository(EntityManager em) {
        super(em);
    }

    /**
     * 게시글 저장
     */
    public void save(DevPost post) {
        em.persist(post);
    }

    /**
     * 게시글 리스트 조회
     */
    public List<DevPost> findAllDev() {
        return em.createQuery("select p from DevPost p", DevPost.class)
                .getResultList();
    }

    /**
     * 게시글 한 개 조회
     */
    public DevPost findOneDev(Long id) {
        return em.find(DevPost.class, id);
    }
}
