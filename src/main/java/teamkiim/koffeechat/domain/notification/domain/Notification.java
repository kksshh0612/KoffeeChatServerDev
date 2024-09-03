package teamkiim.koffeechat.domain.notification.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.dto.request.CreateNotificationRequest;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String eventId;            //알림 이벤트 id

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;            //알림을 받는 회원

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;              // 알림 내용에 포함될 회원

    private String title;               // 알림 제목

    private String content;             // 알림 내용

    private String url;                 // 알림 클릭 시 연결할 주소

    private boolean isRead;             // 읽은 메시지 표시

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private LocalDateTime createdTime;   //알림 생성 날짜

    /**
     * 생성자
     */
    @Builder
    public Notification(String eventId, Member receiver, Member sender, String title, String content,
                        String url, boolean isRead, NotificationType notificationType, LocalDateTime createdTime) {

        this.eventId = eventId;
        this.receiver = receiver;
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
        this.notificationType = notificationType;
        this.createdTime = createdTime;
    }
//    public Notification(CreateNotificationRequest createNotificationRequest, Member receiver, String eventId) {
//        this.eventId = eventId;
//        this.receiver = receiver;
//        this.member = createNotificationRequest.getMember();
//        this.title = createNotificationRequest.getTitle();
//        this.content = createNotificationRequest.getContent();
//        this.url = createNotificationRequest.getUrl();
//        this.isRead = createNotificationRequest.isRead();
//        this.notificationType = createNotificationRequest.getNotificationType();
//        this.createdTime = LocalDateTime.now();
//    }

    //== 비즈니스 로직 ==//

    /**
     * 알림 읽음 처리
     */
    public void read() {
        this.isRead = true;
    }
}
