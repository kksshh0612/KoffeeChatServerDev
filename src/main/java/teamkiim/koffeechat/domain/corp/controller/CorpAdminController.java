package teamkiim.koffeechat.domain.corp.controller;


import static teamkiim.koffeechat.domain.member.domain.MemberRole.ADMIN;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.corp.controller.dto.request.CreateApprovedCorpRequest;
import teamkiim.koffeechat.domain.corp.dto.response.AdminCorpDomainListResponse;
import teamkiim.koffeechat.domain.corp.service.CorpAdminService;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/corps/admin")
@Tag(name = "[ADMIN] 현직자 인증 도메인 관리 API")
public class CorpAdminController {

    private final CorpAdminService corpAdminService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 회사 도메인 등록
     */
    @Auth(role = ADMIN)
    @PostMapping("")
    @CorpAdminApiDocument.CreateApprovedCorp
    public ResponseEntity<?> createApprovedCorp(
            @Valid @RequestBody CreateApprovedCorpRequest createApprovedCorpRequest) {

        corpAdminService.createApprovedCorp(createApprovedCorpRequest.getCorpName(),
                createApprovedCorpRequest.getCorpEmailDomain());

        return ResponseEntity.ok("도메인 등록 완료.");
    }

    /**
     * 도메인 상태 관리 : 승인, 거절
     */
    @Auth(role = ADMIN)
    @PatchMapping("/approve/{corpId}")
    @CorpAdminApiDocument.UpdateCorpVerified
    public ResponseEntity<?> approveCorpDomain(@PathVariable("corpId") String corpId) {

        Long decryptedCorpId = aesCipherUtil.decrypt(corpId);

        corpAdminService.approveCorpDomain(decryptedCorpId);

        return ResponseEntity.ok("승인 완료");
    }

    @Auth(role = ADMIN)
    @PatchMapping("/reject/{corpId}")
    @CorpAdminApiDocument.UpdateCorpVerified
    public ResponseEntity<?> rejectCorpDomain(@PathVariable("corpId") String corpId) {

        Long decryptedCorpId = aesCipherUtil.decrypt(corpId);

        corpAdminService.rejectCorpDomain(decryptedCorpId);

        return ResponseEntity.ok("거절 완료");
    }

    /**
     * 도메인 삭제
     */
    @Auth(role = ADMIN)
    @DeleteMapping("/{corpId}")
    @CorpAdminApiDocument.DeleteCorp
    public ResponseEntity<?> deleteCorp(@PathVariable("corpId") String corpId) {
        Long decryptedCorpId = aesCipherUtil.decrypt(corpId);
        corpAdminService.deleteCorp(decryptedCorpId);

        return ResponseEntity.ok("도메인 삭제 완료.");
    }

    /**
     * 전체 도메인 목록 출력
     */
    @Auth(role = ADMIN)
    @GetMapping("")
    @CorpAdminApiDocument.ListCorp
    public ResponseEntity<?> listCorp() {
        List<AdminCorpDomainListResponse> responseList = corpAdminService.listCorp();

        return ResponseEntity.ok(responseList);
    }

    /**
     * 도메인 검색 : 회사 이름, 이메일
     */
    @Auth(role = ADMIN)
    @GetMapping("/keyword")
    @CorpAdminApiDocument.GetCorp
    public ResponseEntity<?> getCorp(@RequestParam("keyword") String keyword) {
        List<AdminCorpDomainListResponse> responseList = corpAdminService.findCorpByKeyword(keyword);

        return ResponseEntity.ok(responseList);
    }
}
