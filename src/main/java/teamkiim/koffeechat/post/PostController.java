package teamkiim.koffeechat.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.post.community.CommunityPost;

@RestController
@RequiredArgsConstructor
public abstract class PostController {

    private final PostService postService;

    /**
     * 게시글 생성 : 커뮤니티 게시글
     */
    @PostMapping("/com-write")
    public ResponseEntity<Post> createPost(@RequestBody CommunityPost post) {
        postService.createPost(post);

        //생성된 게시물 반환
        return ResponseEntity.ok(post);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/posts/{postId}/delete")
    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId) {
        if (postService.deletePost(postId)) {
            return ResponseEntity.ok("게시물이 삭제되었습니다.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }



//    /**
//     * 게시글 목록 조회
//     */
//    @GetMapping("/posts")
//    public ResponseEntity<List<PostForm>> list() {
//        List<Post> posts= postService.findPosts();
//
//        //Post 엔티티를 PostForm 형태로 정제
//        List<PostForm> postForms = posts.stream()
//                .map(post -> {
//                    PostForm postForm= new PostForm();
//                    postForm.set(post.getId(), post.getTitle(),post.getBodyContent(),post.getViewCount(),post.getLikeCount(),post.getCreatedTime(), post.getModifiedTime());
//                    return postForm;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(postForms);
//    }

}
