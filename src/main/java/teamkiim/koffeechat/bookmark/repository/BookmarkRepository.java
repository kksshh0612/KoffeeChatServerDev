package teamkiim.koffeechat.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.bookmark.domain.Bookmark;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.common.domain.Post;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    //마이페이지에서 로그인된 member의 북마크 기록을 가져온다.
    Optional<Bookmark> findByMember(Member member);

    Optional<Bookmark> findByPostAndMember(Post post, Member member);
}
