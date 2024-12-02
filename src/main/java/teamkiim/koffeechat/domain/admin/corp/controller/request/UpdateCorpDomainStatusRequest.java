package teamkiim.koffeechat.domain.admin.corp.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.admin.corp.domain.VerifyStatus;
import teamkiim.koffeechat.domain.admin.corp.dto.request.UpdateCorpDomainStatusServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCorpDomainStatusRequest {

    @NotNull
    private VerifyStatus verifyStatus;

    public UpdateCorpDomainStatusServiceRequest toServiceRequest() {
        return new UpdateCorpDomainStatusServiceRequest(verifyStatus);
    }
}
