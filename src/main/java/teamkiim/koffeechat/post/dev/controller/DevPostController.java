package teamkiim.koffeechat.post.dev.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.post.dev.dto.response.DevPostViewResponse;
import teamkiim.koffeechat.post.dev.service.DevPostService;
import teamkiim.koffeechat.post.dto.request.PostCreateRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DevPostController {

    private final DevPostService devPostService;

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

        return ResponseEntity.ok(posts);
    }

    /**
     * 개발 게시글 제목, 내용, 카테고리 수정
     * 수정 시간도 업데이트됨
     */
    @Auth
    @PostMapping("/posts/{postId}/edit")
    public ResponseEntity<DevPostViewResponse> updatePost(
            @PathVariable("postId") Long postId, @RequestBody PostCreateRequest postDto, HttpServletRequest request) {
        //작성자 id
        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        DevPostViewResponse dto = devPostService.updatePost(postId, postDto, memberId);

        return ResponseEntity.ok(dto);
    }

    /**
     * 카테고리별 게시글 목록 조회
     */
    @GetMapping("/posts/")
    public ResponseEntity<List<DevPostViewResponse>> categoryDevList(@RequestParam("category-names") List<String> categoryNames) {
        List<DevPostViewResponse> devList = devPostService.findDevPostsByCategories(categoryNames);
        return ResponseEntity.ok(devList);
    }

}
