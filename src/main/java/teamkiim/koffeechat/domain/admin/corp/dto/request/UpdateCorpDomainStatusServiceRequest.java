package teamkiim.koffeechat.domain.admin.corp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.admin.corp.domain.VerifyStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCorpDomainStatusServiceRequest {

    private VerifyStatus verifyStatus;
}
