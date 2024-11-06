package teamkiim.koffeechat.domain.file.service;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public interface ChatFileService {

    void uploadImageFile(MultipartFile multipartFile, Long decryptChatRoomId, String encryptChatRoomId, Long memberId,
                         LocalDateTime sendTime);
}
