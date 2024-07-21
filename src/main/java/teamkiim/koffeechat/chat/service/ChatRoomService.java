package teamkiim.koffeechat.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.chat.domain.room.ChatRoom;
import teamkiim.koffeechat.chat.domain.room.MemberChatRoom;
import teamkiim.koffeechat.chat.repository.ChatRoomRepository;
import teamkiim.koffeechat.chat.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.common.domain.Post;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;

    @Transactional
    public ResponseEntity<?> createChatRoom(Long postId, Long memberId){
        return ResponseEntity.ok("ok");
    }

    @Transactional
    public ResponseEntity<?> joinChatRoom(Long chatRoomId, Long memberId){
        return ResponseEntity.ok("ok");
    }
}
