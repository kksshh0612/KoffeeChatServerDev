package teamkiim.koffeechat.domain.post.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long id);

    @Query("SELECT p FROM Post p WHERE p.member=:member AND TYPE(p) =:postType")
    Page<Post> findAllByMemberAndDType(@Param("member") Member member, @Param("postType") Class<? extends Post> postTypeClass, PageRequest pageRequest);
}
