package teamkiim.koffeechat.domain.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.auth.service.AuthService;
import teamkiim.koffeechat.domain.email.dto.request.EmailAuthRequest;
import teamkiim.koffeechat.domain.member.controller.dto.request.CheckPasswordRequest;
import teamkiim.koffeechat.domain.member.controller.dto.request.EnrollSkillCategoryRequest;
import teamkiim.koffeechat.domain.member.controller.dto.request.ModifyProfileRequest;
import teamkiim.koffeechat.domain.member.dto.request.EnrollSkillCategoryServiceRequest;
import teamkiim.koffeechat.domain.member.dto.request.UpdatePasswordRequest;
import teamkiim.koffeechat.domain.member.service.MemberService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "사용자 API")
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 사용자 닉네임으로 암호화된 pk 요청
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/{memberId}")
    @MemberApiDocument.GetMemberPK
    public ResponseEntity<?> getMemberPK(@PathVariable(value = "memberId") String memberEmailId) {

        return ResponseEntity.ok(memberService.getMemberPK(memberEmailId));
    }

    /**
     * 프로필 사진 등록 (Local 환경)
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/enroll-profile-image/local")
    @MemberApiDocument.SaveProfileImage
    public ResponseEntity<?> saveProfileImageToLocal(@RequestPart(value = "file") MultipartFile multipartFile,
                                                     HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(memberService.enrollProfileImageToLocal(memberId, multipartFile));
    }

    /**
     * 프로필 사진 등록 (S3 환경)
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/enroll-profile-image/s3")
    @MemberApiDocument.SaveProfileImage
    public ResponseEntity<?> saveProfileImageToS3(@RequestPart(value = "file") MultipartFile multipartFile,
                                                  HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(memberService.enrollProfileImageToS3(memberId, multipartFile));
    }

    /**
     * 관심 기술 카테고리 등록
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/enroll-skills")
    @MemberApiDocument.EnrollSkills
    public ResponseEntity<?> enrollSkills(
            @Valid @RequestBody List<EnrollSkillCategoryRequest> enrollSkillCategoryRequest,
            HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<EnrollSkillCategoryServiceRequest> serviceRequestList = enrollSkillCategoryRequest.stream()
                .map(EnrollSkillCategoryRequest::toServiceRequest).toList();

        memberService.enrollSkillCategory(memberId, serviceRequestList);

        return ResponseEntity.ok("관심 기술 설정 완료");
    }

    /**
     * 프로필 수정
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/profile")
    @MemberApiDocument.ModifyProfile
    public ResponseEntity<?> modifyProfile(@Valid @RequestBody ModifyProfileRequest modifyProfileRequest,
                                           HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberService.modifyProfile(modifyProfileRequest.toServiceRequest(), memberId);

        return ResponseEntity.ok("회원 정보 수정 완료");

    }

    /**
     * 본인 프로필 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/profile")
    @MemberApiDocument.FindProfile
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
    public ResponseEntity<?> findProfile(@PathVariable("profileMemberId") String profileMemberId,
                                         HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedProfileMemberId = aesCipherUtil.decrypt(profileMemberId);

        return ResponseEntity.ok(memberService.findMemberInfo(decryptedProfileMemberId, memberId));
    }

    /**
     * 사용자 이메일 변경 시 인증 메시지 전송
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/email")
    @MemberApiDocument.SendNewAuthEmail
    public ResponseEntity<?> sendNewAuthEmail(@Valid @RequestBody EmailAuthRequest emailAuthRequest,
                                              HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberService.sendNewAuthEmail(memberId, emailAuthRequest.getEmail());

        return ResponseEntity.ok("이메일 전송 완료, 이메일을 확인해 주세요.");
    }

    /**
     * 인증 코드 확인 후 이메일 변경
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/email")
    @MemberApiDocument.UpdateNewEmailApiDoc
    public ResponseEntity<?> updateNewAuthEmail(@RequestBody String email, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberService.updateNewAuthEmail(memberId, email);

        return ResponseEntity.ok("이메일 변경되었습니다.");
    }

    /**
     * 비밀번호 인증
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/password")
    @MemberApiDocument.CheckCurrentPasswordApiDoc
    public ResponseEntity<?> checkCurrentPassword(@RequestBody CheckPasswordRequest password,
                                                  HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberService.checkCurrentPassword(memberId, password.getPassword());

        return ResponseEntity.ok("비밀번호 확인 완료");
    }

    /**
     * 비밀번호 변경
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/password")
    @MemberApiDocument.UpdatePasswordApiDoc
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest passwordRequest,
                                            HttpServletRequest request, HttpServletResponse response) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        memberService.updatePassword(memberId, passwordRequest);
        authService.logout(request, response);

        return ResponseEntity.ok("비밀번호 변경 완료. 다시 로그인해주세요.");
    }
}
