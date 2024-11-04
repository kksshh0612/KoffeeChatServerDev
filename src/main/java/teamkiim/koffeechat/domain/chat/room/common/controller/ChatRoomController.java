package teamkiim.koffeechat.domain.chat.room.common.controller;

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
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.service.ChatRoomService;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@Tag(name = "채팅방 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 회원이 현재 속해있는 채팅방 목록 페이징 조회
     *
     * @param page
     * @param size
     * @param chatRoomType
     * @param request
     * @return
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("")
    @ChatRoomApiDocument.FindChatRoomsByTypeApiDoc
    public ResponseEntity<?> findChatRoomsByType(@RequestParam("page") int page, @RequestParam("size") int size,
                                                 @RequestParam(value = "chatRoomType", required = false) ChatRoomType chatRoomType,
                                                 HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(chatRoomService.findChatRoomList(memberId, page, size, chatRoomType));
    }

    /**
     * 채팅 메세지 조회 (커서 기반 페이징)
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @GetMapping("/message/{chatRoomId}")
    public ResponseEntity<?> open(@PathVariable("chatRoomId") String chatRoomId,
                                  @RequestParam("cursor") Long cursor, @RequestParam("size") int size,
                                  HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        return ResponseEntity.ok(chatMessageService.getChatMessages(decryptedChatRoomId, cursor, size, memberId));
    }


    /**
     * 채팅방 종료 (채팅창을 닫을 때 호출하는 API)
     *
     * @param chatRoomId
     * @param request
     * @return
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/close/{chatRoomId}")
    public ResponseEntity<?> closeChatRoom(@PathVariable("chatRoomId") String chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        LocalDateTime closeTime = LocalDateTime.now();

        chatRoomService.close(decryptedChatRoomId, memberId, closeTime);

        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 퇴장 (채팅방에서 완전히 퇴장할 때 호출하는 API)
     *
     * @param chatRoomId
     * @param request
     * @return
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/exit/{chatRoomId}")
    public ResponseEntity<?> exitChatRoom(@PathVariable("chatRoomId") String chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        LocalDateTime exitTime = LocalDateTime.now();

        chatRoomService.exit(decryptedChatRoomId, memberId, exitTime);

        return ResponseEntity.ok().build();
    }

}
