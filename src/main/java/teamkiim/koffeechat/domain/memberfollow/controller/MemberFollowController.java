package teamkiim.koffeechat.domain.memberfollow.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.memberfollow.dto.MemberFollowListResponse;
import teamkiim.koffeechat.domain.memberfollow.service.MemberFollowService;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-follow")
@Tag(name = "팔로우 API")
public class MemberFollowController {

    private final MemberFollowService memberFollowService;

    /**
     * 팔로우 / 언팔로우
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/{memberId}")
    @MemberFollowApiDocument.FollowMember
    public ResponseEntity<?> followMember(@PathVariable("memberId") String followingMemberId, HttpServletRequest request) throws Exception {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberFollowService.followMember(memberId, followingMemberId);

        return ResponseEntity.ok("");
    }

    /**
     * 본인 follower list 확인
     */
    @Auth(role = {})
    @GetMapping("/followers")
    @MemberFollowApiDocument.MyFollowerList
    public ResponseEntity<?> myFollowerList(@RequestParam("page") int page, @RequestParam("size") int size,
                                            HttpServletRequest request) {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.findMyFollowerList(loginMemberId, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * 다른 사용자의 follower list 확인
     */
    @Auth(role = {})
    @GetMapping("/followers/{memberId}")
    @MemberFollowApiDocument.FollowerList
    public ResponseEntity<?> followerList(@PathVariable("memberId") String memberId, @RequestParam("page") int page,
                                          @RequestParam("size") int size, HttpServletRequest request) throws Exception {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.findFollowerList(memberId, loginMemberId, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * 본인 following list 확인
     */
    @Auth(role = {})
    @GetMapping("/followings")
    @MemberFollowApiDocument.MyFollowingList
    public ResponseEntity<?> myFollowingList(@RequestParam("page") int page, @RequestParam("size") int size,
                                             HttpServletRequest request) throws Exception {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList = memberFollowService.findMyFollowingList(loginMemberId, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * 다른 사용자의 following list 확인
     */
    @Auth(role = {})
    @GetMapping("/followings/{memberId}")
    @MemberFollowApiDocument.FollowingList
    public ResponseEntity<?> followingList(@PathVariable("memberId") String memberId, @RequestParam("page") int page,
                                           @RequestParam("size") int size, HttpServletRequest request) throws Exception {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.findFollowingList(memberId, loginMemberId, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * 본인 follower 목록에서 사용자 검색
     */
    @Auth(role = {})
    @GetMapping("/followers/search")
    @MemberFollowApiDocument.SearchFollowerList
    public ResponseEntity<?> searchMyFollowers(@RequestParam("keyword") String keyword, @RequestParam("page") int page,
                                               @RequestParam("size") int size, HttpServletRequest request) throws Exception {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.searchMyFollowers(loginMemberId, keyword, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * follower 목록에서 사용자 검색
     */
    @Auth(role = {})
    @GetMapping("/followers/{memberId}/search")
    @MemberFollowApiDocument.SearchFollowerList
    public ResponseEntity<?> searchFollowers(@PathVariable("memberId") String memberId, @RequestParam("keyword") String keyword,
                                             @RequestParam("page") int page, @RequestParam("size") int size, HttpServletRequest request) throws Exception {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.searchFollowers(memberId, loginMemberId, keyword, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * 본인 following 목록에서 사용자 검색
     */
    @Auth(role = {})
    @GetMapping("/followings/search")
    @MemberFollowApiDocument.SearchFollowingList
    public ResponseEntity<?> searchMyFollowings(@RequestParam("keyword") String keyword, @RequestParam("page") int page,
                                                @RequestParam("size") int size, HttpServletRequest request) throws Exception {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.searchMyFollowings(loginMemberId, keyword, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

    /**
     * following 목록에서 사용자 검색
     */
    @Auth(role = {})
    @GetMapping("/followings/{memberId}/search")
    @MemberFollowApiDocument.SearchFollowingList
    public ResponseEntity<?> searchFollowings(@PathVariable("memberId") String memberId, @RequestParam("keyword") String keyword,
                                              @RequestParam("page") int page, @RequestParam("size") int size,
                                              HttpServletRequest request) throws Exception {

        Long loginMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MemberFollowListResponse> memberFollowListResponseList =
                memberFollowService.searchFollowings(memberId, loginMemberId, keyword, page, size);

        return ResponseEntity.ok(memberFollowListResponseList);
    }

}
