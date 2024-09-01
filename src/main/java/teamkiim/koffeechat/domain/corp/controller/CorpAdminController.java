package teamkiim.koffeechat.domain.corp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.corp.controller.dto.request.CreateApprovedCorpRequest;
import teamkiim.koffeechat.domain.corp.controller.dto.response.AdminCorpDomainListResponse;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.corp.service.CorpAdminService;
import teamkiim.koffeechat.global.Auth;

import java.util.List;

import static teamkiim.koffeechat.global.Auth.MemberRole.ADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/corps/admin")
@Tag(name = "[ADMIN] 현직자 인증 도메인 관리 API")
public class CorpAdminController {

    private final CorpAdminService corpAdminService;

    /**
     * 회사 도메인 등록
     */
    @Auth(role = ADMIN)
    @PostMapping("")
    @CorpAdminApiDocument.CreateApprovedCorp
    public ResponseEntity<?> createApprovedCorp(@Valid @RequestBody CreateApprovedCorpRequest createApprovedCorpRequest) {
        corpAdminService.createApprovedCorp(createApprovedCorpRequest.getCorpName(), createApprovedCorpRequest.getCorpEmailDomain());

        return ResponseEntity.ok("도메인 등록 완료.");
    }

    /**
     * 도메인 상태 관리 : 승인, 거절
     */
    @Auth(role = ADMIN)
    @PatchMapping("/{corpId}")
    @CorpAdminApiDocument.UpdateCorpVerified
    public ResponseEntity<?> updateCorpVerified(@PathVariable("corpId") Long corpId, @Valid @RequestBody Verified verifiedRequest) {
        Verified verified = corpAdminService.updateCorpVerified(corpId, verifiedRequest);

        return ResponseEntity.ok(verified);
    }

    /**
     * 도메인 삭제
     */
    @Auth(role = ADMIN)
    @DeleteMapping("/{corpId}")
    @CorpAdminApiDocument.DeleteCorp
    public ResponseEntity<?> deleteCorp(@PathVariable("corpId") Long corpId) {
        corpAdminService.deleteCorp(corpId);

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

        return ResponseEntity.ok().body(responseList);
    }

    /**
     * 도메인 검색 : 회사 이름, 이메일
     */
    @Auth(role = ADMIN)
    @GetMapping("/search")
    @CorpAdminApiDocument.GetCorp
    public ResponseEntity<?> getCorp(@RequestBody String keyword) {
        List<AdminCorpDomainListResponse> responseList = corpAdminService.findCorpByKeyword(keyword);

        return ResponseEntity.ok().body(responseList);
    }
}
