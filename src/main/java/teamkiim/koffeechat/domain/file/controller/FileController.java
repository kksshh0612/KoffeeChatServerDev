package teamkiim.koffeechat.domain.file.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.dto.response.ImagePathResponse;
import teamkiim.koffeechat.domain.file.service.ChatFileService;
import teamkiim.koffeechat.domain.file.service.PostFileService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "파일 API")
public class FileController {

    private final PostFileService postFileService;
    private final ChatFileService chatFileService;

    /**
     * 게시글 이미지 단건 저장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/post")
    @FileApiDocument.SaveImageFile
    public ResponseEntity<?> saveImageFileInPost(@RequestPart(value = "file") MultipartFile multipartFile,
                                           @RequestPart(value = "postId") Long postId) {

        ImagePathResponse response = postFileService.saveImageFile(multipartFile, postId);

        return ResponseEntity.ok(response);
    }

    /**
     * 채팅 이미지 단건 저장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/chat")
    @FileApiDocument.SaveImageFile
    public ResponseEntity<?> saveImageFileInChat(@RequestPart(value = "file") MultipartFile multipartFile,
                                                 @RequestPart(value = "postId") Long chatRoomId,
                                                 HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        LocalDateTime sendTime = LocalDateTime.now();

        chatFileService.saveImageFile(multipartFile, chatRoomId, memberId, sendTime);

        return ResponseEntity.ok().build();
    }

}
