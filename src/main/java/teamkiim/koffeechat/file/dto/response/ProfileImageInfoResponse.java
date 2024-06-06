package teamkiim.koffeechat.file.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileImageInfoResponse {

    private String profileImagePath;
    private String profileImageName;

    public static ProfileImageInfoResponse of(String path, String name){
        return ProfileImageInfoResponse.builder()
                .profileImagePath(path)
                .profileImageName(name)
                .build();
    }
}
