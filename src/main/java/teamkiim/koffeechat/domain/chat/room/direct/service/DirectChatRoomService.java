package teamkiim.koffeechat.domain.chat.room.direct.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
import teamkiim.koffeechat.domain.chat.room.common.dto.response.ChatRoomListResponse;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.direct.domain.DirectChatRoom;
import teamkiim.koffeechat.domain.chat.room.direct.dto.response.CreateDirectChatRoomResponse;
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
     * @param requestMemberId
     * @param targetMemberId
     * @return
     */
    @Transactional
    public CreateDirectChatRoomResponse createChatRoom(Long requestMemberId, Long targetMemberId) {

        Member requestMember = memberRepository.findById(requestMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<DirectChatRoom> existChatRoom = directChatRoomRepository.findDirectChatRoomByMembers(requestMember,
                targetMember);

        if (existChatRoom.isPresent()) {

            Optional<MemberChatRoom> memberChatRoom = memberChatRoomRepository.findByMemberAndActiveChatRoom(
                    requestMember, existChatRoom.get());

            // 이전에 들어갔다가 나간 채팅방은 재진입
            if (memberChatRoom.isPresent()) {
                memberChatRoom.get().enter();
                chatRoomManager.addMember(existChatRoom.get().getId(), requestMember);
                chatNotificationService.addChatRoomNotification(requestMember.getId(), existChatRoom.get().getId());
            } else {
                // 이미 존재하는 채팅방이면
                return new CreateDirectChatRoomResponse(aesCipherUtil.encrypt(existChatRoom.get().getId()));
            }
        }

        DirectChatRoom directChatRoom = DirectChatRoom.builder()
                .chatRoomType(ChatRoomType.DIRECT)
                .name("DIRECT MESSAGE")
                .lastMessageTime(null)
                .build();

        DirectChatRoom saveChatRoom = directChatRoomRepository.save(directChatRoom);

        MemberChatRoom memberChatRoom1 = MemberChatRoom.of(requestMember, directChatRoom, requestMember.getNickname(),
                null, true);
        MemberChatRoom memberChatRoom2 = MemberChatRoom.of(targetMember, directChatRoom, targetMember.getNickname(),
                null, true);

        memberChatRoomRepository.saveAll(List.of(memberChatRoom1, memberChatRoom2));

        // 채팅방 멤버 관리 추가
        chatRoomManager.addMember(saveChatRoom.getId(), requestMember);
        chatRoomManager.addMember(saveChatRoom.getId(), targetMember);

        // 채팅 알림 등록
        chatNotificationService.addChatRoomNotification(requestMember.getId(), saveChatRoom.getId());
        chatNotificationService.addChatRoomNotification(targetMember.getId(), saveChatRoom.getId());

        return new CreateDirectChatRoomResponse(aesCipherUtil.encrypt(saveChatRoom.getId()));
    }

    /**
     * 참여중인 채팅방 목록 조회 -> 채팅방 별 사용자의 퇴장 시간 기준 안읽은 메세지 수 리턴
     *
     * @param memberId 채팅방 목록 조회 요청한 회원 PK
     * @param page
     * @param size
     * @return
     */
    public List<ChatRoomListResponse> findChatRoomList(Long memberId, int page, int size) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        List<MemberChatRoom> joinMemberChatRooms =
                memberChatRoomRepository.findAllByMemberAndChatRoomType(member, ChatRoomType.DIRECT, pageRequest)
                        .getContent();

        List<ChatRoomInfoDto> chatRoomInfoDtoList = chatMessageService.countUnreadMessageCount(joinMemberChatRooms);

        List<ChatRoomListResponse> chatRoomListResponseList = new ArrayList<>();

        for (ChatRoomInfoDto chatRoomInfoDto : chatRoomInfoDtoList) {
            MemberChatRoom memberChatRoom = chatRoomInfoDto.getMemberChatRoom();

            MemberChatRoom oppositeMemberChatRoom = memberChatRoomRepository.findByChatRoomExceptMember(
                            memberChatRoom.getChatRoom(), memberChatRoom.getMember())
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

            ChatRoomListResponse chatRoomListResponse = ChatRoomListResponse.of(aesCipherUtil.encrypt(memberId),
                    aesCipherUtil.encrypt(chatRoomInfoDto.getMemberChatRoom().getChatRoom().getId()), chatRoomInfoDto,
                    aesCipherUtil.encrypt(oppositeMemberChatRoom.getMember().getId()),
                    oppositeMemberChatRoom.getMember());

            chatRoomListResponseList.add(chatRoomListResponse);
        }

        chatNotificationService.startNotifications(memberId);

        return chatRoomListResponseList;
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

        memberChatRoom.exit();

        chatRoomManager.removeMember(decryptChatRoomId, member);

        // 채팅 알림 정보 삭제
        chatNotificationService.removeChatRoomNotification(memberId, decryptChatRoomId);
    }
}
