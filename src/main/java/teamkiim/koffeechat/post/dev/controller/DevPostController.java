package teamkiim.koffeechat.post.dev.controller;

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
import teamkiim.koffeechat.post.dev.controller.dto.ModifyDevPostRequest;
import teamkiim.koffeechat.post.dev.controller.dto.SaveDevPostRequest;
import teamkiim.koffeechat.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.post.dev.service.DevPostService;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev-post")
@Tag(name = "개발 게시판 API")
public class DevPostController {

    private final DevPostService devPostService;

    /**
     * 개발 게시글 최초 임시 저장
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/init")
    @Operation(summary = "게시글 최초 임시 저장", description = "사용자가 개발 게시판에 글을 작성할 때, 최초 임시 저장한다.")
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
    public ResponseEntity<?> initPost(HttpServletRequest request){

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return devPostService.saveInitDevPost(memberId);
    }

    /**
     * 개발 게시글 작성
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/post")
    @Operation(summary = "게시글 저장", description = "사용자가 개발 게시판에 게시글을 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성된 게시글을 반환한다.",
                    content = @Content(schema = @Schema(implementation = DevPostResponse.class))),
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
            @Valid @RequestBody SaveDevPostRequest saveDevPostRequest) {

        LocalDateTime currDateTime = LocalDateTime.now();

        return devPostService.saveDevPost(saveDevPostRequest.toServiceRequest(currDateTime));
    }

    /**
     * 개발 게시글 목록 조회
     */
    @GetMapping("/list")
    @Operation(summary = "게시글 목록 조회", description = "사용자가 개발 게시글 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개발 게시글 리스트를 반환한다. 만약 사진이 없으면 image 관련 필드는 null이 들어간다.",
                    content = @Content(schema = @Schema(implementation = DevPostListResponse.class))),
    })
    public ResponseEntity<?> showList(@RequestParam("page") int page, @RequestParam("size") int size){

        return devPostService.findDevPostList(page, size);
    }

    /**
     * 개발 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "사용자가 개발 게시글 단건을 상세 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개발 게시글을 반환한다.",
                    content = @Content(schema = @Schema(implementation = DevPostResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "postId에 해당하는 게시글이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")}
            ))
    })
    public ResponseEntity<?> showPost(@PathVariable("postId") Long postId){

        return devPostService.findPost(postId);
    }

    /**
     * 개발 게시글 수정
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PatchMapping("/modify")
    @Operation(summary = "게시글 수정", description = "사용자가 개발 게시글을 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개발 게시글을 반환한다.",
                    content = @Content(schema = @Schema(implementation = DevPostResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "id에 해당하는 게시글이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")}
            ))
    })
    public ResponseEntity<?> modifyPost(@Valid @RequestBody ModifyDevPostRequest modifyDevPostRequest){

        return devPostService.modifyPost(modifyDevPostRequest.toServiceRequest(LocalDateTime.now()));
    }

}