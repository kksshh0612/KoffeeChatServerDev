package teamkiim.koffeechat.domain.chat.room.direct.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.chat.message.controller.dto.CreateDirectChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.direct.service.DirectChatRoomService;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/direct-chat-room")
@Tag(name = "채팅방 관리 API")
public class DirectChatRoomController {

    private final DirectChatRoomService directChatRoomService;

    private final AESCipherUtil aesCipherUtil;


    /**
     * 일대일 채팅방 생성
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/")
    public ResponseEntity<?> create(@Valid @RequestBody CreateDirectChatRoomRequest createDirectChatRoomRequest,
                                    HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long memberId2 = aesCipherUtil.decrypt(createDirectChatRoomRequest.getMemberId());

        return ResponseEntity.ok(
                directChatRoomService.createChatRoom(memberId2, memberId));
    }

    /**
     * 채팅방 입장
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<?> open(@PathVariable("chatRoomId") String chatRoomId,
                                  @RequestParam("page") int page, @RequestParam("size") int size,
                                  HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        return ResponseEntity.ok(directChatRoomService.openChatRoom(decryptedChatRoomId, size, memberId));
    }

}
