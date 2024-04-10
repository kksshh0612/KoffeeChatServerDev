package teamkiim.koffeechat.post.community.domain.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import teamkiim.koffeechat.post.community.domain.CommunityPost;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommunityPostRepository {
    private final EntityManager em;

    /**
     * 게시글 저장
     */
    public void save(CommunityPost post) {
        em.persist(post);
    }

    /**
     * 게시글 한 개 조회
     */
    public CommunityPost findOne(Long id) {
        return em.find(CommunityPost.class, id);
    }

    /**
     * 게시글 리스트 조회
     */
    public List<CommunityPost> findAll() {
        return em.createQuery("select p from CommunityPost p", CommunityPost.class)
                .getResultList();
    }

}
