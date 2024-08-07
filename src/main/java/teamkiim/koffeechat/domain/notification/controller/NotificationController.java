package teamkiim.koffeechat.domain.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * SSE 연결 설정
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/subscribe")
    public SseEmitter subscribe(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return notificationService.connectNotification(memberId);
    }

    /**
     * 페이지 로딩 시 읽지 않은 알림 개수 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadNotificationCount(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(notificationService.getUnreadNotificationCount(memberId));
    }

    /**
     * 알림 목록 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/list")
    public ResponseEntity<?> showList(@RequestParam("page") int page, @RequestParam("size") int size, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(notificationService.list(memberId, page, size));
    }

    /**
     * 알림 확인
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> read(@PathVariable("notificationId") Long notiId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(notificationService.readUpdate(memberId, notiId));
    }

    /**
     * 알림 단건 삭제
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> delete(@PathVariable("notificationId") Long notiId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(notificationService.delete(memberId, notiId));
    }
}
