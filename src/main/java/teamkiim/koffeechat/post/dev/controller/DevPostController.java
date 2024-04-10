package teamkiim.koffeechat.post.dev.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.post.dev.service.DevPostService;
import teamkiim.koffeechat.postlike.service.PostLikeService;
import teamkiim.koffeechat.post.dto.request.PostCreateRequest;
import teamkiim.koffeechat.post.dev.dto.response.DevPostViewResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DevPostController {

    private final DevPostService devPostService;
    private final PostLikeService postLikeService;  //게시글 좋아요 기능


    /**
     * 개발 게시글 생성
     */
    @Auth
    @PostMapping("/dev-write")
    public ResponseEntity<DevPostViewResponse> createPost(
            @Valid @RequestBody PostCreateRequest postDto, HttpServletRequest request) {
        //작성자 id
        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        //게시글 생성
        DevPostViewResponse devPostDto = devPostService.createDevPost(postDto, memberId);

        //생성된 게시물 반환
        return ResponseEntity.ok(devPostDto);
    }

    /**
     * 전체 개발 게시글 목록 조회
     */
    @GetMapping("/dev-posts")
    public ResponseEntity<List<DevPostViewResponse>> list() {
        List<DevPostViewResponse> posts = devPostService.findDevPosts();  //게시글 목록 불러오기
//
//        //Post entity to dto
//        List<DevPostViewResponseDto> dtoList = posts.stream()
//                .map(post -> {
//                    List<SkillCategory> categories= post.getSkillCategoryList();
//                    List<String> categoryNames= categories.stream()
//                            .map(SkillCategory::getName)
//                            .collect(Collectors.toList());
//                    DevPostViewResponseDto dto = new DevPostViewResponseDto();
//                    dto.set(post, categoryNames);
//                    return dto;
//                })
//                .collect(Collectors.toList());

        return ResponseEntity.ok(posts);
    }

    /**
     * 개발 게시글 제목, 내용, 카테고리 수정
     * 수정 시간도 업데이트됨
     */
    @PostMapping("/posts/{postId}/edit")
    public ResponseEntity<DevPostViewResponse> updatePost(@PathVariable("postId") Long postId, @RequestBody PostCreateRequest postDto) {
        DevPostViewResponse dto = devPostService.updatePost(postId, postDto);

        return ResponseEntity.ok(dto);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId) {
        if (devPostService.deletePost(postId)) {
            return ResponseEntity.ok("게시물이 삭제되었습니다.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 카테고리별 게시글 목록 조회
     */
    @GetMapping("/posts/")
    public ResponseEntity<List<DevPostViewResponse>> categoryDevList(@RequestParam("category-names") List<String> categoryNames) {
        List<DevPostViewResponse> devList = devPostService.findDevPostsByCategories(categoryNames);
        return ResponseEntity.ok(devList);
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
