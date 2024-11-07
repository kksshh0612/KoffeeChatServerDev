package teamkiim.koffeechat.domain.chat.room.common.controller;

import static teamkiim.koffeechat.domain.member.domain.MemberRole.ADMIN;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.COMPANY_EMPLOYEE;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.COMPANY_EMPLOYEE_TEMP;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.FREELANCER;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.MANAGER;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.STUDENT;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.chat.room.common.service.ChatRoomService;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-room")
@Tag(name = "채팅방 공통 API")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 채팅 메세지 조회 (커서 기반 페이징)
     */
    @Auth(role = {COMPANY_EMPLOYEE, FREELANCER, STUDENT, COMPANY_EMPLOYEE_TEMP, MANAGER, ADMIN})
    @GetMapping("/message/{chatRoomId}")
    @ChatRoomApiDocument.open
    public ResponseEntity<?> open(@PathVariable("chatRoomId") String chatRoomId,
                                  @RequestParam(value = "cursor", required = false) Long cursor,
                                  @RequestParam("size") int size,
                                  HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        return ResponseEntity.ok(chatMessageService.getChatMessages(decryptedChatRoomId, cursor, size, memberId));
    }


    /**
     * 채팅방 닫기
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/close/{chatRoomId}")
    public ResponseEntity<?> close(@PathVariable("chatRoomId") String chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        LocalDateTime closeTime = LocalDateTime.now();

        chatRoomService.close(decryptedChatRoomId, memberId, closeTime);

        return ResponseEntity.ok().build();
    }

}
