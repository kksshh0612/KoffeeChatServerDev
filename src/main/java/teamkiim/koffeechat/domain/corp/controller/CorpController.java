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
import teamkiim.koffeechat.domain.corp.controller.dto.request.CorpDomainRequest;
import teamkiim.koffeechat.domain.corp.controller.dto.request.FindCorpDomainRequest;
import teamkiim.koffeechat.domain.corp.controller.dto.request.FindCorpNameRequest;
import teamkiim.koffeechat.domain.corp.service.CorpService;
import teamkiim.koffeechat.domain.corp.service.dto.response.CorpDomainResponse;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/corp")
@Tag(name="현직자 인증 API")
public class CorpController {

    private final CorpService corpService;

    /**
     * 도메인 승인 요청 -> 대기 상태로 관리자에게 검증 요청
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/domain")
    @CorpApiDocument.RequestCorpDomain
    public ResponseEntity<?> requestCorpDomain(@Valid @RequestBody CorpDomainRequest corpDomainRequest) {
        String response= corpService.saveCorpRequest(corpDomainRequest.getCorpName(), corpDomainRequest.getCorpEmailDomain());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 회사 도메인 검색
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/find-corp-mail")
    @CorpApiDocument.FindCorpDomain
    public ResponseEntity<?> findCorpDomain(@Valid @RequestBody FindCorpNameRequest corpNameRequest) {

        //회사 이름으로 검색
        List<CorpDomainResponse> corpList= corpService.findCorpRequest(corpNameRequest.getCorpName());

        if (corpList.isEmpty()) {
            return ResponseEntity.ok().body("검색 결과가 없습니다. 도메인 검증을 요청해주세요.");
        }

        return ResponseEntity.ok().body(corpList);
    }

    @AuthenticatedMemberPrincipal
    @PostMapping("/find-corp-name")
    @CorpApiDocument.FindCorpDomain
    public ResponseEntity<?> findCorpName(@Valid @RequestBody FindCorpDomainRequest corpDomainRequest) {

        //회사 도메인으로 검색
        List<CorpDomainResponse> corpList= corpService.findCorpNameRequest(corpDomainRequest.getCorpEmailDomain());

        if (corpList.isEmpty()) {
            return ResponseEntity.ok().body("검색 결과가 없습니다. 도메인 검증을 요청해주세요.");
        }

        return ResponseEntity.ok().body(corpList);
    }


    /**
     * 이메일 인증 진행
     */

}
