package teamkiim.koffeechat.domain.chat.room.direct.controller;

import static teamkiim.koffeechat.domain.member.domain.MemberRole.ADMIN;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.COMPANY_EMPLOYEE;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.COMPANY_EMPLOYEE_TEMP;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.FREELANCER;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.MANAGER;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.STUDENT;

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
import teamkiim.koffeechat.domain.chat.message.controller.dto.CreateDirectChatRoomRequest;
import teamkiim.koffeechat.domain.chat.room.direct.service.DirectChatRoomService;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/direct-chat-room")
@Tag(name = "일대일 채팅방 API")
public class DirectChatRoomController {

    private final DirectChatRoomService directChatRoomService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 일대일 채팅방 생성
     */
    @Auth(role = {COMPANY_EMPLOYEE, FREELANCER, STUDENT, COMPANY_EMPLOYEE_TEMP, MANAGER, ADMIN})
    @PostMapping("/")
    @DirectChatRoomApiDocument.create
    public ResponseEntity<?> create(@Valid @RequestBody CreateDirectChatRoomRequest createDirectChatRoomRequest,
                                    HttpServletRequest request) {

        Long requestMemberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long targetMemberId = aesCipherUtil.decrypt(createDirectChatRoomRequest.getMemberId());

        return ResponseEntity.ok(directChatRoomService.create(requestMemberId, targetMemberId));
    }

    /**
     * 회원이 현재 속해있는 일대일 채팅방 목록 페이징 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("")
    @DirectChatRoomApiDocument.findChatRooms
    public ResponseEntity<?> findChatRooms(@RequestParam("page") int page, @RequestParam("size") int size,
                                           HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(directChatRoomService.findChatRoomList(memberId, page, size));
    }

    /**
     * 채팅방 퇴장
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/exit/{chatRoomId}")
    @DirectChatRoomApiDocument.exit
    public ResponseEntity<?> exit(@PathVariable("chatRoomId") String chatRoomId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedChatRoomId = aesCipherUtil.decrypt(chatRoomId);

        LocalDateTime exitTime = LocalDateTime.now();

        directChatRoomService.exit(decryptedChatRoomId, chatRoomId, memberId, exitTime);

        return ResponseEntity.ok("퇴장 완료");
    }
}
