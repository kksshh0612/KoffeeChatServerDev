package teamkiim.koffeechat.post.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.bookmark.service.BookmarkService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.post.common.repository.PostRepository;
import teamkiim.koffeechat.postlike.service.PostLikeService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeService postLikeService;
    private final BookmarkService bookmarkService;

    /**
     * 게시물 좋아요
     *
     * @param postId   게시물 PK
     * @param memberId 회원 PK
     * @return Long -> 게시물 좋아요 수
     */
    @Transactional
    public ResponseEntity<?> like(Long postId, Long memberId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (postLikeService.isMemberLiked(post, member)) {        // 이미 좋아요 눌렀으면
            postLikeService.cancelLike(post, member);
            post.removeLike();
        } else {                                                   // 좋아요 누르지 않았으면
            postLikeService.like(post, member);
            post.addLike();
        }

        return ResponseEntity.ok(post.getLikeCount());
    }

    /**
     * 게시물 북마크
     * @param memberId 회원 PK
     * @param postId 게시물 PK
     * @return Long -> 게시물 북마크 수
     */
    @Transactional
    public ResponseEntity<?> bookmark(Long memberId, Long postId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (bookmarkService.isMemberBookmarked(member, post)) {  // 이미 북마크 했으면
            bookmarkService.cancelBookmark(member, post);
            post.removeBookmark();
        }else{                                                   // 북마크 누르지 않았다면
            bookmarkService.bookmark(member, post);              // 북마크 생성
            post.addBookmark();
        }

        return ResponseEntity.ok(post.getBookmarkCount());
    }
}
