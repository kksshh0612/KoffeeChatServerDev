package teamkiim.koffeechat.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.post.community.CommunityPost;
import teamkiim.koffeechat.post.dev.DevPost;
import teamkiim.koffeechat.post.dev.DevPostForm;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public abstract class PostController {

    private final PostService postService;

    /**
     * 게시글 생성 : 개발 게시글
     */
//    @PostMapping("/dev-write")
//    public ResponseEntity<Post> createPost(@RequestBody DevPost post) {
//        postService.createPost(post);
//
//        //생성된 게시물 반환
//        return ResponseEntity.ok(post);
//    }

    /**
     * 게시글 생성 : 커뮤니티 게시글
     */
    @PostMapping("/com-write")
    public ResponseEntity<Post> createPost(@RequestBody CommunityPost post) {
        postService.createPost(post);

        //생성된 게시물 반환
        return ResponseEntity.ok(post);
    }

//    /**
//     * 게시글 목록 조회
//     */
//    @GetMapping("/posts")
//    public ResponseEntity<List<DevPostForm>> list() {
//        List<Post> posts= postService.findPosts();
//
//        //Post 엔티티를 PostForm 형태로 정제
//        List<DevPostForm> devPostForms = posts.stream()
//                .map(post -> {
//                    DevPostForm devPostForm= new DevPostForm();
//                    devPostForm.set(post.getId(), post.getTitle(),post.getBodyContent(),post.getViewCount(),post.getLikeCount(),post.getCreatedTime(), post.getModifiedTime());
//                    return devPostForm;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(devPostForms);
//    }

}
