package teamkiim.koffeechat.domain.corp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.VerifyStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회사 도메인 리스트 Response")
public class AdminCorpDomainListResponse {

    @Schema(description = "암호화된 회사 도메인 pk", example = "1qwer")
    private String id;                       //도메인 pk

    @Schema(description = "회사 이름", example = "커피챗")
    private String name;               //회사 이름

    @Schema(description = "회사 도메인", example = "koffeechat.com")
    private String emailDomain;        //회사 이메일 도메인

    @Schema(description = "도메인 승인 상태", example = "WAITING")
    private VerifyStatus status;             //상태

    public static AdminCorpDomainListResponse of(String corpId, Corp corp) {
        return AdminCorpDomainListResponse.builder()
                .id(corpId)
                .name(corp.getName())
                .emailDomain(corp.getEmailDomain())
                .status(corp.getVerifyStatus())
                .build();
    }
}
