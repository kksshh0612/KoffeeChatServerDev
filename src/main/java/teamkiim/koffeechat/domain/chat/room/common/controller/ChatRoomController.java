package teamkiim.koffeechat.domain.chat.room.common.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.service.ChatRoomService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 회원이 현재 속해있는 채팅방 목록 페이징 조회
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

//    @AuthenticatedMemberPrincipal
//    @GetMapping("/{chatRoomId}")
//    @ChatRoomApiDocument.FindChatRoomByChatRoomIdApiDoc
//    public ResponseEntity<?> findChatRoomByChatRoomId(@PathVariable("chatRoomId") Long chatRoomId, HttpServletRequest request) {
//
//        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
//
////        return ResponseEntity.ok(chatRoomService.findChatRoom(chatRoomId, memberId));
//    }

    /**
     * 채팅방 종료 (채팅창을 닫을 때 호출하는 API)
     * @param chatRoomId
     * @param request
     * @return
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/close/{chatRoomId}")
    public ResponseEntity<?> closeChatRoom(@PathVariable("chatRoomId") Long chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime closeTime = LocalDateTime.now();

        chatRoomService.close(chatRoomId, memberId, closeTime);

        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 퇴장 (채팅방에서 완전히 퇴장할 때 호출하는 API)
     * @param chatRoomId
     * @param request
     * @return
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/exit/{chatRoomId}")
    public ResponseEntity<?> exitChatRoom(@PathVariable("chatRoomId") Long chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime exitTime = LocalDateTime.now();

        chatRoomService.exit(chatRoomId, memberId, exitTime);

        return ResponseEntity.ok().build();
    }

}
