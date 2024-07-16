package teamkiim.koffeechat.memberfollow.controller;

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
import teamkiim.koffeechat.memberfollow.service.MemberFollowService;
import teamkiim.koffeechat.memberfollow.service.dto.MemberFollowListResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-follow")
@Tag(name = "회원 구독 API")
public class MemberFollowController {

    private final MemberFollowService memberFollowService;

    /**
     * 구독
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/{memberId}")
    @Operation(summary = "회원 구독", description = "사용자가 다른 사용자를 구독/구독 취소한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다른 사용자의 팔로워 수를 반환한다.",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 구독을 누르는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}

            ))
    })
    public ResponseEntity<?> memberFollow(@PathVariable("memberId") Long followingMemberId, HttpServletRequest request) {
        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return memberFollowService.memberFollow(memberId, followingMemberId);
    }

    /**
     * 사용자 follower list 확인
     */
    @Auth(role={})
    @GetMapping("/follower-list/{memberId}")
    @Operation(summary = "회원 구독자 목록 확인", description = "사용자가 특정 회원의 구독자 목록을 확인한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원의 구독자 리스트를 반환한다.",
                    content = @Content(schema = @Schema(implementation = MemberFollowListResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    public ResponseEntity<?> followerList(@PathVariable("memberId") Long memberId, @RequestParam("page") int page, @RequestParam("size") int size, HttpServletRequest request) {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return memberFollowService.followerList(memberId, loginMemberId, page, size);  //팔로워 목록 확인 대상 member id
    }

}
