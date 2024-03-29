package teamkiim.koffeechat.post.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.request.PostCreateRequestDto;
import teamkiim.koffeechat.response.DevPostCreateResponseDto;

@RestController
@RequiredArgsConstructor
public class DevPostController {

    private final DevPostService devPostService;

    /**
     * 게시글 생성 : 개발 게시글
     */
    @PostMapping("/dev-write")
    public ResponseEntity<DevPostCreateResponseDto> createPost(@RequestBody PostCreateRequestDto postDto) {
        DevPostCreateResponseDto devPostDto= devPostService.createDevPost(postDto);

        //생성된 게시물 반환
        return ResponseEntity.ok(devPostDto);
    }
//
//    /**
//     * 게시글 목록 조회 : 개발 게시글
//     */
//    @GetMapping("/posts")
//    public ResponseEntity<List<DevPostForm>> list() {
//        List<DevPost> posts= devPostService.findDevPosts();
//
//        //Post 엔티티를 PostForm 형태로 정제
//        List<DevPostForm> devPostForms = posts.stream()
//                .map(post -> {
//                    DevPostForm devPostForm= new DevPostForm();
//                    devPostForm.set(post.getId(), post.getTitle(),post.getBodyContent(),post.getViewCount(),post.getLikeCount(),post.getCreatedTime(), post.getModifiedTime(), post.getChatRoomId());
//                    return devPostForm;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(devPostForms);
//    }
//
//    /**
//     * 개발 게시글 제목, 내용 수정
//     */
//    @PostMapping("posts/{postId}/edit")
//    public ResponseEntity<String> updatePost(@PathVariable("postId") Long postId, @RequestBody DevPostForm form) {
//        devPostService.updatePost(postId, form.getTitle(), form.getBodyContent());
//        return  ResponseEntity.ok("수정되었습니다.");
//    }
//
//    /**
//     * 게시글 삭제
//     */
//    @DeleteMapping("/posts/{postId}/delete")
//    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId) {
//        if (devPostService.deletePost(postId)) {
//            return ResponseEntity.ok("게시물이 삭제되었습니다.");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

}
