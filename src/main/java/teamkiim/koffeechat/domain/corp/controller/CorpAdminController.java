package teamkiim.koffeechat.domain.corp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.corp.controller.admindto.request.EnrollCorpDomainRequest;
import teamkiim.koffeechat.domain.corp.controller.admindto.request.ManageCorpDomainRequest;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.corp.service.CorpAdminService;
import teamkiim.koffeechat.global.Auth;

import static teamkiim.koffeechat.global.Auth.MemberRole.ADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/corp")
@Tag(name="[ADMIN] 현직자 인증 도메인 관리 API")
public class CorpAdminController {

    private final CorpAdminService corpAdminService;

    /**
     * 도메인 등록
     */
    @Auth(role=ADMIN)
    @PostMapping("/enroll")
    @CorpAdminApiDocument.EnrollCorpDomain
    public ResponseEntity<?> enrollCorpDomain(@Valid @RequestBody EnrollCorpDomainRequest enrollCorpDomainRequest) {
        corpAdminService.saveCorpDomain(enrollCorpDomainRequest.getCorpName(), enrollCorpDomainRequest.getCorpEmailDomain());

        return ResponseEntity.status(HttpStatus.CREATED).body("인증된 도메인이 추가되었습니다.");
    }

    /**
     * 도메인 상태 관리 : 승인, 거절
     */
    @Auth(role=ADMIN)
    @PostMapping("/modify")
    @CorpAdminApiDocument.ManageCorpDomain
    public ResponseEntity<?> manageCorpDomain(@Valid @RequestBody ManageCorpDomainRequest manageCorpDomainRequest) {
        Verified verified= corpAdminService.modifyCorpDomain(manageCorpDomainRequest.getCorpName(), manageCorpDomainRequest.getCorpEmailDomain(), manageCorpDomainRequest.getVerified());

        return ResponseEntity.status(HttpStatus.CREATED).body(verified);
    }

}
