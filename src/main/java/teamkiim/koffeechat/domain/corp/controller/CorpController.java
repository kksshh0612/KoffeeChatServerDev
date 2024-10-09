package teamkiim.koffeechat.domain.corp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.corp.controller.dto.request.CorpAuthCodeCheckRequest;
import teamkiim.koffeechat.domain.corp.controller.dto.request.CorpAuthRequest;
import teamkiim.koffeechat.domain.corp.controller.dto.request.CorpDomainRequest;
import teamkiim.koffeechat.domain.corp.dto.response.CorpDomainResponse;
import teamkiim.koffeechat.domain.corp.service.CorpService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/corps")
@Tag(name = "현직자 인증 API")
public class CorpController {

    private final CorpService corpService;

    /**
     * 도메인 승인 요청 -> 대기 상태로 관리자에게 검증 요청
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("")
    @CorpApiDocument.CreateWaitingCorp
    public ResponseEntity<?> createWaitingCorp(@Valid @RequestBody CorpDomainRequest corpDomainRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        String response = corpService.createWaitingCorp(memberId, corpDomainRequest.getName(), corpDomainRequest.getEmailDomain());

        return ResponseEntity.ok(response);
    }

    /**
     * 회사 도메인 검색
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/name")
    @CorpApiDocument.FindCorpDomain
    public ResponseEntity<?> findCorpDomain(@RequestParam("name") String corpName) {

        //회사 이름으로 검색
        List<CorpDomainResponse> corpList = corpService.findCorpDomain(corpName);

        if (corpList.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        return ResponseEntity.ok(corpList);
    }

    @AuthenticatedMemberPrincipal
    @GetMapping("/domain")
    @CorpApiDocument.FindCorpDomain
    public ResponseEntity<?> findCorpName(@RequestParam("domain") String corpDomain) {

        //회사 도메인으로 검색
        List<CorpDomainResponse> corpList = corpService.findCorpName(corpDomain);

        if (corpList.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        return ResponseEntity.ok(corpList);
    }

    /**
     * 현직자 인증 시 이메일 전송
     * 이메일 보내고 인증까지 받고나서 admin 최종 승인받기 (승인 목록)
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/email-auth")
    @CorpApiDocument.CreateEmailAuth
    public ResponseEntity<?> createEmailAuth(@Valid @RequestBody CorpAuthRequest corpRequest, HttpServletRequest request) throws Exception {

        String memberId = String.valueOf(request.getAttribute("authenticatedMemberPK"));
        String response = corpService.createEmailAuth(corpRequest.getName(), corpRequest.getEmail(), memberId);

        return ResponseEntity.ok(response);
    }

    /**
     * 회사 이메일 인증 코드 확인
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/email-auth/code")
    @CorpApiDocument.CheckEmailAuthCode
    public ResponseEntity<?> checkEmailAuthCode(@Valid @RequestBody CorpAuthCodeCheckRequest codeCheckRequest, HttpServletRequest request) throws Exception {

        String memberId = String.valueOf(request.getAttribute("authenticatedMemberPK"));
        corpService.checkEmailAuthCode(memberId, codeCheckRequest.getName(), codeCheckRequest.getEmail(), codeCheckRequest.getCode());

        return ResponseEntity.ok("인증 되었습니다.");
    }

}
