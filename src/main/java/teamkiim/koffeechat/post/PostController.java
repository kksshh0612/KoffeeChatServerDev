package teamkiim.koffeechat.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.postlike.service.PostLikeService;

/**
 * 게시글 공통 기능
 * 게시글 삭제
 * 좋아요 기능
 */
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;  //게시글 좋아요 기능

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "게시글 삭제", description = "사용자가 게시글 삭제를 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공")
    })
    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId) {
        if (postService.deletePost(postId)) {
            return ResponseEntity.ok("게시물이 삭제되었습니다.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 게시글 좋아요 토글
     */
    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "게시글 좋아요 요청", description = "사용자가 게시글 좋아요를 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 / 좋아요 취소 성공")
    })
    public ResponseEntity<Void> toggleLike(@PathVariable("postId") Long postId, @RequestParam("member-id") Long memberId) {
        postLikeService.toggleLike(memberId, postId);
        return ResponseEntity.ok().build();
    }
}
