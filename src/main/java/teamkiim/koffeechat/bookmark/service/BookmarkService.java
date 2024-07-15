package teamkiim.koffeechat.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.bookmark.domain.Bookmark;
import teamkiim.koffeechat.bookmark.repository.BookmarkPostRepository;
import teamkiim.koffeechat.bookmark.repository.BookmarkRepository;
import teamkiim.koffeechat.bookmark.service.dto.BookmarkPostListResponse;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.common.domain.Post;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkPostRepository bookmarkPostRepository;

    public boolean isMemberBookmarked(Member member, Post post) {
        if (bookmarkRepository.findByPostAndMember(post, member).isPresent()) return true;
        return false;
    }

    /**
     * 게시물 북마크 생성
     *
     * @param member 북마크한 멤버
     * @param post   북마크한 게시글
     */
    @Transactional
    public void bookmark(Member member, Post post) {
        Bookmark bookmark = Bookmark.create(member, post);
        bookmarkRepository.save(bookmark);
    }

    /**
     * 게시글 북마크 삭제
     *
     * @param member 북마크한 멤버
     * @param post   삭제할 게시글
     */
    @Transactional
    public void cancelBookmark(Member member, Post post) {
        Bookmark bookmark = bookmarkRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }

    /**
     * 북마크한 게시글 목록 조회
     *
     * @param page 페이지 번호 ( ex) 0, 1,,,, )
     * @param size 페이지 당 조회할 데이터 수
     * @return List<CommunityPostListResponse>
     */
    public ResponseEntity<?> findBookmarkPostList(Long memberId, int page, int size) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Long> bookmarkPostIdList = bookmarkRepository.findByMemberId(memberId);

        List<Post> bookmarkPostList = bookmarkPostRepository.findByIdIn(bookmarkPostIdList, pageRequest).getContent();

        List<BookmarkPostListResponse> bookmarkPostResponseList = bookmarkPostList.stream()
                .map(BookmarkPostListResponse::of).collect(Collectors.toList());

        return ResponseEntity.ok(bookmarkPostResponseList);
    }

}
