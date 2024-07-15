package teamkiim.koffeechat.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.bookmark.domain.Bookmark;
import teamkiim.koffeechat.bookmark.repository.BookmarkRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.common.domain.Post;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public boolean isMemberBookmarked(Member member, Post post) {
        if(bookmarkRepository.findByPostAndMember(post, member).isPresent()) return true;
        return false;
    }

    /**
     * 게시물 북마크 생성
     * @param member 북마크한 멤버
     * @param post 북마크한 게시글
     */
    @Transactional
    public void bookmark(Member member, Post post) {
        Bookmark bookmark = Bookmark.create(member, post);
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void cancelBookmark(Member member, Post post) {
        Bookmark bookmark = bookmarkRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }
}
