package teamkiim.koffeechat.domain.post.dev.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.post.dev.controller.dto.ModifyDevPostRequest;
import teamkiim.koffeechat.domain.post.dev.controller.dto.SaveDevPostRequest;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.domain.post.dev.service.DevPostService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev-post")
@Tag(name = "개발 게시판 API")
@Slf4j
public class DevPostController {

    private final DevPostService devPostService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 개발 게시글 최초 임시 저장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/init")
    @DevPostApiDocument.InitPostApiDoc
    public ResponseEntity<?> initPost(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        Long postId = devPostService.saveInitDevPost(memberId);

        return ResponseEntity.ok(aesCipherUtil.encrypt(postId));
    }

    /**
     * 개발 게시글 작성 취소
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/{postId}")
    @DevPostApiDocument.CancelPostApiDoc
    public ResponseEntity<?> cancelPost(@PathVariable("postId") String postId) {

        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        devPostService.cancelWriteDevPost(decryptedPostId);

        return ResponseEntity.ok("게시글 삭제 완료");
    }

    /**
     * 개발 게시글 작성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/{postId}")
    @DevPostApiDocument.SavePostApiDoc
    public ResponseEntity<?> savePost(@PathVariable("postId") String postId,
                                      @Valid @RequestBody SaveDevPostRequest saveDevPostRequest,
                                      HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        LocalDateTime createdTime = LocalDateTime.now();

        devPostService.saveDevPost(decryptedPostId, saveDevPostRequest.toServiceRequest(), memberId, createdTime);

        return ResponseEntity.ok("게시글 작성 완료");
    }

    /**
     * 개발 게시글 목록 조회 (필터: 기술 카테고리, 태그, 제목)
     */
    @GetMapping("")
    @DevPostApiDocument.GetDevPostList
    public ResponseEntity<?> getDevPostList(@RequestParam("sortType") String sortType,
                                            @RequestParam("page") int page, @RequestParam("size") int size,
                                            @RequestParam(value = "word", required = false) String keyword,
                                            @RequestParam(value = "skillCategory", required = false) List<ChildSkillCategory> childSkillCategoryList,
                                            @RequestParam(value = "tag", required = false) List<String> tagContents) {

        return ResponseEntity.ok(
                devPostService.getDevPostList(sortType, page, size, keyword, childSkillCategoryList, tagContents)
        );
    }

    /**
     * 기술 채팅 연관 게시글 조회
     */
    @GetMapping("/skill")
    @DevPostApiDocument.getDevPostListBySkill
    public ResponseEntity<?> getDevPostListBySkill(@RequestParam("cursor") String cursor,
                                                   @RequestParam("size") int size,
                                                   @RequestParam("skillCategory") String childSkillCategory) {

        return ResponseEntity.ok(
                devPostService.findDevPostListByChildSkillCategory(childSkillCategory, cursor, size)
        );
    }

    /**
     * 개발 게시글 상세 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/{postId}")
    @DevPostApiDocument.ShowPostApiDoc
    public ResponseEntity<?> showPost(@PathVariable("postId") String postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        DevPostResponse postResponse = devPostService.findPost(decryptedPostId, memberId, request);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * 개발 게시글 수정
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/{postId}")
    @DevPostApiDocument.ModifyPostApiDoc
    public ResponseEntity<?> modifyPost(@PathVariable("postId") String postId,
                                        @Valid @RequestBody ModifyDevPostRequest modifyDevPostRequest,
                                        HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        devPostService.modifyPost(decryptedPostId, modifyDevPostRequest.toServiceRequest(), memberId);

        return ResponseEntity.ok("게시물 수정 완료");
    }

}
