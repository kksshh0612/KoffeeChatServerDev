package teamkiim.koffeechat.domain.chat.room.tech.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.chat.room.tech.controller.dto.CreateTechChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.tech.controller.dto.EnterTechChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.tech.controller.dto.ExitTechChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.tech.service.TechChatRoomService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tech-chat-room")
public class TechChatRoomController {

    private final TechChatRoomService techChatRoomService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 채팅방 최초 생성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/")
    public ResponseEntity<?> create(@Valid @RequestBody CreateTechChatRoomRequest createTechChatRoomRequest,
                                    HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        techChatRoomService.createChatRoom(createTechChatRoomRequest.toServiceRequest(), memberId);

        return ResponseEntity.ok("채팅방 생성 완료");
    }

    /**
     * 채팅방 입장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/enter")
    public ResponseEntity<?> create(@Valid @RequestBody EnterTechChatRoomRequest enterTechChatRoomRequest,
                                    HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedTechChatRoomId = aesCipherUtil.decrypt(enterTechChatRoomRequest.getChatRoomId());

        LocalDateTime currTime = LocalDateTime.now();

        techChatRoomService.enterChatRoom(enterTechChatRoomRequest.toServiceRequest(currTime, decryptedTechChatRoomId),
                memberId);

        return ResponseEntity.ok("채팅방 입장 완료");
    }

    /**
     * 채팅방 퇴장
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/")
    public ResponseEntity<?> exit(@Valid @RequestBody ExitTechChatRoomRequest exitTechChatRoomRequest,
                                  HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(exitTechChatRoomRequest.getChatRoomId());

        LocalDateTime currTime = LocalDateTime.now();

        techChatRoomService.exitChatRoom(exitTechChatRoomRequest.toServiceRequest(decryptedChatRoomId, currTime),
                memberId);

        return ResponseEntity.ok("채팅방 퇴장 완료");
    }

}
