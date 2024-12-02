package teamkiim.koffeechat.domain.file.service;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;

public interface ChatFileService {

    ImageUrlResponse uploadImageFile(MultipartFile multipartFile, Long decryptChatRoomId, String encryptChatRoomId,
                                     Long memberId, LocalDateTime sendTime);
}
