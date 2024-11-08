package teamkiim.koffeechat.domain.file.service.s3;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.chat.room.common.ChatRoomManager;
import teamkiim.koffeechat.domain.file.domain.ChatFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.file.repository.ChatFileRepository;
import teamkiim.koffeechat.domain.file.service.ChatFileService;
import teamkiim.koffeechat.domain.notification.service.ChatNotificationService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile("prod")
public class S3ChatFileService implements ChatFileService {

    private final ChatFileRepository chatFileRepository;
    private final ChatMessageService chatMessageService;
    private final S3FileStorageControlService s3FileStorageControlService;
    private final ChatNotificationService chatNotificationService;
    private final ChatRoomManager chatRoomManager;

    /**
     * 채팅에서 S3에 이미지 전송
     *
     * @param multipartFile     이미지 파일
     * @param decryptChatRoomId 이미지 전송한 채팅방 복호화된 PK
     * @param encryptChatRoomId 이미지 전송한 채팅방 암호화된 PK
     * @param memberId          이미지 전송한 회원 PK
     * @param sendTime          이미지를 전송한 시간
     */
    @Transactional
    public void uploadImageFile(MultipartFile multipartFile, Long decryptChatRoomId, String encryptChatRoomId,
                                Long memberId, LocalDateTime sendTime) {

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        ImageUrlResponse imageUrlResponse = s3FileStorageControlService.uploadFile(fileName, multipartFile);

        ChatFile chatFile = ChatFile.builder()
                .url(imageUrlResponse.getUrl())
                .build();

        ChatFile saveFile = chatFileRepository.save(chatFile);

        ChatMessageServiceRequest chatMessageServiceRequest = ChatMessageServiceRequest.builder()
                .messageId(null)
                .messageType(MessageType.IMAGE)
                .content(saveFile.getUrl())
                .createdTime(sendTime)
                .build();

        List<Long> chatRoomMemberIds = chatRoomManager.getMemberIds(decryptChatRoomId);

        chatMessageService.saveImageMessage(chatMessageServiceRequest, decryptChatRoomId, encryptChatRoomId, memberId);

        chatNotificationService.createChatNotification(chatMessageServiceRequest,
                decryptChatRoomId, memberId, chatRoomMemberIds);
    }
}
