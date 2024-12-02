package teamkiim.koffeechat.domain.admin.corp.controller;


import static teamkiim.koffeechat.domain.member.domain.MemberRole.ADMIN;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import teamkiim.koffeechat.domain.admin.corp.controller.request.CreateApprovedCorpRequest;
import teamkiim.koffeechat.domain.admin.corp.controller.request.UpdateCorpDomainStatusRequest;
import teamkiim.koffeechat.domain.admin.corp.service.CorpAdminService;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/corps")
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
    @PatchMapping("/{corpId}")
    @CorpAdminApiDocument.UpdateCorpVerified
    public ResponseEntity<?> approveCorpDomain(@PathVariable("corpId") String corpId,
                                               @Valid @RequestBody UpdateCorpDomainStatusRequest updateCorpDomainStatusRequest) {

        Long decryptedCorpId = aesCipherUtil.decrypt(corpId);

        corpAdminService.updateCorpDoaminStatus(decryptedCorpId, updateCorpDomainStatusRequest.toServiceRequest());

        return ResponseEntity.ok("승인/거절 완료");
    }

    /**
     * 도메인 검색 : 회사 이름, 이메일 (keyword 없을 시, 전체 검색)
     */
    @Auth(role = ADMIN)
    @GetMapping("")
    @CorpAdminApiDocument.getCorpList
    public ResponseEntity<?> getCorpList(@RequestParam(value = "keyword", required = false) String keyword) {

        return ResponseEntity.ok(corpAdminService.findCorpListByKeyword(keyword));
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
}
