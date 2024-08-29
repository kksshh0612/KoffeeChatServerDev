package teamkiim.koffeechat.domain.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamkiim.koffeechat.domain.notification.controller.dto.NotificationApiDocument;
import teamkiim.koffeechat.domain.notification.dto.response.NotificationListResponse;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.util.List;

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
    @NotificationApiDocument.SubscribeApiDoc
    public SseEmitter subscribe(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return notificationService.connectNotification(memberId);
    }

    /**
     * 페이지 로딩 시 읽지 않은 알림 개수 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/unread-notifications")
    @NotificationApiDocument.getUnreadNotificationCountApiDoc
    public ResponseEntity<?> getUnreadNotificationCount(HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        int count = notificationService.getUnreadNotificationCount(memberId);

        return ResponseEntity.ok(count);
    }

    /**
     * 알림 목록 조회
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/")
    @NotificationApiDocument.ShowListApiDoc
    public ResponseEntity<?> showList(@RequestParam("page") int page, @RequestParam("size") int size, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<NotificationListResponse> response = notificationService.getNotificationList(memberId, page, size);

        return ResponseEntity.ok(response);
    }

    /**
     * 알림 확인
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/{notificationId}")
    @NotificationApiDocument.UpdateIsReadApiDoc
    public ResponseEntity<?> updateIsRead(@PathVariable("notificationId") Long notiId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        long count = notificationService.updateIsRead(memberId, notiId);

        return ResponseEntity.ok(count);
    }

    /**
     * 알림 단건 삭제
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/{notificationId}")
    @NotificationApiDocument.DeleteApiDoc
    public ResponseEntity<?> delete(@PathVariable("notificationId") Long notiId, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        long count = notificationService.deleteNotification(memberId, notiId);

        return ResponseEntity.ok(count);
    }
}
