package teamkiim.koffeechat.domain.chat.room.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.chat.room.common.dto.response.ChatRoomListResponse;
import teamkiim.koffeechat.domain.chat.room.common.repository.ChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;

    /**
     * 참여중인 채팅방 목록 조회
     * -> 채팅방 별 사용자의 퇴장 시간 기준 안읽은 메세지 수, 마지막 메세지 리턴
     *
     * @param memberId
     * @param page
     * @param size
     * @return
     */
    public List<ChatRoomListResponse> findChatRoomList(Long memberId, int page, int size) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "closeTime"));

        List<MemberChatRoom> memberChatRoomList = memberChatRoomRepository.findAllByMember(member, pageRequest).getContent();

        List<ChatRoomInfoDto> chatRoomInfoDtoList = chatMessageService.findCount(memberChatRoomList);

        List<ChatRoomListResponse> chatRoomListResponseList = new ArrayList<>();

        for (ChatRoomInfoDto chatRoomInfoDto : chatRoomInfoDtoList) {
            ChatRoomListResponse chatRoomListResponse = ChatRoomListResponse.of(chatRoomInfoDto);

            chatRoomListResponseList.add(chatRoomListResponse);
        }

        return chatRoomListResponseList;
    }

//    public ChatRoomListResponse findChatRoom(Long chatRoomId){
//
//    }


    @Transactional
    public void close(Long chatRoomId, Long memberId, LocalDateTime closeTime) throws Exception {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndChatRoom(member, chatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

        memberChatRoom.updateCloseTime(closeTime);
    }
}
