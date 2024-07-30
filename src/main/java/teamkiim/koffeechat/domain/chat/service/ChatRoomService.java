package teamkiim.koffeechat.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.repository.ChatRoomRepository;
import teamkiim.koffeechat.domain.chat.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;

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
