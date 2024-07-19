package teamkiim.koffeechat.post.community.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCommunityPostServiceRequest {

    private Long id;
    private String title;
    private String bodyContent;
    private List<Long> fileIdList;
}
