package teamkiim.koffeechat.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.memberfollow.repository.MemberFollowRepository;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.notification.dto.request.CreateNotificationRequest;
import teamkiim.koffeechat.domain.notification.dto.response.NotificationListItemResponse;
import teamkiim.koffeechat.domain.notification.dto.response.NotificationResponse;
import teamkiim.koffeechat.domain.notification.repository.EmitterRepository;
import teamkiim.koffeechat.domain.notification.repository.NotificationRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final static Long DEFAULT_TIMEOUT = 3600000L;  //1시간

    private final MemberRepository memberRepository;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final MemberFollowRepository memberFollowRepository;

    /**
     * 로그인한 회원과 서버의 SSE 연결 생성
     *
     * @param memberId 연결할 회원 Id
     * @return SseEmitter
     */
    public SseEmitter connectNotification(Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);   //연결 유지를 위한 적절한 타임 아웃 설정 필요
        String emitterId = memberId + "_" + System.currentTimeMillis();  //memberId + 연결된 시간으로 id 생성 -> memberId를 통해 알림 전송
        emitterRepository.save(emitterId, sseEmitter);

        sseEmitter.onTimeout(() -> {
            log.info("disconnected by timeout server sent event: id={}", emitterId);
            emitterRepository.deleteById(emitterId);
        });
        sseEmitter.onError(e -> {
            log.info("sse error occurred: id={}, message={}", emitterId, e.getMessage());  //오류 로그 기록
            emitterRepository.deleteById(emitterId);
        });
        sseEmitter.onCompletion(() -> {
            log.info("SSE completed: id={}", emitterId);
            emitterRepository.deleteById(emitterId);
        });

        //연결 후 첫 메시지 전송 : 503 에러 방지
        String eventId = memberId + "_" + System.currentTimeMillis();
        sendNotification(emitterId, sseEmitter, eventId, "connected");

        return sseEmitter;
    }

    /**
     * 글 알림 생성
     */
    public void createPostNotification(Member writer, Post post, PostCategory postType) {

        List<Member> followerList = memberFollowRepository.findFollowerListByFollowing(writer);

        String notiTitle = writer.getNickname();

        String postTypeString = postType.equals(PostCategory.DEV) ? "dev-post" : "community-post";
        Long notiUrl = post.getId();

        followerList.forEach(follower ->
                createNotification(CreateNotificationRequest.of(writer, notiTitle, post.getTitle(), notiUrl, NotificationType.POST), follower)
        );
    }

    /**
     * 댓글 알림 생성
     */
    public void createCommentNotification(Post post, PostCategory postType, Member sender, Long commentId, String content) {

        Member receiver = post.getMember();
        String postTypeString = postType.equals(PostCategory.DEV) ? "dev-post" : "community-post";
        Long notiUrl = post.getId();

        createNotification(CreateNotificationRequest.of(sender, post.getTitle(), content, notiUrl, NotificationType.COMMENT), receiver);
    }

    /**
     * 팔로우 알림 생성
     */
    public void createFollowNotification(Member follower, Member following) {

        Member receiver = following;
        String notiTitle = follower.getNickname();
        Long notiUrl = follower.getId();  //팔로우 건 회원의 프로필 링크

        createNotification(CreateNotificationRequest.of(follower, notiTitle, null, notiUrl, NotificationType.FOLLOW), receiver);
    }

    /**
     * 알림 생성
     */
    private void createNotification(CreateNotificationRequest createNotificationRequest, Member receiver) {

        String eventId = receiver.getId() + "_" + System.currentTimeMillis();   //eventId 생성
        Notification savedNotification = notificationRepository.save(createNotificationRequest.toEntity(eventId, receiver));

        receiver.addUnreadNotifications();  //읽지 않은 알림 개수 +1

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterByReceiverId(String.valueOf(receiver.getId()));  //알림 받는 사람이 연결되어있는 모든 emitter에 이벤트 발송

        emitters.forEach(
                (id, emitter) -> {
                    sendNotification(id, emitter, eventId, NotificationResponse.of(savedNotification, receiver.getUnreadNotifications()));
                }
        );
    }

    /**
     * 알림 발송
     *
     * @param response 알림 내용
     */
    private void sendNotification(String emitterId, SseEmitter emitter, String eventId, Object response) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(response));
        } catch (IOException e) {
            log.error("Failed to send notification, eventId={}, emitterId={}, error={}", eventId, emitterId, e.getMessage());
            emitter.completeWithError(e);
            emitterRepository.deleteById(emitterId);
        }
    }

    /**
     * 페이지 로딩 시 읽지 않은 알림 개수 조회
     *
     * @Return 읽지 않은 알림 개수
     */
    @Transactional
    public int getUnreadNotificationCount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        int unreadNotificationCount = notificationRepository.countByReceiverAndIsReadFalse(member);

        member.updateUnreadNotificationCount(unreadNotificationCount);

        return unreadNotificationCount;
    }

    /**
     * 알림 목록 조회
     *
     * @param page     페이지 번호 ( ex) 0, 1,,,, )
     * @param size     페이지 당 조회할 데이터 수
     * @param memberId 로그인 한 회원
     * @return List<NotificationListResponse>
     */
    public List<NotificationListItemResponse> getNotificationList(Long memberId, int page, int size) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Notification> notificationList = notificationRepository.findAllByReceiverId(memberId, pageRequest).getContent();
        return notificationList.stream().map(NotificationListItemResponse::of).toList();
    }

    /**
     * 알림 확인
     *
     * @param memberId member pk
     * @param notiId   notification pk
     * @Return 읽지 않은 알림 개수
     */
    @Transactional
    public long updateNotificationIsRead(Long memberId, Long notiId) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Notification notification = notificationRepository.findByIdAndReceiverId(notiId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.isRead()) {
            notification.read();                   // 알림 읽음 처리
            receiver.removeUnreadNotifications();  // 읽지 않은 알림 개수 -1
        }

        return receiver.getUnreadNotifications();
    }

    /**
     * 알림 단건 삭제
     *
     * @param memberId member pk
     * @param notiId   notification pk
     * @Return 읽지 않은 알림 개수
     */
    @Transactional
    public long deleteNotification(Long memberId, Long notiId) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Notification notification = notificationRepository.findById(notiId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.isRead()) {
            receiver.removeUnreadNotifications();  // 읽지 않은 알림 개수 -1
        }
        notificationRepository.delete(notification);

        return receiver.getUnreadNotifications();
    }

    /**
     * 알림 전체 삭제
     *
     * @param memberId member pk
     * @Return 읽지 않은 알림 개수 : 0
     */
    @Transactional
    public void deleteAllNotifications(Long memberId) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        notificationRepository.deleteAllByReceiver(receiver);     //알림 전체 삭제
        receiver.updateUnreadNotificationCount(0); //읽지 않은 알림 개수 업데이트
    }

    /**
     * 알림 전체 읽기
     *
     * @param memberId member pk
     * @Return 읽지 않은 알림 개수 : 0
     */
    @Transactional
    public void updateAllNotificationsIsRead(Long memberId) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        notificationRepository.updateAllIsRead(receiver);     //알림 전체 읽음
        receiver.updateUnreadNotificationCount(0); //읽지 않은 알림 개수 업데이트
    }

}
