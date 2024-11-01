package teamkiim.koffeechat.domain.post.common.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.global.aescipher.AESCipher;
import teamkiim.koffeechat.domain.bookmark.domain.Bookmark;
import teamkiim.koffeechat.domain.bookmark.repository.BookmarkRepository;
import teamkiim.koffeechat.domain.bookmark.service.BookmarkService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;
import teamkiim.koffeechat.domain.post.common.domain.SortType;
import teamkiim.koffeechat.domain.post.common.dto.response.BookmarkPostListResponse;
import teamkiim.koffeechat.domain.post.common.dto.response.MyPostListResponse;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.domain.postlike.service.PostLikeService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeService postLikeService;
    private final BookmarkService bookmarkService;
    private final BookmarkRepository bookmarkRepository;

    private final AESCipher aesCipher;

    /**
     * 게시글 삭제 (soft delete)
     *
     * @param postId 삭제할 게시글 PK
     */
    @Transactional
    public void softDelete(String postId) throws Exception {

        Post post = postRepository.findById(aesCipher.decrypt(postId))
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.delete();
    }

    /**
     * 게시물 좋아요
     *
     * @param postId   게시물 PK
     * @param memberId 회원 PK
     * @return long -> 게시물 좋아요 수
     */
    @Transactional
    public long like(String postId, Long memberId) throws Exception {

        Post post = postRepository.findById(aesCipher.decrypt(postId))
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.isDeleted() || post.isEditing()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (postLikeService.isMemberLiked(post, member)) {        // 이미 좋아요 눌렀으면
            postLikeService.cancelLike(post, member);
            post.removeLike();
        } else {                                                   // 좋아요 누르지 않았으면
            postLikeService.like(post, member);
            post.addLike();
        }

        return post.getLikeCount();
    }

    /**
     * 게시물 북마크
     *
     * @param postId   게시물 PK
     * @param memberId 회원 PK
     * @return long -> 게시물 북마크 수
     */
    @Transactional
    public long bookmark(String postId, Long memberId) throws Exception {

        Post post = postRepository.findById(aesCipher.decrypt(postId))
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.isDeleted() || post.isEditing()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (bookmarkService.isMemberBookmarked(member, post)) {     // 이미 북마크 했으면
            bookmarkService.cancelBookmark(member, post);
            post.removeBookmark();
        } else {                                                      // 북마크 누르지 않았다면
            bookmarkService.bookmark(member, post);                 // 북마크 생성
            post.addBookmark();
        }

        return post.getBookmarkCount();
    }

    /**
     * 로그인한 회원이 북마크한 게시글 목록 조회
     *
     * @param memberId     로그인한 회원
     * @param postCategory 게시글 종류 (개발 / 커뮤니티)
     * @param sortType     정렬 순서 (최신순, 좋아요순, 조회순)
     * @param page         페이지 번호 ( ex) 0, 1,,,, )
     * @param size         페이지 당 조회할 데이터 수
     * @return List<BookmarkPostListResponse>
     */
    public List<BookmarkPostListResponse> findBookmarkPostList(Long memberId, PostCategory postCategory, SortType sortType, int page, int size) throws Exception {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = sortBySortCategory(sortType, "id", "post.likeCount", "post.viewCount", page, size);

        List<Bookmark> bookmarkList = bookmarkRepository.findAllByMemberAndPostCategory(member, postCategory, pageRequest).getContent();

        List<Post> bookmarkPostList = bookmarkList.stream().map(Bookmark::getPost).toList();

        return bookmarkPostList.stream().map(post -> {
            try {
                return BookmarkPostListResponse.of(aesCipher.encrypt(post.getId()), post);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
            }
        }).toList();

    }

    /**
     * 로그인한 회원이 작성한 게시글 목록 조회
     *
     * @param memberId     로그인한 회원
     * @param postCategory 게시글 종류 (개발 / 커뮤니티)
     * @param sortType     정렬 순서 (최신순, 좋아요순, 조회순)
     * @param page         페이지 번호 ( ex) 0, 1,,,, )
     * @param size         페이지 당 조회할 데이터 수
     * @return List<BookmarkPostListResponse>
     */
    public List<MyPostListResponse> findMyPostList(Long memberId, PostCategory postCategory, SortType sortType, int page, int size) throws Exception {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = sortBySortCategory(sortType, "id", "likeCount", "viewCount", page, size);

        List<Post> postList = postRepository.findAllByMemberAndPostCategory(member, postCategory, pageRequest).getContent();

        return postList.stream().map(post -> {
            try {
                return MyPostListResponse.of(aesCipher.encrypt(post.getId()), post);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
            }
        }).toList();
    }

    /**
     * 게시글 조회수
     */
    public void viewPost(Post post, HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();
        String uniqueViewKey = "viewedPost_" + post.getId() + "_" + clientIp;

        if (request.getSession().getAttribute(uniqueViewKey) == null) {
            post.addViewCount();
            request.getSession().setAttribute(uniqueViewKey, true);
        }
    }

    /**
     * 게시글 정렬 (최신순 | 좋아요순 | 조회순)
     */
    public PageRequest sortBySortCategory(SortType sortType, String newRequest, String likeRequest, String viewRequest, int page, int size) {
        return switch (sortType) {
            case NEW -> PageRequest.of(page, size, Sort.by(DESC, newRequest));
            case LIKE -> PageRequest.of(page, size, Sort.by(DESC, likeRequest).and(Sort.by(DESC, newRequest)));
            case VIEW -> PageRequest.of(page, size, Sort.by(DESC, viewRequest).and(Sort.by(DESC, newRequest)));
        };
    }
}
