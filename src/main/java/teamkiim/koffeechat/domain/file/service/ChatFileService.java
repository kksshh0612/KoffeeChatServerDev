package teamkiim.koffeechat.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface ChatFileService {

    void uploadImageFile(MultipartFile multipartFile, Long chatRoomId, Long memberId, LocalDateTime sendTime);
}
