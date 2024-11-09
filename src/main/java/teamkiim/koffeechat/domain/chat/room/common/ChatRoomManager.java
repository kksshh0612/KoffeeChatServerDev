package teamkiim.koffeechat.domain.chat.room.common;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;

@Component
@RequiredArgsConstructor
public class ChatRoomManager {

    private Map<Long, List<Member>> chatRoomMemberInfoMap = new ConcurrentHashMap<>();

    private final MemberChatRoomRepository memberChatRoomRepository;  // 채팅방-회원 관계를 관리하는 Repository

    // 서비스 시작 시 DB에서 채팅방과 회원 정보 로드
    @PostConstruct
    public void initChatRoomMemberInfoMap() {

        // DB에서 모든 퇴장하지 않은 회원-채팅방 정보 가져옴
        List<MemberChatRoom> memberChatRoomList = memberChatRoomRepository.findAllActiveMemberChatRooms();

        // 채팅방 ID별로 회원을 맵에 추가
        for (MemberChatRoom memberChatRoom : memberChatRoomList) {
            Long chatRoomId = memberChatRoom.getChatRoom().getId();
            Member member = memberChatRoom.getMember();
            addMember(chatRoomId, member);  // 채팅방에 회원 추가
        }
    }

    public synchronized void removeMember(Long chatRoomId, Member member) {

        List<Member> members = chatRoomMemberInfoMap.get(chatRoomId);
        if (members != null && !members.isEmpty()) {
            members.remove(member);
        }
    }

    public List<Member> getMembers(Long chatRoomId) {
        return chatRoomMemberInfoMap.getOrDefault(chatRoomId, new ArrayList<>());
    }

    public List<Long> getMemberIds(Long chatRoomId) {
        return chatRoomMemberInfoMap.getOrDefault(chatRoomId, new ArrayList<>()).stream()
                .map(Member::getId).toList();
    }

    public synchronized void addMember(Long chatRoomId, Member member) {
        chatRoomMemberInfoMap.computeIfAbsent(chatRoomId, k -> new ArrayList<>()).add(member);
    }

}
