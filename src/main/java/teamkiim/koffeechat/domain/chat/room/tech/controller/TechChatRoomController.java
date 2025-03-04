package teamkiim.koffeechat.domain.chat.room.tech.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.chat.room.tech.controller.dto.CreateTechChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.tech.service.TechChatRoomService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tech-chat-room")
@Tag(name = "기술 채팅방 API")
public class TechChatRoomController {

    private final TechChatRoomService techChatRoomService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 채팅방 최초 생성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/")
    @TechChatRoomApiDocument.create
    public ResponseEntity<?> create(@Valid @RequestBody CreateTechChatRoomRequest createTechChatRoomRequest,
                                    HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(techChatRoomService.create(createTechChatRoomRequest.toServiceRequest(), memberId));
    }

    /**
     * 채팅방 리스트 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/list")
    @TechChatRoomApiDocument.findChatRooms
    public ResponseEntity<?> findChatRooms(
            @RequestParam(value = "parentSkillCategory", required = false) String parentSkillCategory) {

        return ResponseEntity.ok(techChatRoomService.findChatRooms(parentSkillCategory));
    }

    /**
     * 채팅방 참여
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/{chatRoomId}")
    @TechChatRoomApiDocument.enter
    public ResponseEntity<?> enter(@PathVariable("chatRoomId") String chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedTechChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        LocalDateTime currentTime = LocalDateTime.now();

        return ResponseEntity.ok(techChatRoomService.enterChatRoom(decryptedTechChatRoomId, memberId, currentTime));
    }

    /**
     * 회원이 현재 속해있는 채팅방 목록 페이징 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/join-list")
    @TechChatRoomApiDocument.findJoinChatRooms
    public ResponseEntity<?> findJoinChatRooms(@RequestParam("page") int page, @RequestParam("size") int size,
                                               HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(techChatRoomService.findJoinChatRoomList(memberId, page, size));
    }

    /**
     * 채팅방 퇴장 (채팅방에서 완전히 퇴장할 때 호출하는 API)
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/exit/{chatRoomId}")
    @TechChatRoomApiDocument.exit
    public ResponseEntity<?> exit(@PathVariable("chatRoomId") String chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        LocalDateTime exitTime = LocalDateTime.now();

        techChatRoomService.exit(decryptedChatRoomId, chatRoomId, memberId, exitTime);

        return ResponseEntity.ok().build();
    }
}
