package teamkiim.koffeechat.domain.post.dev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.util.List;
import java.util.Optional;

/**
 * 개발 게시글 Repository
 */
public interface DevPostRepository extends JpaRepository<DevPost, Long> {

    Optional<DevPost> findById(Long id);

    @Query("select p from DevPost p where p.isEditing = false")
    Page<DevPost> findAllCompletePost(Pageable pageable);

    @Query("select p from DevPost p join fetch p.skillCategoryList sc where sc.childSkillCategory in :childSkillCategoryList and p.isEditing = false")
    Page<DevPost> findAllCompletePostBySkillCategoryList(@Param("childSkillCategoryList") List<ChildSkillCategory> childSkillCategoryList, PageRequest pageRequest);

    @Query("select p from DevPost p join fetch PostTag pt on pt.post = p " +
            "where pt.tag.content in (:tagContents) and p.isEditing = false " +
            "group by p order by count(pt.tag) desc")
    Page<DevPost> findAllCompletePostByTags(@Param("tagContents") List<String> tagContents, PageRequest pageRequest);

    @Query("select p from DevPost p join fetch p.skillCategoryList sc join fetch PostTag pt on pt.post = p " +
            "where sc.childSkillCategory in :childSkillCategoryList and pt.tag.content in (:tagContents) and p.isEditing = false " +
            "group by p order by count(pt.tag) desc")
    Page<DevPost> findAllCompletePostBySkillCategoryAndTags(@Param("childSkillCategoryList") List<ChildSkillCategory> childSkillCategoryList, @Param("tagContents") List<String> tagContents, PageRequest pageRequest);

    @Query("select p from DevPost p where p.title like %:keyword% and p.isEditing = false")
    Page<DevPost> findAllCompletePostByKeyword(@Param("keyword") String keyword, PageRequest pageRequest);

    @Query("select p from DevPost p join fetch PostTag pt on pt.post = p " +
            "where p.title like %:keyword% and pt.tag.content in (:tagContents) and p.isEditing = false " +
            "group by p order by count(pt.tag) desc")
    Page<DevPost> findAllCompletePostByKeywordAndTags(@Param("keyword") String keyword, @Param("tagContents") List<String> tagContents, PageRequest pageRequest);

    @Query("select p from DevPost p join fetch p.skillCategoryList sc " +
            "where p.title like %:keyword% and sc.childSkillCategory in :childSkillCategoryList and p.isEditing = false")
    Page<DevPost> findAllCompletePostByKeywordAndSkillCategory(@Param("keyword") String keyword, @Param("childSkillCategoryList") List<ChildSkillCategory> childSkillCategoryList, PageRequest pageRequest);

    //기술 채팅방의 카테고리 관련 게시글 조회
    @Query("select distinct p from DevPost p join fetch p.skillCategoryList sc where :skillCategory member of p.skillCategoryList and p.isEditing = false")
    Page<DevPost> findAllCompletePostBySkillCategory(@Param("skillCategory") SkillCategory skillCategory, Pageable pageable);

}
