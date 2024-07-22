package teamkiim.koffeechat.domain.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.chat.service.ChatRoomService;
import teamkiim.koffeechat.global.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-room")
@Tag(name = "채팅방 관리 API")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/create/{postId}")
    public ResponseEntity<?> createChatRoom(@PathVariable("postId") Long postId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return chatRoomService.createChatRoom(postId, memberId);
    }
}
