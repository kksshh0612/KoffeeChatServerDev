package teamkiim.koffeechat.post;

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
public class PostContorller {

    private final PostService postService;
    private final PostLikeService postLikeService;  //게시글 좋아요 기능

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/posts/{postId}")
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
    public ResponseEntity<Void> toggleLike(@PathVariable("postId") Long postId, @RequestParam("member-id") Long memberId) {
        postLikeService.toggleLike(memberId, postId);
        return ResponseEntity.ok().build();
    }
}
