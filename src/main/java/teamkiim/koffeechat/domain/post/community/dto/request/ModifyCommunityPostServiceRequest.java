package teamkiim.koffeechat.domain.post.community.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyCommunityPostServiceRequest {

    private String title;
    private String bodyContent;
    private List<String> fileUrlList;
    private List<String> tagContentList;
}
