package teamkiim.koffeechat.post.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.post.common.service.PostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "게시판 공통API")
public class PostController {

    private final PostService postService;

    /**
     * 좋아요
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/like/{postId}")
    @Operation(summary = "게시글 좋아요", description = "사용자가 개발, 커뮤니티 게시판에 좋아요를 누른다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성된 게시글의 PK를 반환한다.",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 좋아요를 누르는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}"),
                            @ExampleObject(name = "게시물을 찾을 수 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 게시물이 존재하지 않습니다\"}")}

            ))
    })
    public ResponseEntity<?> like(@PathVariable("postId") Long postId, HttpServletRequest request){

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return postService.like(postId, memberId);
    }

    /**
     * 게시글 삭제 (soft delete)
     */
    @DeleteMapping("delete/{postId}")
    @Operation(summary = "게시글 삭제", description = "사용자가 작성한 게시물을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성된 게시글의 PK를 반환한다.",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "게시물을 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시물이 존재하지 않습니다\"}")}
            ))
    })
    public ResponseEntity<?> delete(@PathVariable("postId") Long postId){

        return postService.softDelete(postId);
    }
}
