package teamkiim.koffeechat.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.bookmark.domain.Bookmark;
import teamkiim.koffeechat.domain.bookmark.repository.BookmarkRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    /**
     * 회원이 해당 글에 대해 이미 북마크를 눌렀는지 확인
     * @param member
     * @param post
     * @return
     */
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

    /**
     * 게시글 북마크 취소
     * @param member 북마크한 멤버
     * @param post 북마크를 취소할 게시글
     */
    @Transactional
    public void cancelBookmark(Member member, Post post) {

        Bookmark bookmark = bookmarkRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }

}
