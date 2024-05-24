package teamkiim.koffeechat.post.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.file.domain.File;

@Getter
@AllArgsConstructor
@Builder
public class ImageFileInfoDto {

    private final Long id;
    private final String path;
    private final String fileName;

    public static ImageFileInfoDto of(File file){

        return ImageFileInfoDto.builder()
                .id(file.getId())
                .path(file.getPath())
                .fileName(file.getName())
                .build();
    }
}
