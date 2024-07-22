package teamkiim.koffeechat.domain.bookmark.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.domain.bookmark.domain.Bookmark;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    //마이페이지에서 로그인된 member의 북마크 기록을 가져온다.
    @Query("SELECT b.post FROM Bookmark b WHERE b.member = :member")
    Page<Post> findBookmarkedPostsByMemberId(Member member, Pageable pageable);

    Optional<Bookmark> findByPostAndMember(Post post, Member member);
}
