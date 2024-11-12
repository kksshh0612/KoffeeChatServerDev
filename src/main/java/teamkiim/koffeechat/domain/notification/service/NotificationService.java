package teamkiim.koffeechat.domain.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamkiim.koffeechat.domain.admin.corp.domain.VerifyStatus;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.memberfollow.repository.MemberFollowRepository;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.notification.dto.request.CreateNotificationRequest;
import teamkiim.koffeechat.domain.notification.dto.response.CommentNotificationResponse;
import teamkiim.koffeechat.domain.notification.dto.response.CorpNotificationResponse;
import teamkiim.koffeechat.domain.notification.dto.response.FollowNotificationResponse;
import teamkiim.koffeechat.domain.notification.dto.response.NotificationListItemResponse;
import teamkiim.koffeechat.domain.notification.dto.response.PostNotificationResponse;
import teamkiim.koffeechat.domain.notification.dto.response.SkillPostNotificationResponse;
import teamkiim.koffeechat.domain.notification.repository.EmitterRepository;
import teamkiim.koffeechat.domain.notification.repository.NotificationRepository;
import teamkiim.koffeechat.domain.notification.service.emitter.SseEmitterWrapper;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

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

    private final AESCipherUtil aesCipherUtil;

    /**
     * 로그인한 회원과 서버의 SSE 연결 생성
     *
     * @param memberId 연결할 회원 Id
     * @return SseEmitter
     */
    public SseEmitter connectNotification(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);   //연결 유지를 위한 적절한 타임 아웃 설정 필요
        String emitterId = aesCipherUtil.encrypt(memberId) + "_"
                + System.currentTimeMillis();  //암호화된 memberId + 연결된 시간으로 id 생성 -> memberId를 통해 알림 전송

        SseEmitterWrapper emitterWrapper = emitterRepository.save(emitterId, new SseEmitterWrapper(sseEmitter));

        //채팅방 목록 emitter에 추가
        List<Long> chatRoomIdList = memberChatRoomRepository.findAllByMember(member).stream()
                .map(memberChatRoom -> memberChatRoom.getChatRoom().getId())
                .collect(Collectors.toList());

        if (!chatRoomIdList.isEmpty()) {
            emitterWrapper.updateChatRoomNotificationStatus(chatRoomIdList);
        }

//        sendPing(sseEmitter);
        sseEmitter.onTimeout(() -> handleTimeout(emitterId));
        sseEmitter.onError(e -> handleError(emitterId, e));
        sseEmitter.onCompletion(() -> handleCompletion(emitterId));

        //연결 후 첫 메시지 전송 : 503 에러 방지
        sendFirstConnectionMessage(memberId, emitterId, sseEmitter);

        return sseEmitter;
    }

    private void sendPing(SseEmitter sseEmitter) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            try {
                sseEmitter.send(SseEmitter.event().data("ping"));  // "ping" 메시지 전송
            } catch (IOException e) {
                log.error("Error sending ping event to keep connection alive", e);
            }
        }, 0, 30, TimeUnit.SECONDS);  // 30초 간격으로 전송
    }

    private void handleTimeout(String emitterId) {
        log.info("disconnected by timeout server sent event: id={}", emitterId);
        emitterRepository.deleteById(emitterId);
    }

    private void handleError(String emitterId, Throwable e) {
        log.info("sse error occurred: id={}, message={}", emitterId, e.getMessage());   //오류 로그 기록
        emitterRepository.deleteById(emitterId);
    }

    private void handleCompletion(String emitterId) {
        log.info("SSE completed: id={}", emitterId);
        emitterRepository.deleteById(emitterId);
    }

    private void sendFirstConnectionMessage(Long memberId, String emitterId, SseEmitter sseEmitter) {
        String eventId = aesCipherUtil.encrypt(memberId) + "_" + System.currentTimeMillis();
        sendNotification(emitterId, sseEmitter, eventId, "connected");
    }

    /**
     * 글 알림 생성
     */
    public void createPostNotification(Member writer, Post post) {

        List<Member> followerList = memberFollowRepository.findFollowerListByFollowing(writer);  // 글쓴이의 팔로워들
        followerList.forEach(follower ->
                createNotification(CreateNotificationRequest.ofForPost(NotificationType.POST, writer, post), follower));
    }

    /**
     * 기술 글 알림 생성
     */
    public void createSkillPostNotification(List<SkillCategory> skillCategoryList, Post post) {

        skillCategoryList.forEach(skill -> {
            ChildSkillCategory childSkill = skill.getChildSkillCategory();
            createChildSkillPostNotification(childSkill, post);
        });
    }

    private void createChildSkillPostNotification(ChildSkillCategory childSkill, Post post) {
        List<Member> skillChatRoomMembers = memberChatRoomRepository.findMembersByChildSkillCategory(childSkill);

        String encryptedPostPk = aesCipherUtil.encrypt(post.getId());
        for (Member member : skillChatRoomMembers) {
            String eventId = aesCipherUtil.encrypt(member.getId()) + "_" + System.currentTimeMillis();
            Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(
                    aesCipherUtil.encrypt(member.getId()));
            String encryptedMemberId = aesCipherUtil.encrypt(member.getId());

            emitters.forEach((id, emitter) -> {
                sendTechNotification(id, emitter.getSseEmitter(), eventId,
                        SkillPostNotificationResponse.of(childSkill, encryptedMemberId, encryptedPostPk, post));
            });
        }
    }

    /**
     * 댓글 알림 생성
     */
    public void createCommentNotification(Post post, Member sender, Comment comment) {
        createNotification(CreateNotificationRequest.ofForComment(NotificationType.COMMENT, sender, post, comment),
                post.getMember());
    }

    /**
     * 팔로우 알림 생성
     */
    public void createFollowNotification(Member follower, Member following) {
        createNotification(CreateNotificationRequest.ofForFollow(NotificationType.FOLLOW, follower), following);
    }

    /**
     * 현직자 인증 알림 생성
     */
    public void createCorpNotification(List<Member> memberList, Corp corp, VerifyStatus verifyStatus) {
        memberList.forEach(
                member -> createNotification(CreateNotificationRequest.ofForCorp(NotificationType.CORP, corp,
                                verifyStatus),
                        member));
    }

    /**
     * 알림 생성
     */
    private void createNotification(CreateNotificationRequest createNotificationRequest, Member receiver) {

        String eventId = aesCipherUtil.encrypt(receiver.getId()) + "_" + System.currentTimeMillis();   //eventId 생성

        Notification savedNotification = notificationRepository.save(
                createNotificationRequest.toEntity(eventId, receiver));

        receiver.addUnreadNotifications();  //읽지 않은 알림 개수 +1

        Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(
                aesCipherUtil.encrypt(receiver.getId()));  //알림 받는 사람이 연결되어있는 모든 emitter에 이벤트 발송

        String encryptedReceiverId = aesCipherUtil.encrypt(receiver.getId());
        String encryptedSenderId =
                savedNotification.getSender() != null ? aesCipherUtil.encrypt(savedNotification.getSender().getId())
                        : null;
        String encryptedUrlPK =
                savedNotification.getUrlPK() != null ? aesCipherUtil.encrypt(savedNotification.getUrlPK()) : null;

        emitters.forEach((id, emitter) -> {
            // 알림 종류에 따른 response 전달
            if (savedNotification.getNotificationType().equals(NotificationType.POST)) {
                sendNotification(id, emitter.getSseEmitter(), eventId,
                        PostNotificationResponse.of(encryptedReceiverId, encryptedSenderId, encryptedUrlPK,
                                savedNotification));
                return;
            }
            if (savedNotification.getNotificationType().equals(NotificationType.COMMENT)) {
                String encryptedCommentId = aesCipherUtil.encrypt(savedNotification.getCommentId());
                sendNotification(id, emitter.getSseEmitter(), eventId,
                        CommentNotificationResponse.of(encryptedReceiverId, encryptedSenderId, encryptedUrlPK,
                                encryptedCommentId, savedNotification));
                return;
            }

            if (savedNotification.getNotificationType().equals(NotificationType.FOLLOW)) {
                sendNotification(id, emitter.getSseEmitter(), eventId,
                        FollowNotificationResponse.of(encryptedReceiverId, encryptedSenderId, savedNotification));
                return;
            }
            if (savedNotification.getNotificationType().equals(NotificationType.CORP)) {
                sendNotification(id, emitter.getSseEmitter(), eventId,
                        CorpNotificationResponse.of(encryptedReceiverId, encryptedUrlPK, savedNotification));
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
            log.error("Failed to send notification, eventId={}, emitterId={}, error={}", eventId, emitterId,
                    e.getMessage());
            emitter.completeWithError(e);
            emitterRepository.deleteById(emitterId);
        }
    }

    /**
     * 기술 게시글 알림 발송
     *
     * @param response 알림 내용
     */
    private void sendTechNotification(String emitterId, SseEmitter emitter, String eventId, Object response) {
        try {
            emitter.send(SseEmitter.event().id(eventId).name("tech").data(response));
        } catch (IOException e) {
            log.error("Failed to send notification, eventId={}, emitterId={}, error={}", eventId, emitterId,
                    e.getMessage());
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
    public List<NotificationListItemResponse> getNotificationList(NotificationType notificationType, Long memberId,
                                                                  int page, int size) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));  // 최신순 정렬

        List<Notification> notificationList = (notificationType.equals(NotificationType.ALL)) ?
                notificationRepository.findAllByReceiver(receiver, pageRequest).getContent()
                : notificationRepository.findAllByReceiverAndNotificationType(receiver, notificationType, pageRequest)
                        .getContent();

        return notificationList.stream()
                .map(notification -> {
                    String encryptedId = aesCipherUtil.encrypt(notification.getId());
                    String encryptedSenderId =
                            notification.getSender() != null ? aesCipherUtil.encrypt(notification.getSender().getId())
                                    : "KOFFEECHAT";
                    String encryptedUrlPK =
                            notification.getUrlPK() != null ? aesCipherUtil.encrypt(notification.getUrlPK()) : null;
                    String encryptedCommentId =
                            notification.getCommentId() != null ? aesCipherUtil.encrypt(notification.getCommentId())
                                    : null;

                    return NotificationListItemResponse.of(encryptedId, encryptedSenderId, encryptedUrlPK,
                            encryptedCommentId,
                            notification);
                }).collect(Collectors.toList());
    }

    /**
     * 알림 확인
     *
     * @param memberId member pk
     * @param notiId   notification pk
     * @return 읽지 않은 알림 개수
     */
    @Transactional
    public long updateNotificationIsRead(Long memberId, String notiId) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findByIdAndReceiver(aesCipherUtil.decrypt(notiId), receiver)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.isRead()) {
            notification.read();                   // 알림 읽음 처리
            receiver.removeUnreadNotifications();  // 읽지 않은 알림 개수 -1
        }

        return receiver.getUnreadNotificationCount();
    }

    /**
     * 알림 단건 삭제
     *
     * @param memberId member pk
     * @param notiId   notification pk
     * @return 읽지 않은 알림 개수
     */
    @Transactional
    public long deleteNotification(Long memberId, String notiId) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findById(aesCipherUtil.decrypt(notiId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.isRead()) {
            receiver.removeUnreadNotifications();  // 읽지 않은 알림 개수 -1
        }
        notificationRepository.delete(notification);

        return receiver.getUnreadNotificationCount();
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