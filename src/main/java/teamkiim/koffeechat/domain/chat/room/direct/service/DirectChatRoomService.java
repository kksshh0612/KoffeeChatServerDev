package teamkiim.koffeechat.domain.chat.room.direct.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.chat.room.common.ChatRoomManager;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.direct.domain.DirectChatRoom;
import teamkiim.koffeechat.domain.chat.room.direct.dto.response.CreateDirectChatRoomResponse;
import teamkiim.koffeechat.domain.chat.room.direct.dto.response.DirectChatRoomListResponse;
import teamkiim.koffeechat.domain.chat.room.direct.repository.DirectChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.ChatNotificationService;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DirectChatRoomService {

    private final DirectChatRoomRepository directChatRoomRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatRoomManager chatRoomManager;
    private final ChatNotificationService chatNotificationService;
    private final ChatMessageService chatMessageService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 일대일 채팅방 생성
     *
     * @param requestMemberId 일대일 채팅방 생성 요청한 회원 PK
     * @param targetMemberId  일대일 채팅방 생성 상대방 회원 PK
     * @return CreateDirectChatRoomResponse
     */
    @Transactional
    public CreateDirectChatRoomResponse create(Long requestMemberId, Long targetMemberId) {

        Member requestMember = memberRepository.findById(requestMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<DirectChatRoom> existChatRoom = directChatRoomRepository.findDirectChatRoomByMembers(requestMember,
                targetMember);

        // 이전에 해당 채팅방을 생성한 적이 있는 경우
        if (existChatRoom.isPresent()) {
            Optional<MemberChatRoom> memberChatRoom = memberChatRoomRepository.findByMemberAndNotActiveChatRoom(
                    requestMember, existChatRoom.get());

            // 현재 퇴장한 상태인 경우
            if (memberChatRoom.isPresent()) {
                memberChatRoom.get().enter();
                chatRoomManager.addMember(existChatRoom.get().getId(), requestMember.getId());
                chatNotificationService.addChatRoomNotification(requestMember.getId(), existChatRoom.get().getId());

                // 입장 메세지 전송 (재입장)
                ChatMessageServiceRequest chatMessageServiceRequest = ChatMessageServiceRequest.builder()
                        .messageType(MessageType.ENTER)
                        .content(requestMember.getNickname() + " 님이 입장하셨습니다")
                        .createdTime(null)
                        .build();

                chatMessageService.saveTextMessage(chatMessageServiceRequest, existChatRoom.get().getId(),
                        aesCipherUtil.encrypt(existChatRoom.get().getId()), requestMemberId);
            }

            log.info("[DirectChatRoomService / craeteChatRoom] 재생성 memberId : {}, chatRoomId : {}",
                    requestMemberId, existChatRoom.get().getId());

            return new CreateDirectChatRoomResponse(aesCipherUtil.encrypt(existChatRoom.get().getId()));
        }

        DirectChatRoom directChatRoom = DirectChatRoom.builder()
                .chatRoomType(ChatRoomType.DIRECT)
                .name("DIRECT MESSAGE")
                .lastMessageTime(null)
                .build();

        DirectChatRoom saveChatRoom = directChatRoomRepository.save(directChatRoom);

        MemberChatRoom requestMemberChatRoom = MemberChatRoom.of(requestMember, directChatRoom,
                requestMember.getNickname(),
                null, true);
        MemberChatRoom targetMemberChatRoom = MemberChatRoom.of(targetMember, directChatRoom,
                targetMember.getNickname(),
                null, true);

        memberChatRoomRepository.saveAll(List.of(requestMemberChatRoom, targetMemberChatRoom));

        // 채팅방 멤버 관리 목록에 추가
        chatRoomManager.addMember(saveChatRoom.getId(), requestMember.getId());
        chatRoomManager.addMember(saveChatRoom.getId(), targetMember.getId());
        // 채팅 알림 등록
        chatNotificationService.addChatRoomNotification(requestMember.getId(), saveChatRoom.getId());
        chatNotificationService.addChatRoomNotification(targetMember.getId(), saveChatRoom.getId());

        log.info("[DirectChatRoomService / craeteChatRoom] 첫 생성 memberId : {}, chatRoomId : {}",
                requestMemberId, saveChatRoom.getId());

        return new CreateDirectChatRoomResponse(aesCipherUtil.encrypt(saveChatRoom.getId()));
    }

    /**
     * 참여중인 채팅방 목록 조회 -> 채팅방 별 사용자의 퇴장 시간 기준 안읽은 메세지 수 리턴
     *
     * @param memberId 채팅방 목록 조회 요청한 회원 PK
     * @param page     페이징에 사용될 page
     * @param size     페이징에 사용될 size
     * @return List<ChatRoomListResponse>
     */
    public List<DirectChatRoomListResponse> findChatRoomList(Long memberId, int page, int size) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 닫은 시간 기준 내림차순 정렬
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("closeTime").nullsLast()));
        List<MemberChatRoom> joinMemberChatRooms =
                memberChatRoomRepository.findAllActiveChatRoomByMemberAndChatRoomType(
                        member, ChatRoomType.DIRECT, pageRequest).getContent();

        // 채팅방별 정보 조회
        List<ChatRoomInfoDto> chatRoomInfoDtoList = chatMessageService.getChatRoomInfo(joinMemberChatRooms);

        List<DirectChatRoomListResponse> directChatRoomListResponseList = new ArrayList<>();

        for (ChatRoomInfoDto chatRoomInfoDto : chatRoomInfoDtoList) {
            MemberChatRoom memberChatRoom = chatRoomInfoDto.getMemberChatRoom();

            MemberChatRoom oppositeMemberChatRoom = memberChatRoomRepository.findByChatRoomExceptMember(
                            memberChatRoom.getChatRoom(), memberChatRoom.getMember())
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

            DirectChatRoomListResponse directChatRoomListResponse = DirectChatRoomListResponse.of(
                    aesCipherUtil.encrypt(memberId),
                    aesCipherUtil.encrypt(chatRoomInfoDto.getMemberChatRoom().getChatRoom().getId()),
                    aesCipherUtil.encrypt(oppositeMemberChatRoom.getMember().getId()),
                    oppositeMemberChatRoom.getMember(),
                    chatRoomInfoDto
            );

            directChatRoomListResponseList.add(directChatRoomListResponse);
        }

        chatNotificationService.startNotifications(memberId);

        return directChatRoomListResponseList;
    }

    /**
     * 채팅방 퇴장
     *
     * @param decryptChatRoomId 복호화된 채팅방 PK
     * @param encryptChatRoomId 암호화된 채팅방 PK
     * @param memberId          채팅방 퇴장 요청한 회원 PK
     * @param exitTime          채팅방 퇴장 시간
     */
    @Transactional
    public void exit(Long decryptChatRoomId, String encryptChatRoomId, Long memberId, LocalDateTime exitTime) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = directChatRoomRepository.findById(decryptChatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndActiveChatRoom(member, chatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

        // 퇴장 메세지 전송
        ChatMessageServiceRequest chatMessageServiceRequest = ChatMessageServiceRequest.builder()
                .messageType(MessageType.EXIT)
                .content(member.getNickname() + " 님이 퇴장하셨습니다")
                .createdTime(exitTime)
                .build();

        chatMessageService.saveTextMessage(chatMessageServiceRequest, decryptChatRoomId, encryptChatRoomId, memberId);

        // 퇴장 처리
        memberChatRoom.exit();
        chatRoomManager.removeMember(decryptChatRoomId, member.getId());
        chatNotificationService.removeChatRoomNotification(memberId, decryptChatRoomId);

        log.info("[DirectChatRoomService / exit] memberId : {}, chatRoomId : {}", memberId, decryptChatRoomId);
    }
}
