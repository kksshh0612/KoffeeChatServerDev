package teamkiim.koffeechat.domain.tag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;
import teamkiim.koffeechat.domain.tag.domain.PostTag;
import teamkiim.koffeechat.domain.tag.domain.Tag;
import teamkiim.koffeechat.domain.tag.repository.PostTagRepository;
import teamkiim.koffeechat.domain.tag.repository.TagRepository;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.domain.post.common.dto.response.TagInfoDto;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;
    @Mock
    private PostTagRepository postTagRepository;
    @Mock
    private AESCipherUtil aesCipherUtil;

    private Post post;

    @BeforeEach
    void setUp() {
        // CommunityPost 생성
        CommunityPost communityPost = CommunityPost.builder()
                .member(member) // Member와 연관 설정
                .title("test title")
                .bodyContent("test content")
                .isEditing(false)
                .build();

        post = communityPost;
    }

    @Test
    @DisplayName("게시글에 새 태그를 추가하면 태그가 저장된다.")
    void addTagsTest() {
        // given
        List<String> newTagContentList = List.of("Spring", "Java");
        when(tagRepository.findByContent("Spring")).thenReturn(Optional.empty());
        when(tagRepository.findByContent("Java")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        tagService.addTags(post, newTagContentList);

        // then
        assertEquals(2, post.getPostTagList().size());
        assertTrue(post.getPostTagList().stream()
                .anyMatch(postTag -> postTag.getTag().getContent().equals("Spring")));
        assertTrue(post.getPostTagList().stream()
                .anyMatch(postTag -> postTag.getTag().getContent().equals("Java")));
    }

    @Test
    @DisplayName("게시글 태그를 수정하면 새 태그가 추가되고 기존 태그는 삭제된다.")
    void updateTagsTest() {
        // given
        Tag existingTag = new Tag("Spring");
        PostTag postTag = new PostTag(post, existingTag);
        post.addTag(existingTag);
        List<String> newTagContentList = List.of("Java");

        when(postTagRepository.findAllByPost(post)).thenReturn(List.of(postTag));
        when(tagRepository.findByContent("Java")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        tagService.updateTags(post, newTagContentList);

        // then
        assertEquals(1, post.getPostTagList().size());
        assertEquals("Java", post.getPostTagList().get(0).getTag().getContent());
    }

    @Test
    @DisplayName("게시글의 태그 목록을 TagInfoDto 리스트로 변환한다.")
    void toTagInfoDtoListTest() {
        // given
        Tag tag1 = new Tag("Spring");
        Tag tag2 = new Tag("Java");
        post.addTag(tag1);
        post.addTag(tag2);

        when(aesCipherUtil.encrypt(tag1.getId())).thenReturn("encryptedSpring");
        when(aesCipherUtil.encrypt(tag2.getId())).thenReturn("encryptedJava");

        // when
        List<TagInfoDto> tagInfoDtoList = tagService.toTagInfoDtoList(post);

        // then
        assertEquals(2, tagInfoDtoList.size());
        assertTrue(tagInfoDtoList.stream().anyMatch(dto -> dto.getEncryptedId().equals("encryptedSpring")));
        assertTrue(tagInfoDtoList.stream().anyMatch(dto -> dto.getEncryptedId().equals("encryptedJava")));
    }
}
