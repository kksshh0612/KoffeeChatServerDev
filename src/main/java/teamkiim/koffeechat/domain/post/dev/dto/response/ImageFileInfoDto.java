package teamkiim.koffeechat.domain.post.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.file.domain.File;

@Getter
@AllArgsConstructor
@Builder
public class ImageFileInfoDto {

    private final Long id;
    private final String url;

    public static ImageFileInfoDto of(File file){

        return ImageFileInfoDto.builder()
                .id(file.getId())
                .url(file.getUrl())
                .build();
    }
}
