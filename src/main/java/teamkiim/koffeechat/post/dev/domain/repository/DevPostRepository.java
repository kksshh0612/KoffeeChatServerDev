package teamkiim.koffeechat.post.dev.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.post.dev.domain.DevPost;

import java.util.List;

/**
 * 개발 게시글 관련 리포지토리
 */
@Repository
@RequiredArgsConstructor
public class DevPostRepository {

    private final EntityManager em;

    /**
     * 게시글 저장
     */
    public void save(DevPost post) {
        em.persist(post);
    }

    /**
     * 개발 게시글 한 개 조회
     */
    public DevPost findOneDev(Long id) {
        return em.find(DevPost.class, id);
    }

    /**
     * 게시글 리스트 조회
     */
    public List<DevPost> findAllDev() {
        return em.createQuery("select p from DevPost p", DevPost.class)
                .getResultList();
    }

    /**
     * 카테고리로 게시글 조회
     */
    public List<DevPost> findByCategories(List<String> categoryNames) {
        return em.createQuery("select DISTINCT p from Post p join p.skillCategoryList c where c.name in :categoryNames", DevPost.class)
                .setParameter("categoryNames", categoryNames)
                .getResultList();
    }
}
