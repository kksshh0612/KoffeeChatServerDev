package teamkiim.koffeechat.domain.post.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamkiim.koffeechat.domain.bookmark.domain.Bookmark;
import teamkiim.koffeechat.domain.bookmark.repository.BookmarkRepository;
import teamkiim.koffeechat.domain.bookmark.service.BookmarkService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.domain.postlike.service.PostLikeService;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostLikeService postLikeService;
    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private AESCipherUtil aesCipherUtil;
    @Mock
    private HttpServletRequest request;

    private Member member;
    private Post post;

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

        // CommunityPost 생성
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

    @DisplayName("게시글을 소프트 삭제하면 'isDeleted' 상태가 true로 변경된다.")
    @Test
    void softDeleteTest() {
        // given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        postService.softDelete(postId);

        // then
        assertTrue(post.isDeleted());
        verify(postRepository).findById(postId);
    }

    @DisplayName("게시글에 좋아요를 추가하면 좋아요 수가 1 증가한다.")
    @Test
    void likePostTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(postLikeService.isMemberLiked(post, member)).thenReturn(false);

        // when
        long likeCount = postService.like(postId, memberId);

        // then
        assertEquals(1, likeCount);
        verify(postLikeService).like(post, member);
    }

    @DisplayName("게시글의 기존 좋아요를 취소하면 좋아요 수가 1 감소한다.")
    @Test
    void cancelLikePostTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(postLikeService.isMemberLiked(post, member)).thenReturn(true);

        // when
        long likeCount = postService.like(postId, memberId);

        // then
        assertEquals(0, likeCount);
        verify(postLikeService).cancelLike(post, member);
    }

    @DisplayName("게시글을 북마크하면 북마크 수가 1 증가한다.")
    @Test
    void bookmarkPostTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookmarkService.isMemberBookmarked(member, post)).thenReturn(false);

        // when
        long bookmarkCount = postService.bookmark(postId, memberId);

        // then
        assertEquals(1, bookmarkCount);
        verify(bookmarkService).bookmark(member, post);
    }

    @DisplayName("게시글의 기존 북마크를 취소하면 북마크 수가 1 감소한다.")
    @Test
    void cancelBookmarkPostTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookmarkService.isMemberBookmarked(member, post)).thenReturn(true);

        // when
        long bookmarkCount = postService.bookmark(postId, memberId);

        // then
        assertEquals(0, bookmarkCount);
        verify(bookmarkService).cancelBookmark(member, post);
    }

    @DisplayName("게시글을 조회하면 조회수가 1 증가한다.")
    @Test
    void viewPostTest() {
        // given
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getSession().getAttribute("viewedPost_1_127.0.0.1")).thenReturn(null);

        // when
        postService.viewPost(post, request);

        // then
        assertEquals(1, post.getViewCount());
        verify(request.getSession()).setAttribute("viewedPost_1_127.0.0.1", true);
    }

    @DisplayName("특정 카테고리의 북마크된 게시글 목록을 조회한다.")
    @Test
    void findBookmarkPostListTest() {
        // given
        Long memberId = 1L;
        PostCategory category = PostCategory.DEV;

        Bookmark bookmark = new Bookmark();
        bookmark.setPost(post);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookmarkRepository.findAllByMemberAndPostCategory(any(Member.class), eq(category), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(bookmark)));
        when(aesCipherUtil.encrypt(post.getId())).thenReturn("encryptedPostId");

        // when
        List<BookmarkPostListResponse> response = postService.findBookmarkPostList(
                memberId, category, SortType.NEW, 0, 10
        );

        // then
        assertEquals(1, response.size());
        assertEquals("encryptedPostId", response.get(0).getPostId());
    }
}
