package teamkiim.koffeechat.domain.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

import java.util.Optional;

/**
 * 알림 메시지 저장 repository
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByReceiver(Member receiver, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.receiver = :receiver AND n.notificationType = :notificationType ")
    Page<Notification> findALLByReceiverIdAndNotificationType(@Param("receiver") Member receiver, @Param("notificationType") NotificationType notificationType, PageRequest pageRequest);

    Optional<Notification> findByIdAndReceiverId(Long notiId, Long memberId);

    int countByReceiverAndIsReadFalse(Member member);

    void deleteAllByReceiver(Member receiver);  //알림 전체 삭제

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.isRead = false AND n.receiver = :receiver ")
    void updateAllIsRead(@Param("receiver") Member receiver);      //알림 전체 읽음

}
