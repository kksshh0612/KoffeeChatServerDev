package teamkiim.koffeechat.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.MemberRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyProfileServiceRequest {

    private String nickname;
    private MemberRole memberRole;
}
