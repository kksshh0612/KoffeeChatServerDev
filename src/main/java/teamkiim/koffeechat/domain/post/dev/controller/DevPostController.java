package teamkiim.koffeechat.domain.post.dev.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.post.common.domain.SortCategory;
import teamkiim.koffeechat.domain.post.dev.controller.dto.ModifyDevPostRequest;
import teamkiim.koffeechat.domain.post.dev.controller.dto.SaveDevPostRequest;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.domain.post.dev.dto.request.SkillCategoryRequest;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.domain.post.dev.service.DevPostService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev-post")
@Tag(name = "개발 게시판 API")
@Slf4j
public class DevPostController {

    private final DevPostService devPostService;

    /**
     * 개발 게시글 최초 임시 저장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/init")
    @DevPostApiDocument.InitPostApiDoc
    public ResponseEntity<?> initPost(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        Long postId = devPostService.saveInitDevPost(memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    /**
     * 개발 게시글 작성 취소
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/{postId}")
    @DevPostApiDocument.CancelPostApiDoc
    public ResponseEntity<?> cancelPost(@PathVariable("postId") Long postId) {

        devPostService.cancelWriteDevPost(postId);

        return ResponseEntity.ok("게시글 삭제 완료");
    }

    /**
     * 개발 게시글 작성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/{postId}")
    @DevPostApiDocument.SavePostApiDoc
    public ResponseEntity<?> savePost(@PathVariable("postId") Long postId, @Valid @RequestBody SaveDevPostRequest saveDevPostRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime createdTime = LocalDateTime.now();

        devPostService.saveDevPost(saveDevPostRequest.toServiceRequest(postId), memberId, createdTime);

        return ResponseEntity.ok("게시글 작성 완료");
    }

    /**
     * 개발 게시글 목록 조회 (필터: 기술 카테고리, 태그, 제목)
     */
    @GetMapping("")
    @DevPostApiDocument.GetDevPostList
    public ResponseEntity<?> getDevPostList(@RequestParam("sortType") SortCategory sortType, @RequestParam("page") int page, @RequestParam("size") int size,
                                            @RequestParam(value = "word", required = false) String keyword,
                                            @RequestParam(value = "skillCategory", required = false) List<ChildSkillCategory> childSkillCategoryList,
                                            @RequestParam(value = "tag", required = false) List<String> tagContents) {

        log.info("/dev-post/list 진입");

        List<DevPostListResponse> responseList = devPostService.getDevPostList(sortType, page, size, keyword, childSkillCategoryList, tagContents);

        return ResponseEntity.ok(responseList);
    }

    /**
     * 개발 게시글 상세 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/{postId}")
    @DevPostApiDocument.ShowPostApiDoc
    public ResponseEntity<?> showPost(@PathVariable("postId") Long postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        DevPostResponse postResponse = devPostService.findPost(postId, memberId, request);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * 개발 게시글 수정
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/{postId}")
    @DevPostApiDocument.ModifyPostApiDoc
    public ResponseEntity<?> modifyPost(@PathVariable("postId") Long postId, @Valid @RequestBody ModifyDevPostRequest modifyDevPostRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        devPostService.modifyPost(modifyDevPostRequest.toServiceRequest(postId), memberId);

        return ResponseEntity.ok("게시물 수정 완료");
    }

    /**
     * 기술 채팅방 관련 게시글 조회
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/skillCategory")
    @DevPostApiDocument.SkillCategoryPostsApiDoc
    public ResponseEntity<?> skillCategoryPosts(@RequestBody SkillCategoryRequest skillCategoryRequest, @RequestParam("page") int page, @RequestParam("size") int size) {

        List<DevPostListResponse> responses = devPostService.findSkillCategoryPosts(skillCategoryRequest.convertToSkillCategory(), page, size);

        return ResponseEntity.ok(responses);
    }
}
