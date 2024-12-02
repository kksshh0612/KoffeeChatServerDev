package teamkiim.koffeechat.domain.file.service;

import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;

public interface ProfileFileService {

    ImageUrlResponse uploadProfileImage(MultipartFile multipartFile, Long memberId);
}
