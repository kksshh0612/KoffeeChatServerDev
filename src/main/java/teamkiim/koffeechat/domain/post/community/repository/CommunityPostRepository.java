package teamkiim.koffeechat.domain.post.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;

import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findById(Long id);

    @Query("select p from CommunityPost p where p.isEditing = false and p.deleted = false")
    Page<CommunityPost> findAllCompletePost(Pageable pageable);

    @Query("select p from CommunityPost p join fetch PostTag pt on pt.post = p " +
            "where pt.tag.content in (:tagContents) and p.isEditing = false and p.deleted = false " +
            "group by p order by count(pt.tag) desc")
    Page<CommunityPost> findAllCompletePostByTags(@Param("tagContents") List<String> tagContent, Pageable pageable);

    @Query("SELECT p FROM CommunityPost p WHERE p.title LIKE %:keyword% AND p.isEditing = false AND p.deleted = false")
    Page<CommunityPost> findAllCompletePostByKeyword(@Param("keyword") String keyword, PageRequest pageRequest);
}
