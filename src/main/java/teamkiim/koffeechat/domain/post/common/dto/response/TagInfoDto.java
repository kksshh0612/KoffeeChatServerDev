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

    private String id;
    private String content;

    public static TagInfoDto of(String tagId, Tag tag) {
        return TagInfoDto.builder()
                .id(tagId)
                .content(tag.getContent())
                .build();
    }
}
