package teamkiim.koffeechat.domain.file.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUrlResponse {

    private String presignedUrl;
    private String key;
    private String url;

    public static ImageUrlResponse of(String presgnedUrl, String key, String url) {
        return new ImageUrlResponse(presgnedUrl, key, url);
    }
}
