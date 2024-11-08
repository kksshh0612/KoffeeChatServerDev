package teamkiim.koffeechat.domain.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamkiim.koffeechat.domain.notification.controller.dto.NotificationApiDocument;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.notification.dto.response.NotificationListItemResponse;
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
    @NotificationApiDocument.SubscribeApiDoc
    public SseEmitter subscribe(@RequestParam(value = "lastEventId", required = false) String lastEventId,
                                HttpServletRequest request) {

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
     * 알림 목록 조회 (전체 | 게시글 | 댓글 | 팔로우)
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("")
    @NotificationApiDocument.ShowNotificationListApiDoc
    public ResponseEntity<?> showNotificationList(@RequestParam("notificationType") NotificationType notiType,
                                                  @RequestParam("page") int page,
                                                  @RequestParam("size") int size, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<NotificationListItemResponse> response = notificationService.getNotificationList(notiType, memberId, page,
                size);

        return ResponseEntity.ok(response);
    }

    /**
     * 알림 확인
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("/{notificationId}")
    @NotificationApiDocument.UpdateNotificationIsReadApiDoc
    public ResponseEntity<?> updateNotificationIsRead(@PathVariable("notificationId") String notiId,
                                                      HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        long count = notificationService.updateNotificationIsRead(memberId, notiId);

        return ResponseEntity.ok(count);
    }

    /**
     * 알림 단건 삭제
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/{notificationId}")
    @NotificationApiDocument.DeleteNotificationApiDoc
    public ResponseEntity<?> deleteNotification(@PathVariable("notificationId") String notiId,
                                                HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        long count = notificationService.deleteNotification(memberId, notiId);

        return ResponseEntity.ok(count);
    }

    /**
     * 알림 전체 삭제
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("")
    @NotificationApiDocument.DeleteAllNotificationsApiDoc
    public ResponseEntity<?> deleteAllNotifications(HttpServletRequest request) {
        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        notificationService.deleteAllNotifications(memberId);

        return ResponseEntity.ok("전체 알림 삭제 완료");
    }

    /**
     * 알림 전체 읽음
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("")
    @NotificationApiDocument.ReadAllNotificationsApiDoc
    public ResponseEntity<?> readAllNotifications(HttpServletRequest request) {
        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        notificationService.updateAllNotificationsIsRead(memberId);

        return ResponseEntity.ok("전체 알림 읽음 완료");
    }
}
