package teamkiim.koffeechat.domain.post.common.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;
import teamkiim.koffeechat.domain.post.common.domain.SortType;
import teamkiim.koffeechat.domain.post.common.dto.response.BookmarkPostListResponse;
import teamkiim.koffeechat.domain.post.common.dto.response.MyPostListResponse;
import teamkiim.koffeechat.domain.post.common.service.PostService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "게시판 공통 API")
public class PostController {

    private final PostService postService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 게시글 삭제 (soft delete)
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("delete/{postId}")
    @PostApiDocument.DeletePostApiDoc
    public ResponseEntity<?> delete(@PathVariable("postId") String postId) {

        Long decryptedPostId = aesCipherUtil.decrypt(postId);
        postService.softDelete(decryptedPostId);

        return ResponseEntity.ok("게시글이 휴지통으로 이동했습니다.");
    }

    /**
     * 좋아요
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/like/{postId}")
    @PostApiDocument.LikeApiDoc
    public ResponseEntity<?> like(@PathVariable("postId") String postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        long likeCount = postService.like(decryptedPostId, memberId);

        return ResponseEntity.ok(likeCount);
    }

    /**
     * 북마크
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/bookmark/{postId}")
    @PostApiDocument.BookmarkApiDoc
    public ResponseEntity<?> bookmark(@PathVariable("postId") String postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        long bookmarkCount = postService.bookmark(decryptedPostId, memberId);

        return ResponseEntity.ok(bookmarkCount);
    }

    /**
     * 마이페이지 북마크 리스트 확인
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/bookmark/{postType}/{sortType}")
    @PostApiDocument.BookmarkedPostListApiDoc
    public ResponseEntity<?> findBookmarkedPostList(@PathVariable("postType") PostCategory postType,
                                                    @PathVariable("sortType") SortType sortType,
                                                    @RequestParam("page") int page, @RequestParam("size") int size,
                                                    HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<BookmarkPostListResponse> bookmarkPostResponseList = postService.findBookmarkPostList(memberId, postType,
                sortType, page, size);

        return ResponseEntity.ok(bookmarkPostResponseList);
    }

    /**
     * 마이페이지 내가 쓴 게시글 리스트 확인
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/{postType}/{sortType}")
    @PostApiDocument.MyPostListApiDoc
    public ResponseEntity<?> findMyPostList(@PathVariable("postType") PostCategory postType,
                                            @PathVariable("sortType") SortType sortType,
                                            @RequestParam("page") int page, @RequestParam("size") int size,
                                            HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MyPostListResponse> myPostListResponseList =
                postService.findMyPostList(memberId, postType, sortType, page, size);

        return ResponseEntity.ok(myPostListResponseList);
    }

}
