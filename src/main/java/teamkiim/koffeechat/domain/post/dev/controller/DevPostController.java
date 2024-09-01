package teamkiim.koffeechat.domain.post.dev.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.post.dev.controller.dto.ModifyDevPostRequest;
import teamkiim.koffeechat.domain.post.dev.controller.dto.SaveDevPostRequest;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.domain.post.dev.service.DevPostService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

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

        return ResponseEntity.ok(postId);
    }

    /**
     * 개발 게시글 작성 취소
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/cancel/{postId}")
    @DevPostApiDocument.CancelPostApiDoc
    public ResponseEntity<?> cancelPost(@PathVariable("postId") Long postId) {

        devPostService.cancelWriteDevPost(postId);

        return ResponseEntity.ok("게시글 작성 취소 완료");
    }

    /**
     * 개발 게시글 작성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/post")
    @DevPostApiDocument.SavePostApiDoc
    public ResponseEntity<?> savePost(@Valid @RequestBody SaveDevPostRequest saveDevPostRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        DevPostResponse response = devPostService.saveDevPost(saveDevPostRequest.toServiceRequest(), memberId);

        return ResponseEntity.ok(response);
    }

    /**
     * 개발 게시글 목록 조회
     */
    @GetMapping("/list")
    @DevPostApiDocument.GetDevPostList
    public ResponseEntity<?> getDevPostList(@RequestParam("page") int page, @RequestParam("size") int size,
                                            @RequestParam(value = "skillCategory", required = false) List<ChildSkillCategory> childSkillCategoryList) {

        log.info("/dev-post/list 진입");

        List<DevPostListResponse> responseList = devPostService.getDevPostList(page, size, childSkillCategoryList);

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

        DevPostResponse response = devPostService.findPost(postId, memberId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 개발 게시글 수정
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/modify")
    @DevPostApiDocument.ModifyPostApiDoc
    public ResponseEntity<?> modifyPost(@Valid @RequestBody ModifyDevPostRequest modifyDevPostRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        DevPostResponse response = devPostService.modifyPost(modifyDevPostRequest.toServiceRequest(), memberId);

        return ResponseEntity.ok(response);
    }

}
