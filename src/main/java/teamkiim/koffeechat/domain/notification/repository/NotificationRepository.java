package teamkiim.koffeechat.domain.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.notification.domain.Notification;

import java.util.Optional;


/**
 * 알림 메시지 저장 repository
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByReceiverId(Long id, Pageable pageable);

    Optional<Notification> findByIdAndReceiverId(Long notiId, Long memberId);
}
