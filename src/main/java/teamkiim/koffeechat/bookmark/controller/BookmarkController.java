package teamkiim.koffeechat.bookmark.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.bookmark.service.BookmarkService;
import teamkiim.koffeechat.bookmark.service.dto.BookmarkPostListResponse;
import teamkiim.koffeechat.global.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
@Tag(name = "북마크 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 북마크 한 게시글 목록 조회
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @GetMapping("/list")
    @Operation(summary = "게시글 목록 조회", description = "사용자가 북마크한 게시글 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크한 게시글 리스트를 반환한다. 만약 사진이 없으면 image 관련 필드는 null이 들어간다.",
                    content = @Content(schema = @Schema(implementation = BookmarkPostListResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 북마크 목록을 확인하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
    })
    public ResponseEntity<?> showList(@RequestParam("page") int page, @RequestParam("size") int size, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return bookmarkService.findBookmarkPostList(memberId, page, size);
    }
}
