package teamkiim.koffeechat.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.notification.domain.Notification;

/**
 * 알림 메시지 저장 repository
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
