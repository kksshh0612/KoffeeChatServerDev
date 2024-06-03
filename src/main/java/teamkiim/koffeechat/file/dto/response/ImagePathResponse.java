package teamkiim.koffeechat.file.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.file.domain.File;

@Getter
@AllArgsConstructor
public class ImagePathResponse {

    private String path;
    private String name;

    public static ImagePathResponse of(File file){

        String nameCombinedId = new StringBuilder()
                .append(file.getName()).append("#").append(file.getId()).toString();

        return new ImagePathResponse(file.getPath(), nameCombinedId);
    }
}
