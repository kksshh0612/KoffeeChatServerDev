package teamkiim.koffeechat.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.global.Auth;
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
    @Auth
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "게시글 삭제", description = "사용자가 게시글 삭제를 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "권한이 없는 사용자가 게시글 삭제를 요청하는 경우",
                            value = "{\"code\":403, \"message\":\"게시글 삭제 권한이 없습니다.\"}")}
            ))
    })
    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId, HttpServletRequest request) {

        Long pk = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));  //현재 로그인한 사용자

        if (postService.deletePost(postId, pk)) {
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
