package teamkiim.koffeechat.domain.chat.room.direct.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.chat.message.controller.dto.CreateDirectChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.direct.service.DirectChatRoomService;
import teamkiim.koffeechat.global.Auth;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/direct-chat-room")
@Tag(name = "채팅방 관리 API")
public class DirectChatRoomController {

    private final DirectChatRoomService directChatRoomService;

    /**
     * 일대일 채팅방 생성
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/")
    public ResponseEntity<?> create(@Valid @RequestBody CreateDirectChatRoomRequest createDirectChatRoomRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(directChatRoomService.createChatRoom(createDirectChatRoomRequest.getMemberId(), memberId));
    }

    /**
     * 채팅방 입장
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<?> open(@PathVariable("chatRoomId") Long chatRoomId, @RequestParam("size") int size,
                                  HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(directChatRoomService.openChatRoom(chatRoomId, size, memberId));
    }

    /**
     * 채팅방 닫기
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @GetMapping("/close/{chatRoomId}")
    public ResponseEntity<?> close(@PathVariable Long chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        LocalDateTime now = LocalDateTime.now();

        directChatRoomService.closeChatRoom(chatRoomId, memberId, now);

        return ResponseEntity.ok("");
    }
}
