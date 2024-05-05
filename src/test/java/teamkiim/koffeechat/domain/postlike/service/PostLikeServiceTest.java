package teamkiim.koffeechat.domain.postlike.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.domain.postlike.domain.PostLike;
import teamkiim.koffeechat.domain.postlike.repository.PostLikeRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

class PostLikeServiceTest {

    @InjectMocks
    private PostLikeService postLikeService;
    @Mock
    private PostLikeRepository postLikeRepository;

    private Post post;
    private Member member;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .email("test@example.com")
                .password("encryptedPassword")
                .nickname("test")
                .memberRole(MemberRole.STUDENT)
                .interestSkillCategoryList(
                        List.of(new SkillCategory(ParentSkillCategory.WEB, ChildSkillCategory.DJANGO)))
                .followerCount(10)
                .followingCount(5)
                .profileImageUrl("https://example.com/image.jpg")
                .build();

        CommunityPost communityPost = CommunityPost.builder()
                .member(member) // Member와 연관 설정
                .title("test title")
                .bodyContent("test content")
                .isEditing(false)
                .build();

        // 테스트용 데이터 설정
        this.member = member;
        this.post = communityPost;
    }

    @DisplayName("멤버가 게시물을 좋아요 했는지 확인할 때, 좋아요가 존재하면 true를 반환한다.")
    @Test
    void isMemberLiked_shouldReturnTrue_whenLikeExists() {
        // given
        when(postLikeRepository.findByPostAndMember(post, member))
                .thenReturn(Optional.of(new PostLike()));

        // when
        boolean result = postLikeService.isMemberLiked(post, member);

        // then
        assertThat(result).isTrue();
        verify(postLikeRepository, times(1)).findByPostAndMember(post, member);
    }

    @DisplayName("멤버가 게시물을 좋아요 했는지 확인할 때, 좋아요가 존재하지 않으면 false를 반환한다.")
    @Test
    void isMemberLiked_shouldReturnFalse_whenLikeDoesNotExist() {
        // given
        when(postLikeRepository.findByPostAndMember(post, member))
                .thenReturn(Optional.empty());

        // when
        boolean result = postLikeService.isMemberLiked(post, member);

        // then
        assertThat(result).isFalse();
        verify(postLikeRepository, times(1)).findByPostAndMember(post, member);
    }

    @DisplayName("멤버가 게시물을 좋아요하면 PostLike를 저장한다.")
    @Test
    void like_shouldSavePostLike() {
        // given
        ArgumentCaptor<PostLike> captor = ArgumentCaptor.forClass(PostLike.class);

        // when
        postLikeService.like(post, member);

        // then
        verify(postLikeRepository, times(1)).save(captor.capture());
        PostLike savedPostLike = captor.getValue();
        assertThat(savedPostLike.getPost()).isEqualTo(post);
        assertThat(savedPostLike.getMember()).isEqualTo(member);
    }

    @DisplayName("멤버가 좋아요를 취소하면 PostLike를 삭제한다.")
    @Test
    void cancelLike_shouldDeletePostLike_whenLikeExists() {
        // given
        PostLike postLike = PostLike.create(member, post);
        when(postLikeRepository.findByPostAndMember(post, member))
                .thenReturn(Optional.of(postLike));

        // when
        postLikeService.cancelLike(post, member);

        // then
        verify(postLikeRepository, times(1)).delete(postLike);
    }

    @DisplayName("멤버가 좋아요를 취소할 때, 좋아요가 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void cancelLike_shouldThrowException_whenLikeDoesNotExist() {
        // given
        when(postLikeRepository.findByPostAndMember(post, member))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postLikeService.cancelLike(post, member))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LIKE_NOT_FOUND.getMsg());
        verify(postLikeRepository, never()).delete(any());
    }
}
