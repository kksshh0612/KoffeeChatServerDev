package teamkiim.koffeechat.post.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.post.community.controller.dto.ModifyCommunityPostRequest;
import teamkiim.koffeechat.post.community.controller.dto.SaveCommunityPostRequest;
import teamkiim.koffeechat.post.community.dto.response.CommunityPostListResponse;
import teamkiim.koffeechat.post.community.dto.response.CommunityPostResponse;
import teamkiim.koffeechat.post.community.service.CommunityPostService;
import teamkiim.koffeechat.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.post.dev.dto.response.DevPostResponse;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community-post")
@Tag(name = "커뮤니티 게시판 API")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    /**
     * 커뮤니티 게시글 최초 임시 저장
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/init")
    @Operation(summary = "게시글 최초 임시 저장", description = "사용자가 커뮤니티 게시판에 글을 작성할 때, 최초 임시 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성된 게시글의 PK를 반환한다.",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 게시글을 쓰려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    public ResponseEntity<?> initPost(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return communityPostService.saveInitCommunityPost(memberId);
    }

    /**
     * 커뮤니티 게시글 작성 취소
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @DeleteMapping("/cancel/{postId}")
    @Operation(summary = "게시글 작성 취소", description = "사용자가 게시물을 작성하다가 취소하면 관련 도메인을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "postId에 해당하는 게시물이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")}
            ))
    })
    public ResponseEntity<?> cancelPost(@PathVariable("postId") Long postId) {

        return communityPostService.cancelWriteCommunityPost(postId);
    }

    /**
     * 커뮤니티 게시글 작성
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/post")
    @Operation(summary = "게시글 저장", description = "사용자가 커뮤니티 게시판에 게시글을 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성된 게시글을 반환한다.",
                    content = @Content(schema = @Schema(implementation = CommunityPostResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 게시글을 쓰려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "제목 or 본문 없이 게시글을 쓰려고 하는 경우",
                            value = "{\"code\":400, \"message\":\"제목을 입력해 주세요.\"}")}
            ))
    })
    public ResponseEntity<?> savePost(
            @Valid @RequestBody SaveCommunityPostRequest saveCommunityPostRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime currDateTime = LocalDateTime.now();

        return communityPostService.saveCommunityPost(saveCommunityPostRequest.toServiceRequest(currDateTime), memberId);
    }

    /**
     * 커뮤니티 게시글 목록 조회
     */
    @GetMapping("/list")
    @Operation(summary = "게시글 목록 조회", description = "사용자가 커뮤니티 게시글 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "커뮤니티 게시글 리스트를 반환한다. 만약 사진이 없으면 image 관련 필드는 null이 들어간다.",
                    content = @Content(schema = @Schema(implementation = CommunityPostListResponse.class))),
    })
    public ResponseEntity<?> showList(@RequestParam("page") int page, @RequestParam("size") int size) {

        return communityPostService.findCommunityPostList(page, size);
    }

    /**
     * 커뮤니티 게시글 상세 조회
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "사용자가 커뮤니티 게시글 단건을 상세 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "커뮤니티 게시글을 반환한다.",
                    content = @Content(schema = @Schema(implementation = CommunityPostResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "postId에 해당하는 게시글이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")}
            ))
    })
    public ResponseEntity<?> showPost(@PathVariable("postId") Long postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return communityPostService.findPost(postId, memberId);
    }

    /**
     * 커뮤니티 게시글 수정
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PatchMapping("/modify")
    @Operation(summary = "게시글 수정", description = "사용자가 커뮤니티 게시글을 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "커뮤니티 게시글을 반환한다.",
                    content = @Content(schema = @Schema(implementation = CommunityPostResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "id에 해당하는 게시글이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")}
            ))
    })
    public ResponseEntity<?> modifyPost(@Valid @RequestBody ModifyCommunityPostRequest modifyCommunityPostRequest,
                                        HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime currDateTime = LocalDateTime.now();

        return communityPostService.modifyPost(modifyCommunityPostRequest.toServiceRequest(currDateTime), memberId);
    }

}
