package teamkiim.koffeechat.domain.post.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.tag.domain.Tag;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagInfoDto {

    private Long id;
    private String content;

    public static TagInfoDto of(Tag tag) {
        return TagInfoDto.builder()
                .id(tag.getId())
                .content(tag.getContent())
                .build();
    }
}
