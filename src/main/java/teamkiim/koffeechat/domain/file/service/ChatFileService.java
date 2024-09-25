package teamkiim.koffeechat.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.file.domain.ChatFile;
import teamkiim.koffeechat.domain.file.domain.PostFile;
import teamkiim.koffeechat.domain.file.dto.response.ImagePathResponse;
import teamkiim.koffeechat.domain.file.repository.ChatFileRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.io.File;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatFileService {

    private final ChatFileRepository chatFileRepository;
    private final ChatMessageService chatMessageService;
    private final FileStorageControlService fileStorageControlService;

    /**
     * 채팅에서 이미지 전송
     * @param multipartFile
     * @param chatRoomId
     * @param memberId
     * @param sendTime
     */
    @Transactional
    public void saveImageFile(MultipartFile multipartFile, Long chatRoomId, Long memberId, LocalDateTime sendTime){

        ChatFile chatFile = ChatFile.builder()
                .chatRoomId(chatRoomId)
                .build();

        fileStorageControlService.saveFile(chatFile, multipartFile);

        ChatFile saveFile = chatFileRepository.save(chatFile);

        String imagePath = saveFile.getPath() + File.separator + saveFile.getName();

        ChatMessageServiceRequest chatMessageServiceRequest = ChatMessageServiceRequest.builder()
                .messageType(MessageType.IMAGE)
                .content(imagePath)
                .createdTime(sendTime)
                .build();

        chatMessageService.saveImageMessage(chatMessageServiceRequest, chatRoomId, memberId);
        chatMessageService.send(chatMessageServiceRequest, chatRoomId, memberId);
    }

}
