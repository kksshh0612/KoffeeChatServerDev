package teamkiim.koffeechat.domain.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.email.dto.request.EmailAuthRequest;
import teamkiim.koffeechat.domain.member.controller.dto.EnrollSkillCategoryRequest;
import teamkiim.koffeechat.domain.member.controller.dto.ModifyProfileRequest;
import teamkiim.koffeechat.domain.member.dto.request.EnrollSkillCategoryServiceRequest;
import teamkiim.koffeechat.domain.member.service.MemberService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 프로필 사진 등록
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/enroll-profile-image")
    @MemberApiDocument.SaveProfileImage
    public ResponseEntity<?> saveProfileImage(@RequestPart(value = "file") MultipartFile multipartFile,
                                              HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(memberService.enrollProfileImage(memberId, multipartFile));
    }

    /**
     * 관심 기술 카테고리 등록
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/enroll-skills")
    @MemberApiDocument.EnrollSkills
    public ResponseEntity<?> enrollSkills(@Valid @RequestBody List<EnrollSkillCategoryRequest> enrollSkillCategoryRequest,
                                          HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<EnrollSkillCategoryServiceRequest> serviceRequestList = enrollSkillCategoryRequest.stream()
                .map(EnrollSkillCategoryRequest::toServiceRequest)
                .collect(Collectors.toList());

        memberService.enrollSkillCategory(memberId, serviceRequestList);

        return ResponseEntity.ok("관심 기술 설정 완료");
    }

    /**
     * 프로필 수정
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/profile")
    @MemberApiDocument.ModifyProfile
    public ResponseEntity<?> modifyProfile(@Valid @RequestBody ModifyProfileRequest modifyProfileRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberService.modifyProfile(modifyProfileRequest.toServiceRequest(), memberId);

        return ResponseEntity.ok("회원 정보 수정 완료");

    }

    /**
     * 본인 프로필 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/profile")
    @MemberApiDocument.FindMemberProfile
    public ResponseEntity<?> findProfile(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(memberService.findMemberInfo(null, memberId));
    }

    /**
     * 타회원 프로필 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/profile/{profileMemberId}")
    @MemberApiDocument.FindMemberProfile
    public ResponseEntity<?> findProfile(@PathVariable(value = "profileMemberId") Long profileMemberId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(memberService.findMemberInfo(profileMemberId, memberId));
    }

    /**
     * 사용자 이메일 변경 시 인증 메시지 전송
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/new-email")
    @MemberApiDocument.SendNewAuthEmail
    public ResponseEntity<?> sendNewAuthEmail(@Valid @RequestBody EmailAuthRequest emailAuthRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberService.sendNewAuthEmail(memberId, emailAuthRequest.getEmail());

        return ResponseEntity.ok("이메일 전송 완료, 이메일을 확인해 주세요.");
    }

    /**
     * 이메일 변경
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/new-email")
    @MemberApiDocument.UpdateNewEmailApiDoc
    public ResponseEntity<?> updateNewAuthEmail(@RequestBody String email, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberService.updateNewAuthEmail(memberId, email);

        return ResponseEntity.ok("이메일 변경되었습니다.");
    }


}
