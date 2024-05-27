package teamkiim.koffeechat.post.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyCommunityPostServiceRequest {

    private Long id;
    private String title;
    private String bodyContent;
    private LocalDateTime currDateTime;
}
