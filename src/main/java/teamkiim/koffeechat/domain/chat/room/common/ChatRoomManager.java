package teamkiim.koffeechat.domain.chat.room.common;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.global.aop.lock.DistributedLock;
import teamkiim.koffeechat.global.redis.util.RedissonUtil;

@Component
@RequiredArgsConstructor
public class ChatRoomManager {

    private final RedissonUtil redissonUtil;
    private final MemberChatRoomRepository memberChatRoomRepository;    // 채팅방-회원 관계를 관리하는 Repository

    private static final String CHAT_ROOM_PREFIX = "chat_room:";
    private static final String LOCK_NAME = "chat_room_lock";

    // 서비스 시작 시 DB에서 채팅방과 회원 정보 로드
    @PostConstruct
    public void initChatRoomMemberInfoMap() {

        // DB에서 모든 퇴장하지 않은 회원-채팅방 정보 가져옴
        List<MemberChatRoom> memberChatRoomList = memberChatRoomRepository.findAllActiveMemberChatRooms();

        // 채팅방 ID별로 회원을 맵에 추가
        for (MemberChatRoom memberChatRoom : memberChatRoomList) {
            Long chatRoomId = memberChatRoom.getChatRoom().getId();
            Long memberId = memberChatRoom.getMember().getId();
            redissonUtil.setData(CHAT_ROOM_PREFIX, chatRoomId, memberId);
        }
    }

    /**
     * 채팅방에서 회원 삭제
     *
     * @param chatRoomId
     * @param memberId
     */
    @DistributedLock(key = LOCK_NAME)
    public void removeMember(Long chatRoomId, Long memberId) {

        redissonUtil.deleteData(CHAT_ROOM_PREFIX, chatRoomId, memberId);
    }

    /**
     * 채팅방에 회원 추가
     *
     * @param chatRoomId
     * @param memberId
     */
    @DistributedLock(key = LOCK_NAME)
    public void addMember(Long chatRoomId, Long memberId) {

        redissonUtil.setData(CHAT_ROOM_PREFIX, chatRoomId, memberId);
    }

    /**
     * 채팅방에 입장해있는 모든 회원 id 조회
     *
     * @param chatRoomId
     * @return
     */
    public List<Long> getMemberIds(Long chatRoomId) {

        return redissonUtil.getDataList(CHAT_ROOM_PREFIX, chatRoomId);
    }
}
