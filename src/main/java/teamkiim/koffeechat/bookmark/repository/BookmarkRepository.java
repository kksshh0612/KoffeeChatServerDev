package teamkiim.koffeechat.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.bookmark.domain.Bookmark;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.common.domain.Post;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    //마이페이지에서 로그인된 member의 북마크 기록을 가져온다.
    @Query("SELECT b.post.id FROM Bookmark b WHERE b.member.id= :memberId")
    List<Long> findByMemberId(Long memberId);

    Optional<Bookmark> findByPostAndMember(Post post, Member member);
}
