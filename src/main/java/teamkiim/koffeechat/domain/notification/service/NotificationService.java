package teamkiim.koffeechat.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamkiim.koffeechat.domain.aescipher.AESCipher;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.memberfollow.repository.MemberFollowRepository;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.notification.domain.SseEmitterWrapper;
import teamkiim.koffeechat.domain.notification.dto.request.CreateNotificationRequest;
import teamkiim.koffeechat.domain.notification.dto.response.*;
import teamkiim.koffeechat.domain.notification.repository.EmitterRepository;
import teamkiim.koffeechat.domain.notification.repository.NotificationRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
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
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final NotificationRepository notificationRepository;
    private final MemberFollowRepository memberFollowRepository;

    private final AESCipher aesCipher;

    /**
     * 로그인한 회원과 서버의 SSE 연결 생성
     *
     * @param memberId 연결할 회원 Id
     * @return SseEmitter
     */
    public SseEmitter connectNotification(Long memberId) throws Exception {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);   //연결 유지를 위한 적절한 타임 아웃 설정 필요
        String emitterId = aesCipher.encrypt(memberId) + "_" + System.currentTimeMillis();  //암호화된 memberId + 연결된 시간으로 id 생성 -> memberId를 통해 알림 전송

        SseEmitterWrapper emitterWrapper = emitterRepository.save(emitterId, new SseEmitterWrapper(sseEmitter));

        //채팅방 목록 emitter에 추가
        List<Long> memberChatRoomIdList = memberChatRoomRepository.findAllByMember(member)
                .stream().map(memberChatRoom -> memberChatRoom.getChatRoom().getId()).toList();

        if (memberChatRoomIdList != null && !memberChatRoomIdList.isEmpty()) {
            emitterWrapper.updateChatRoomNotificationStatus(memberChatRoomIdList);
        }

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
    public void createPostNotification(Member writer, Post post) {

        List<Member> followerList = memberFollowRepository.findFollowerListByFollowing(writer);  // 글쓴이의 팔로워들

        followerList.forEach(follower -> {
            try {
                createNotification(CreateNotificationRequest.ofForPost(NotificationType.POST, writer, post), follower);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
            }
        });
    }

    /**
     * 댓글 알림 생성
     */
    public void createCommentNotification(Post post, Member sender, Comment comment) throws Exception {
        createNotification(CreateNotificationRequest.ofForComment(NotificationType.COMMENT, sender, post, comment), post.getMember());
    }

    /**
     * 팔로우 알림 생성
     */
    public void createFollowNotification(Member follower, Member following) throws Exception {
        createNotification(CreateNotificationRequest.ofForFollow(NotificationType.FOLLOW, follower), following);
    }

    /**
     * 현직자 인증 알림 생성
     */
    public void createCorpNotification(List<Member> memberList, Corp corp, Verified verified) {
        memberList.forEach(member -> {
            try {
                createNotification(CreateNotificationRequest.ofForCorp(NotificationType.CORP, corp, verified), member);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
            }
        });
    }

    /**
     * 알림 생성
     */
    private void createNotification(CreateNotificationRequest createNotificationRequest, Member receiver) throws Exception {

        String receiverId = aesCipher.encrypt(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();   //eventId 생성
        Notification savedNotification = notificationRepository.save(createNotificationRequest.toEntity(eventId, receiver));

        receiver.addUnreadNotifications();  //읽지 않은 알림 개수 +1

        Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(receiverId);  //알림 받는 사람이 연결되어있는 모든 emitter에 이벤트 발송

        emitters.forEach((id, emitter) -> {
            try {  // 알림 종류에 따른 response 전달
                switch (savedNotification.getNotificationType()) {
                    case POST -> sendNotification(id, emitter.getSseEmitter(), eventId,
                            PostNotificationResponse.of(aesCipher.encrypt(savedNotification.getReceiver().getId()), aesCipher.encrypt(savedNotification.getSender().getId()),
                                    aesCipher.encrypt(savedNotification.getUrlPK()), savedNotification, receiver.getUnreadNotifications()));
                    case COMMENT -> sendNotification(id, emitter.getSseEmitter(), eventId,
                            CommentNotificationResponse.of(aesCipher.encrypt(savedNotification.getReceiver().getId()), aesCipher.encrypt(savedNotification.getSender().getId()),
                                    aesCipher.encrypt(savedNotification.getUrlPK()), aesCipher.encrypt(savedNotification.getCommentId()), savedNotification, receiver.getUnreadNotifications()));
                    case FOLLOW -> sendNotification(id, emitter.getSseEmitter(), eventId, FollowNotificationResponse.of(aesCipher.encrypt(savedNotification.getReceiver().getId()),
                            aesCipher.encrypt(savedNotification.getSender().getId()), savedNotification, receiver.getUnreadNotifications()));
                    case CORP -> sendNotification(id, emitter.getSseEmitter(), eventId,
                            CorpNotificationResponse.of(aesCipher.encrypt(savedNotification.getReceiver().getId()), savedNotification, receiver.getUnreadNotifications()));
                }
            } catch (Exception e) {
                throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
            }
        });
    }

    /**
     * 알림 발송
     *
     * @param response 알림 내용
     */
    private void sendNotification(String emitterId, SseEmitter emitter, String eventId, Object response) {
        try {
            emitter.send(SseEmitter.event().id(eventId).data(response));
        } catch (IOException e) {
            log.error("Failed to send notification, eventId={}, emitterId={}, error={}", eventId, emitterId, e.getMessage());
            emitter.completeWithError(e);
            emitterRepository.deleteById(emitterId);
        }
    }

    /**
     * 페이지 로딩 시 읽지 않은 알림 개수 조회
     *
     * @return 읽지 않은 알림 개수
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
     * @param notificationType 조회할 알림 종류 (전체 | 게시글 | 댓글 | 팔로우)
     * @param memberId         로그인 한 회원
     * @param page             페이지 번호 ( ex) 0, 1,,,, )
     * @param size             페이지 당 조회할 데이터 수
     * @return List<NotificationListResponse>
     */
    public List<NotificationListItemResponse> getNotificationList(NotificationType notificationType, Long memberId, int page, int size) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));  // 최신순 정렬

        List<Notification> notificationList = (notificationType.equals(NotificationType.ALL)) ?
                notificationRepository.findAllByReceiver(receiver, pageRequest).getContent()
                : notificationRepository.findAllByReceiverAndNotificationType(receiver, notificationType, pageRequest).getContent();

        return notificationList.stream().map(notification -> {
            try {
                switch (notification.getNotificationType()) {
                    case POST -> {
                        return NotificationListItemResponse.of(aesCipher.encrypt(notification.getId()), notification, aesCipher.encrypt(notification.getSender().getId()), aesCipher.encrypt(notification.getUrlPK()), null);
                    }
                    case COMMENT -> {
                        return NotificationListItemResponse.of(aesCipher.encrypt(notification.getId()), notification, aesCipher.encrypt(notification.getSender().getId()), aesCipher.encrypt(notification.getUrlPK()), aesCipher.encrypt(notification.getCommentId()));
                    }
                    case FOLLOW -> {
                        return NotificationListItemResponse.of(aesCipher.encrypt(notification.getId()), notification, aesCipher.encrypt(notification.getSender().getId()), null, null);
                    }
                    case CORP -> {
                        return NotificationListItemResponse.of(aesCipher.encrypt(notification.getId()), notification, null, notification.getUrlPK().toString(), null);
                    }
                    default -> {
                        throw new CustomException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND);
                    }
                }
            } catch (Exception e) {
                throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
            }
        }).toList();
    }

    /**
     * 알림 확인
     *
     * @param memberId member pk
     * @param notiId   notification pk
     * @return 읽지 않은 알림 개수
     */
    @Transactional
    public long updateNotificationIsRead(Long memberId, String notiId) throws Exception {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findByIdAndReceiver(aesCipher.decrypt(notiId), receiver)
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
     * @return 읽지 않은 알림 개수
     */
    @Transactional
    public long deleteNotification(Long memberId, String notiId) throws Exception {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findById(aesCipher.decrypt(notiId))
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
     */
    @Transactional
    public void updateAllNotificationsIsRead(Long memberId) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        notificationRepository.updateAllIsRead(receiver);     //알림 전체 읽음
        receiver.updateUnreadNotificationCount(0); //읽지 않은 알림 개수 업데이트
    }

}
