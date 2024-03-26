package teamkiim.koffeechat.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.post.dev.DevPost;
import teamkiim.koffeechat.post.PostRepository;
import teamkiim.koffeechat.post.PostService;
import teamkiim.koffeechat.user.User;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 게시글_생성() throws Exception{
        //given
        DevPost devPost= createDevPost();  //개발 게시글 생성

        //when
        Long devPostId = postService.createPost(devPost);

        //then : 게시글이 생성되어 postRepository에 저장된다.
        Assertions.assertEquals(devPost, postRepository.findOne(devPostId));

    }

    private DevPost createDevPost() {
        User user= new User();
        em.persist(user);
        DevPost devPost= new DevPost();
        devPost.setUser(user);
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
