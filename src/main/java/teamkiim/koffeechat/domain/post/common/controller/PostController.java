package teamkiim.koffeechat.domain.post.common.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.post.common.controller.dto.response.BookmarkPostListResponse;
import teamkiim.koffeechat.domain.post.common.controller.dto.response.MyPostListResponse;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;
import teamkiim.koffeechat.domain.post.common.domain.SortCategory;
import teamkiim.koffeechat.domain.post.common.service.PostService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "게시판 공통 API")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 삭제 (soft delete)
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("delete/{postId}")
    @PostApiDocument.DeletePostApiDoc
    public ResponseEntity<?> delete(@PathVariable("postId") Long postId) {

        postService.softDelete(postId);

        return ResponseEntity.ok("게시글이 휴지통으로 이동했습니다.");
    }

    /**
     * 좋아요
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/like/{postId}")
    @PostApiDocument.LikeApiDoc
    public ResponseEntity<?> like(@PathVariable("postId") Long postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        long likeCount = postService.like(postId, memberId);

        return ResponseEntity.ok(likeCount);
    }

    /**
     * 북마크
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/bookmark/{postId}")
    @PostApiDocument.BookmarkApiDoc
    public ResponseEntity<?> bookmark(@PathVariable("postId") Long postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        long bookmarkCount = postService.bookmark(postId, memberId);

        return ResponseEntity.ok(bookmarkCount);
    }

    /**
     * 마이페이지 북마크 리스트 확인
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/bookmark/{postType}/{sortType}")
    @PostApiDocument.BookmarkedPostListApiDoc
    public ResponseEntity<?> findBookmarkedPostList(@PathVariable("postType") PostCategory postType, @PathVariable("sortType") SortCategory sortType,
                                                    @RequestParam("page") int page, @RequestParam("size") int size, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<BookmarkPostListResponse> bookmarkPostResponseList = postService.findBookmarkPostList(memberId, postType, sortType, page, size);

        return ResponseEntity.ok(bookmarkPostResponseList);
    }

    /**
     * 마이페이지 내가 쓴 게시글 리스트 확인
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/{postType}/{sortType}")
    @PostApiDocument.MyPostListApiDoc
    public ResponseEntity<?> findMyPostList(@PathVariable("postType") PostCategory postType, @PathVariable("sortType") SortCategory sortType,
                                            @RequestParam("page") int page, @RequestParam("size") int size, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MyPostListResponse> myPostListResponseList = postService.findMyPostList(memberId, postType, sortType, page, size);

        return ResponseEntity.ok(myPostListResponseList);
    }

}
