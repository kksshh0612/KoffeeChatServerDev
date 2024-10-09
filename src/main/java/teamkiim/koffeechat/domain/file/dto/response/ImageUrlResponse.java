package teamkiim.koffeechat.domain.file.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUrlResponse {

    private String url;

    public static ImageUrlResponse of(String url) {
        return new ImageUrlResponse(url);
    }
}
