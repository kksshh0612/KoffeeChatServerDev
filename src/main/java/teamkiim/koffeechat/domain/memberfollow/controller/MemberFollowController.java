package teamkiim.koffeechat.domain.memberfollow.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.memberfollow.dto.MemberFollowListResponse;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.domain.memberfollow.service.MemberFollowService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-follow")
public class MemberFollowController {

    private final MemberFollowService memberFollowService;

    /**
     * 팔로우 / 언팔로우
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/{memberId}")
    @MemberFollowApiDocument.FollowMember
    public ResponseEntity<?> followMember(@PathVariable("memberId") Long followingMemberId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberFollowService.followMember(memberId, followingMemberId);

        return ResponseEntity.ok("");
    }

    /**
     * 본인의 follower list 확인
     */
    @Auth(role={})
    @GetMapping("/follower-list")
    @MemberFollowApiDocument.FollowerList
    public ResponseEntity<?> followerList(@RequestParam("page") int page, @RequestParam("size") int size,
                                          HttpServletRequest request) {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.findFollowerList(null, loginMemberId, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * 타회원의 follower list 확인
     */
    @Auth(role={})
    @GetMapping("/follower-list/{memberId}")
    @MemberFollowApiDocument.FollowerList
    public ResponseEntity<?> followerList(@PathVariable("memberId") Long memberId,
                                          @RequestParam("page") int page, @RequestParam("size") int size,
                                          HttpServletRequest request) {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.findFollowerList(memberId, loginMemberId, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * 본인의 following list 확인
     */
    @Auth(role={})
    @GetMapping("/following-list")
    @MemberFollowApiDocument.FollowingList
    public ResponseEntity<?> followingList(@RequestParam("page") int page, @RequestParam("size") int size,
                                           HttpServletRequest request) {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.findFollowingList(null, loginMemberId, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * 타회원의 following list 확인
     */
    @Auth(role={})
    @GetMapping("/following-list/{memberId}")
    @MemberFollowApiDocument.FollowingList
    public ResponseEntity<?> followingList(@PathVariable("memberId") Long memberId,
                                           @RequestParam("page") int page, @RequestParam("size") int size,
                                           HttpServletRequest request) {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.findFollowingList(memberId, loginMemberId, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

}
