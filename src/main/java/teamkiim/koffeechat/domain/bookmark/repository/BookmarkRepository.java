package teamkiim.koffeechat.domain.bookmark.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.bookmark.domain.Bookmark;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByPostAndMember(Post post, Member member);

    @Query("SELECT b FROM Bookmark b WHERE b.member=:member AND b.post.postCategory =:postType AND b.post.isEditing = false")
    Page<Bookmark> findAllByMemberAndPostCategory(@Param("member") Member member, @Param("postType") PostCategory postType, PageRequest pageRequest);
}
