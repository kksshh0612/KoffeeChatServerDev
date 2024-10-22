package teamkiim.koffeechat.domain.file.service.local;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.file.domain.ChatFile;
import teamkiim.koffeechat.domain.file.repository.ChatFileRepository;
import teamkiim.koffeechat.domain.file.service.ChatFileService;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile("local")
public class LocalChatFileService implements ChatFileService {

    private final ChatFileRepository chatFileRepository;
    private final ChatMessageService chatMessageService;
    private final LocalFileStorageControlService localFileStorageControlService;

    @Value("${file-path}")
    private String baseFilePath;

    /**
     * 채팅에서 Local에 이미지 전송
     *
     * @param multipartFile 이미지 파일
     * @param chatRoomId    이미지를 전송한 채팅방 PK
     * @param memberId      이미지를 전송한 회원 PK
     * @param sendTime      이미지를 전송한 시간
     */
    @Transactional
    public void uploadImageFile(MultipartFile multipartFile, Long chatRoomId, Long memberId, LocalDateTime sendTime) {

        String saveFileUrl = Paths.get(baseFilePath, "CHAT", UUID.randomUUID() + "_" + multipartFile.getOriginalFilename()).toString();

        ChatFile chatFile = ChatFile.builder()
                .url(saveFileUrl)
                .build();

        localFileStorageControlService.uploadFile(saveFileUrl, multipartFile);

        ChatFile saveFile = chatFileRepository.save(chatFile);

        ChatMessageServiceRequest chatMessageServiceRequest = ChatMessageServiceRequest.builder()
                .messageType(MessageType.IMAGE)
                .content(saveFileUrl)
                .createdTime(sendTime)
                .build();

        chatMessageService.saveImageMessage(chatMessageServiceRequest, chatRoomId, memberId);
    }
}
