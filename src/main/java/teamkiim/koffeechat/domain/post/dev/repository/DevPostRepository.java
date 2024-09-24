package teamkiim.koffeechat.domain.post.dev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;

import java.util.List;
import java.util.Optional;

/**
 * 개발 게시글 Repository
 */
public interface DevPostRepository extends JpaRepository<DevPost, Long> {

    Optional<DevPost> findById(Long id);

    @Query("select p from DevPost p where p.isEditing = false and p.deleted = false")
    Page<DevPost> findAllCompletePostBySkillCategory(Pageable pageable);

    @Query("select p from DevPost p join fetch p.skillCategoryList sc where p.isEditing = false and p.deleted = false and sc.childSkillCategory in :childSkillCategoryList")
    Page<DevPost> findAllCompletePostBySkillCategory(@Param("childSkillCategoryList") List<ChildSkillCategory> childSkillCategoryList, Pageable pageable);

    @Query("select p from DevPost p join fetch PostTag pt on pt.post = p " +
            "where pt.tag.content in (:tagContents) and p.isEditing = false and p.deleted = false " +
            "group by p order by count(pt.tag) desc")
    Page<DevPost> findAllCompletePostByTags(List<String> tagContents, PageRequest pageRequest);
}
