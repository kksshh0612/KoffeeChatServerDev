package teamkiim.koffeechat.post.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.post.dev.dto.response.DevPostViewResponse;
import teamkiim.koffeechat.post.dev.service.DevPostService;
import teamkiim.koffeechat.post.dto.request.CreatePostRequest;
import teamkiim.koffeechat.post.dto.request.UpdatePostRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DevPostController {

    private final DevPostService devPostService;

    /**
     * 개발 게시글 생성 : 생성된 개발 게시글을 보여주는 페이지 return
     */
    @Auth(role = {Auth.MemberRole.USER, Auth.MemberRole.ADMIN})
    @PostMapping("/dev-write")
    @Operation(summary = "게시글 생성", description = "사용자가 개발 게시판에 게시글 생성을 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 게시글을 쓰려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "제목 없이 게시글을 쓰려고 하는 경우",
                            value = "{\"code\":400, \"message\":\"제목을 입력해 주세요.\"}")}
            )),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "내용 없이 게시글을 쓰려고 하는 경우",
                            value = "{\"code\":400, \"message\":\"내용을 입력해 주세요.\"}")}
            ))
    })
    public ResponseEntity<DevPostViewResponse> createPost(
            @RequestBody @Valid CreatePostRequest postRequest, HttpServletRequest memberRequest) {

        Object memberIdObject = memberRequest.getAttribute("authenticatedMemberPK");
        //로그인 되어있지 않은 상태 -> 로그인 요청 에러
        if (memberIdObject == null) throw new CustomException(ErrorCode.UNAUTHORIZED);

        //게시글 작성 시 제목, 내용을 쓰지 않은 경우
        if (postRequest.getTitle().isEmpty()) throw new CustomException(ErrorCode.POST_REQUEST_WITHOUT_TITLE);
        if(postRequest.getBodyContent().isEmpty()) throw new CustomException(ErrorCode.POST_REQUEST_WITHOUT_CONTENT);

        //작성자 id
        Long memberId = Long.valueOf(String.valueOf(memberIdObject));
        //게시글 생성
        DevPostViewResponse createPostResponse = devPostService.createDevPost(postRequest, memberId);

        //생성된 게시물 반환
        return ResponseEntity.ok(createPostResponse);
    }

    /**
     * 전체 개발 게시글 목록 조회
     */
    @GetMapping("/dev-posts")
    @Operation(summary = "개발 게시글 목록 조회", description = "사용자가 개발 게시글 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    })
    public ResponseEntity<DevPostViewResponseResult> list() {
        List<DevPostViewResponse> posts = devPostService.findDevPosts();  //게시글 목록 불러오기

        return ResponseEntity.ok(new DevPostViewResponseResult(posts));
    }

    //게시글 리스트 object data type으로 반환
    @Data
    @AllArgsConstructor
    static class DevPostViewResponseResult<T>{
        private T data;
    }

    /**
     * 개발 게시글 제목, 내용, 카테고리 수정 가능
     * 수정 시간도 업데이트됨
     */
    @Auth
    @PutMapping("/posts/{postId}/edit")
    @Operation(summary = "게시글 수정", description = "사용자가 개발 게시글 수정을 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "게시글 작성자가 아닌 사용자가 게시글 수정을 요청하는 경우",
                            value = "{\"code\":403, \"message\":\"게시글 수정 권한이 없습니다.\"}")}
            ))
    })
    public ResponseEntity<DevPostViewResponse> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid UpdatePostRequest postRequest, HttpServletRequest memberRequest) {
        //작성자 id
        Long memberId = Long.valueOf(String.valueOf(memberRequest.getAttribute("authenticatedMemberPK")));
        DevPostViewResponse updatePostResponse = devPostService.updatePost(postId, postRequest, memberId);

        return ResponseEntity.ok(updatePostResponse);
    }

    /**
     * 카테고리별 게시글 목록 조회
     */
    @GetMapping("/posts/")
    @Operation(summary = "카테고리 별 개발 게시글 목록 조회", description = "사용자가 지정한 카테고리의 개발 게시글 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지정 카테고리 게시글 목록 조회 성공")
    })
    public ResponseEntity<DevPostViewResponseResultWithCategory> categoryDevList(@RequestParam("category-names") List<String> categoryNames) {
        List<DevPostViewResponse> devList = devPostService.findDevPostsByCategories(categoryNames);
        return ResponseEntity.ok(new DevPostViewResponseResultWithCategory(devList));
    }

    //게시글 리스트 object data type으로 반환
    @Data
    @AllArgsConstructor
    static class DevPostViewResponseResultWithCategory<T>{
        private T data;
    }

}
