package teamkiim.koffeechat.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.post.dev.DevPost;
import teamkiim.koffeechat.post.dev.DevPostRepository;
import teamkiim.koffeechat.post.dev.DevPostService;
import teamkiim.koffeechat.member.Member;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    DevPostService postService;
    @Autowired
    DevPostRepository postRepository;
    @Autowired
    EntityManager em;

//    @Test
//    public void 게시글_생성() throws Exception{
//        //given
//        DevPost devPost= createDevPost();  //개발 게시글 생성
//
//        //when
//        Long devPostId = postService.createDevPost(devPost);
//
//        //then : 게시글이 생성되어 postRepository에 저장된다.
//        Assertions.assertEquals(devPost, postRepository.findOneDev(devPostId));
//
//    }

    private DevPost createDevPost() {
        Member member = new Member();
        em.persist(member);
        DevPost devPost= new DevPost();
        devPost.setMember(member);
        devPost.setTitle("post crud에 대해 알아보자.");
        devPost.setBodyContent("잘 만들고있는건지 모르겠네요");
        devPost.setViewCount(10L);
        devPost.setLikeCount(10L);
        devPost.setCreatedTime(LocalDateTime.now());
        devPost.setModifiedTime(LocalDateTime.now());
        devPost.setChatRoomId(1L);
        em.persist(devPost);
        return devPost;
    }
}
