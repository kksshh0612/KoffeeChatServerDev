package teamkiim.koffeechat.domain.post.community.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyCommunityPostServiceRequest {

    private Long id;
    private String title;
    private String bodyContent;
}
