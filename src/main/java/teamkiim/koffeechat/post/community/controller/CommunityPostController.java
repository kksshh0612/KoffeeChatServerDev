package teamkiim.koffeechat.post.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.post.community.dto.response.CommunityPostViewResponse;
import teamkiim.koffeechat.post.community.service.CommunityPostService;
import teamkiim.koffeechat.post.dto.request.PostCreateRequest;

@RestController
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    /**
     * 비개발 게시글 생성
     */
    @Auth
    @PostMapping("/com-write")
    @Operation(summary = "비개발 게시글 생성", description = "사용자가 비개발 게시글 생성을 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공")
    })
    public ResponseEntity<CommunityPostViewResponse> createPost(
            @Valid @RequestBody PostCreateRequest postDto, HttpServletRequest request) {
        //작성자 id
        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        //게시글 생성
        CommunityPostViewResponse comPostDto = communityPostService.createCommunityPost(postDto, memberId);

        //생성된 게시물 반환
        return ResponseEntity.ok(comPostDto);
    }
}
