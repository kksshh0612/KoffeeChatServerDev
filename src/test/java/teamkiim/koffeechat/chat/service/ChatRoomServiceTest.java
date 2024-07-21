package teamkiim.koffeechat.chat.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import teamkiim.koffeechat.TestSupport;
import teamkiim.koffeechat.chat.domain.room.ChatRoom;
import teamkiim.koffeechat.chat.repository.ChatRoomRepository;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.MemberRole;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.post.common.repository.PostRepository;
import teamkiim.koffeechat.post.dev.domain.DevPost;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class ChatRoomServiceTest extends TestSupport {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void tearDown(){
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
    }

    @DisplayName("회원 PK와 게시물 PK를 이용하여 게시물과 연관된 채팅방을 생성한다.")
    @Test
    void createChatRoom() {
        // given
        Member member = memberRepository.save(createMember("test@test.com"));
        Post post = postRepository.save(createPost(member, "test"));
        Long memberId = member.getId();
        Long postId = post.getId();

        // when
        ResponseEntity<?> responseEntity = chatRoomService.createChatRoom(memberId, postId);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Long) responseEntity.getBody()).isNotNull();
        Long chatRoomId = (Long) responseEntity.getBody();
        Optional<ChatRoom> savedChatRoom = chatRoomRepository.findById(chatRoomId);
        assertThat(savedChatRoom).isNotNull();
        assertThat(savedChatRoom.get().getCreateMember().getId()).isEqualTo(memberId);
        assertThat(savedChatRoom.get().getPost().getId()).isEqualTo(postId);

    }

    private Member createMember(String email){
        return Member.builder()
                .email(email)
                .password("test")
                .nickname("test")
                .memberRole(MemberRole.FREELANCER)
                .interestSkillCategoryList(null)
                .profileImageName(null)
                .build();
    }
    private DevPost createPost(Member member, String title){
        return DevPost.builder()
                .member(member)
                .title(title)
                .bodyContent("Sample body content")
                .viewCount(0L)
                .likeCount(0L)
                .createdTime(LocalDateTime.of(2024, 6, 12, 10, 0))
                .modifiedTime(null)
                .isEditing(false)
                .skillCategoryList(null)
                .build();
    }
}