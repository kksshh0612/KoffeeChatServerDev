package teamkiim.koffeechat.post.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.request.PostCreateRequestDto;
import teamkiim.koffeechat.response.DevPostViewResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DevPostController {

    private final DevPostService devPostService;

    /**
     * 개발 게시글 생성
     */
    @PostMapping("/dev-write")
    public ResponseEntity<DevPostViewResponseDto> createPost(@RequestBody PostCreateRequestDto postDto) {
        DevPostViewResponseDto devPostDto = devPostService.createDevPost(postDto);

        //생성된 게시물 반환
        return ResponseEntity.ok(devPostDto);
    }

    /**
     * 개발 게시글 목록 조회
     */
    @GetMapping("/dev-posts")
    public ResponseEntity<List<DevPostViewResponseDto>> list() {
        List<DevPost> posts = devPostService.findDevPosts();  //게시글 목록 불러오기

        //Post entity to dto
        List<DevPostViewResponseDto> dtoList = posts.stream()
                .map(post -> {
                    DevPostViewResponseDto dto = new DevPostViewResponseDto();
                    dto.set(post);
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    /**
     * 개발 게시글 제목, 내용 수정
     */
    @PostMapping("posts/{postId}/edit")
    public ResponseEntity<DevPostViewResponseDto> updatePost(@PathVariable("postId") Long postId, @RequestBody PostCreateRequestDto postDto) {
        DevPostViewResponseDto dto= devPostService.updatePost(postId, postDto.getTitle(), postDto.getBodyContent());

        return  ResponseEntity.ok(dto);
    }

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
