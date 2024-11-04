package teamkiim.koffeechat.domain.file.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.service.ChatFileService;
import teamkiim.koffeechat.domain.file.service.PostFileService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "파일 API")
public class FileController {

    private final PostFileService postFileService;
    private final ChatFileService chatFileService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 게시글 이미지 단건 저장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/post")
    @FileApiDocument.saveImageFileInPost
    public ResponseEntity<?> saveImageFileInPost(@RequestPart(value = "file") MultipartFile multipartFile,
                                                 @RequestPart(value = "postId") String postId) {

        Long decryptedPostId = aesCipherUtil.decrypt(postId);
        return ResponseEntity.ok(postFileService.uploadImageFile(multipartFile, decryptedPostId));
    }

    /**
     * 채팅 이미지 단건 저장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/chat")
    @FileApiDocument.saveImageFileInChat
    public ResponseEntity<?> saveImageFileInChat(@RequestPart(value = "file") MultipartFile multipartFile,
                                                 @RequestPart(value = "chatRoomId") String chatRoomId,
                                                 HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        LocalDateTime sendTime = LocalDateTime.now();

        chatFileService.uploadImageFile(multipartFile, decryptedChatRoomId, memberId, sendTime);

        return ResponseEntity.ok().build();
    }
}
