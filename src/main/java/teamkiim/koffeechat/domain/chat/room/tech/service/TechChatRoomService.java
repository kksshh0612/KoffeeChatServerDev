package teamkiim.koffeechat.domain.chat.room.tech.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.tech.domain.TechChatRoom;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.CreateTechChatRoomServiceRequest;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.EnterTechChatRoomServiceRequest;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.ExitTechChatRoomServiceRequest;
import teamkiim.koffeechat.domain.chat.room.tech.repository.TechChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TechChatRoomService {

    private final static int MAX_MEMBER_SIZE = 100;
    private final static int MIN_REQUIRED_MEMBER_SIZE = 90;

    private final TechChatRoomRepository techChatRoomRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatMessageService chatMessageService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 기술 채팅방 생성
     *
     * @param createTechChatRoomServiceRequest
     * @param memberId                         채팅방 생성을 요청한 사용자 PK
     */
    @Transactional
    public void createChatRoom(CreateTechChatRoomServiceRequest createTechChatRoomServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<TechChatRoom> existTechChatRoomList = techChatRoomRepository.findByChildSkillCategory(
                createTechChatRoomServiceRequest.getChildSkillCategory());

        for (TechChatRoom techChatRoom : existTechChatRoomList) {
            if (techChatRoom.getCurrentMemberSize() < MIN_REQUIRED_MEMBER_SIZE) {
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
     * 기술 채팅방 입장
     *
     * @param enterTechChatRoomServiceRequest
     * @param memberId                        채팅방 입장을 요청한 사용자 PK
     */
    @Transactional
    public void enterChatRoom(EnterTechChatRoomServiceRequest enterTechChatRoomServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        TechChatRoom techChatRoom = techChatRoomRepository.findById(enterTechChatRoomServiceRequest.getChatRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        if (techChatRoom.getCurrentMemberSize() >= MAX_MEMBER_SIZE) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_FULL);
        }

        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .chatRoom(techChatRoom)
                .member(member)
                .build();

        memberChatRoomRepository.save(memberChatRoom);

        techChatRoom.increaseMemberCount();                         // 채팅방 총 인원 수 + 1

        ChatMessageServiceRequest messageRequest = ChatMessageServiceRequest.builder()
                .messageType(MessageType.ENTER)
                .content(member.getNickname() + " 님이 입장했습니다.")
                .createdTime(enterTechChatRoomServiceRequest.getEnterTime())
                .build();

        chatMessageService.saveTextMessage(messageRequest, techChatRoom.getId(), memberId);
    }

    /**
     * 기술 채팅방 퇴장
     *
     * @param exitTechChatRoomServiceRequest
     * @param memberId                       채팅방 입장을 요청한 사용자 PK
     */
    @Transactional
    public void exitChatRoom(ExitTechChatRoomServiceRequest exitTechChatRoomServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        TechChatRoom techChatRoom = techChatRoomRepository.findById(exitTechChatRoomServiceRequest.getChatRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndChatRoom(member, techChatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

        memberChatRoomRepository.delete(memberChatRoom);            // 채팅방 삭제

        techChatRoom.decreaseMemberCount();                         // 채팅방 총 인원 수 - 1

        ChatMessageServiceRequest messageRequest = ChatMessageServiceRequest.builder()
                .messageType(MessageType.EXIT)
                .content(member.getNickname() + " 님이 퇴장했습니다.")
                .createdTime(exitTechChatRoomServiceRequest.getExitTime())
                .build();

        chatMessageService.saveTextMessage(messageRequest, techChatRoom.getId(), member.getId());
    }
}
