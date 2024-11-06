package teamkiim.koffeechat.domain.chat.room.tech.service;

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
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.chat.room.common.dto.response.ChatRoomListResponse;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.tech.domain.TechChatRoom;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.CreateTechChatRoomServiceRequest;
import teamkiim.koffeechat.domain.chat.room.tech.repository.TechChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.ChatNotificationService;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TechChatRoomService {

    private final static int MAX_MEMBER_SIZE = 100;

    private final TechChatRoomRepository techChatRoomRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final ChatNotificationService chatNotificationService;
    private final ChatRoomManager chatRoomManager;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 기술 채팅방 생성
     *
     * @param createTechChatRoomServiceRequest 채팅방 생성할 기술 정보 dto
     * @param memberId                         채팅방 생성을 요청한 사용자 PK
     */
    @Transactional
    public void createChatRoom(CreateTechChatRoomServiceRequest createTechChatRoomServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<TechChatRoom> existTechChatRoomList = techChatRoomRepository.findByChildSkillCategory(
                createTechChatRoomServiceRequest.getChildSkillCategory());

        for (TechChatRoom techChatRoom : existTechChatRoomList) {
            if (techChatRoom.getCurrentMemberSize() >= MAX_MEMBER_SIZE) {
                throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_EXIST);
            }
        }

        TechChatRoom techChatRoom = createTechChatRoomServiceRequest.toEntity(MAX_MEMBER_SIZE);

        techChatRoomRepository.save(techChatRoom);

        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .chatRoom(techChatRoom)
                .member(member)
                .build();

        memberChatRoomRepository.save(memberChatRoom);
    }

    /**
     * 기술 채팅방 참여
     *
     * @param chatRoomId 참여를 요청한 채팅방 PK
     * @param memberId   참여를 요청한 회원 PK
     * @param enterTime  참여를 요청한 시간
     */
    @Transactional
    public void enterChatRoom(Long chatRoomId, Long memberId, LocalDateTime enterTime) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        TechChatRoom techChatRoom = techChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        if (techChatRoom.getCurrentMemberSize() >= MAX_MEMBER_SIZE) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_FULL);
        }

        if (memberChatRoomRepository.findByMemberAndActiveChatRoom(member, techChatRoom).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_JOINED_CHAT_ROOM);
        }

        // 이전에 들어간 적 있는 회원이면 다시 입장 처리
        Optional<MemberChatRoom> existMemberChatRoom = memberChatRoomRepository.findByMemberAndNotActiveChatRoom(member,
                techChatRoom);
        if (existMemberChatRoom.isPresent()) {
            existMemberChatRoom.get().enter();
        } else {
            MemberChatRoom memberChatRoom = MemberChatRoom.of(member, techChatRoom, techChatRoom.getName(), null, true);

            memberChatRoomRepository.save(memberChatRoom);
        }

        techChatRoom.increaseMemberCount();                         // 채팅방 총 인원 수 + 1

        ChatMessageServiceRequest messageRequest = ChatMessageServiceRequest.builder()
                .messageType(MessageType.ENTER)
                .content(member.getNickname() + " 님이 입장했습니다.")
                .createdTime(enterTime)
                .build();

        chatMessageService.saveTextMessage(messageRequest, techChatRoom.getId(),
                aesCipherUtil.encrypt(techChatRoom.getId()), memberId);

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
                memberChatRoomRepository.findAllByMemberAndChatRoomType(member, ChatRoomType.TECH, pageRequest)
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

        TechChatRoom techChatRoom = techChatRoomRepository.findById(decryptChatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndActiveChatRoom(member, techChatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

        // 퇴장 메세지 전송
        ChatMessageServiceRequest chatMessageServiceRequest = ChatMessageServiceRequest.builder()
                .messageType(MessageType.EXIT)
                .content(member.getNickname() + " 님이 퇴장하셨습니다")
                .createdTime(exitTime)
                .build();

        chatMessageService.saveTextMessage(chatMessageServiceRequest, decryptChatRoomId, encryptChatRoomId, memberId);

        memberChatRoom.exit();

        techChatRoom.decreaseMemberCount();

        chatRoomManager.removeMember(decryptChatRoomId, member);

        // 채팅 알림 정보 삭제
        chatNotificationService.removeChatRoomNotification(memberId, decryptChatRoomId);
    }
}
