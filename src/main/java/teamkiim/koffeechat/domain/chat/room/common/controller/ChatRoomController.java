package teamkiim.koffeechat.domain.chat.room.common.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
     * 회원이 속한
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

    @AuthenticatedMemberPrincipal
    @GetMapping("/close/{chatRoomId}")
    public ResponseEntity<?> closeChatRoom(@PathVariable("chatRoomId") Long chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime closeTime = LocalDateTime.now();

        chatRoomService.close(chatRoomId, memberId, closeTime);

        return ResponseEntity.ok().build();
    }

}
