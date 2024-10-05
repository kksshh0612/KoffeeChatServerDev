package teamkiim.koffeechat.domain.chat.room.tech.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.chat.room.tech.controller.dto.CreateTechChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.tech.controller.dto.EnterTechChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.tech.controller.dto.ExitTechChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.tech.service.TechChatRoomService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tech-chat-room")
public class TechChatRoomController {

    private final TechChatRoomService techChatRoomService;

    /**
     * 채팅방 최초 생성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/")
    public ResponseEntity<?> create(@Valid @RequestBody CreateTechChatRoomRequest createTechChatRoomRequest,
                                    HttpServletRequest request) throws Exception {

        String memberId = String.valueOf(request.getAttribute("authenticatedMemberPK"));

        techChatRoomService.createChatRoom(createTechChatRoomRequest.toServiceRequest(), memberId);

        return ResponseEntity.ok("채팅방 생성 완료");
    }

    /**
     * 채팅방 입장
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/enter")
    public ResponseEntity<?> create(@Valid @RequestBody EnterTechChatRoomRequest enterTechChatRoomRequest,
                                    HttpServletRequest request) throws Exception {

        String memberId = String.valueOf(request.getAttribute("authenticatedMemberPK"));

        LocalDateTime currTime = LocalDateTime.now();

        techChatRoomService.enterChatRoom(enterTechChatRoomRequest.toServiceRequest(currTime), memberId);

        return ResponseEntity.ok("채팅방 입장 완료");
    }

    /**
     * 채팅방 퇴장
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/")
    public ResponseEntity<?> exit(@Valid @RequestBody ExitTechChatRoomRequest exitTechChatRoomRequest,
                                  HttpServletRequest request) throws Exception {

        String memberId = String.valueOf(request.getAttribute("authenticatedMemberPK"));

        LocalDateTime currTime = LocalDateTime.now();

        techChatRoomService.exitChatRoom(exitTechChatRoomRequest.toServiceRequest(currTime), memberId);

        return ResponseEntity.ok("채팅방 퇴장 완료");
    }

}
