package teamkiim.koffeechat.domain.post.community.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import teamkiim.koffeechat.domain.post.community.controller.dto.ModifyCommunityPostRequest;
import teamkiim.koffeechat.domain.post.community.controller.dto.SaveCommunityPostRequest;
import teamkiim.koffeechat.domain.post.community.dto.response.CommunityPostListResponse;
import teamkiim.koffeechat.domain.post.community.dto.response.CommunityPostResponse;
import teamkiim.koffeechat.domain.post.community.service.CommunityPostService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community-post")
@Tag(name = "커뮤니티 게시판 API")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 커뮤니티 게시글 최초 임시 저장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/init")
    @CommunityPostApiDocument.InitPostApiDoc
    public ResponseEntity<?> initPost(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        Long postId = communityPostService.saveInitCommunityPost(memberId);

        return ResponseEntity.ok(aesCipherUtil.encrypt(postId));
    }

    /**
     * 커뮤니티 게시글 작성 취소
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/{postId}")
    @CommunityPostApiDocument.CancelPostApiDoc
    public ResponseEntity<?> cancelPostApiDoc(@PathVariable("postId") String postId) {

        Long decryptedPostId = aesCipherUtil.decrypt(postId);
        communityPostService.cancelWriteCommunityPost(decryptedPostId);

        return ResponseEntity.ok("게시글 작성 취소 완료");
    }

    /**
     * 커뮤니티 게시글 작성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/{postId}")
    @CommunityPostApiDocument.SavePostApiDoc
    public ResponseEntity<?> savePost(@PathVariable("postId") String postId,
                                      @Valid @RequestBody SaveCommunityPostRequest saveCommunityPostRequest,
                                      HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        LocalDateTime createdTime = LocalDateTime.now();

        communityPostService.saveCommunityPost(decryptedPostId, saveCommunityPostRequest, memberId, createdTime);

        return ResponseEntity.ok("게시글 작성 완료");
    }

    /**
     * 커뮤니티 게시글 목록 조회 (필터: 제목, 태그)
     */
    @GetMapping("")
    @CommunityPostApiDocument.GetCommunityPostListApiDoc
    public ResponseEntity<?> getCommunityPostList(@RequestParam("sortType") SortType sortType,
                                                  @RequestParam("page") int page, @RequestParam("size") int size,
                                                  @RequestParam(value = "word", required = false) String keyword,
                                                  @RequestParam(value = "tag", required = false) List<String> tagContents) {

        List<CommunityPostListResponse> listResponse = communityPostService.findCommunityPostList(sortType, page, size,
                keyword, tagContents);

        return ResponseEntity.ok(listResponse);
    }

    /**
     * 커뮤니티 게시글 상세 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/{postId}")
    @CommunityPostApiDocument.ShowPostApiDoc
    public ResponseEntity<?> showPost(@PathVariable("postId") String postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        CommunityPostResponse postResponse = communityPostService.findPost(decryptedPostId, memberId, request);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * 커뮤니티 게시글 수정
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/{postId}")
    @CommunityPostApiDocument.ModifyPostApiDoc
    public ResponseEntity<?> modifyPost(@PathVariable("postId") String postId,
                                        @Valid @RequestBody ModifyCommunityPostRequest modifyCommunityPostRequest,
                                        HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        communityPostService.modifyPost(decryptedPostId, modifyCommunityPostRequest.toPostServiceRequest(),
                modifyCommunityPostRequest.toVoteServiceRequest(), memberId);

        return ResponseEntity.ok("게시글 수정 완료");
    }

}
