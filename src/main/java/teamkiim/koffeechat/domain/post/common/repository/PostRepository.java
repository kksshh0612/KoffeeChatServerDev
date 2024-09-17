package teamkiim.koffeechat.domain.post.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long id);

    @Query("select p from Post p join fetch p.commentList where p.id = :id")
    Optional<Post> findByIdWithComments(@Param("id") Long id);

    @Query("SELECT p FROM Post p WHERE p.member = :member AND p.postCategory = :postType AND p.isEditing = false AND p.deleted = false")
    Page<Post> findAllByMemberAndPostCategory(@Param("member") Member member, @Param("postType") PostCategory postType, PageRequest pageRequest);
}
