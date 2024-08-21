package teamkiim.koffeechat.domain.bookmark.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import teamkiim.koffeechat.TestSupport;
import teamkiim.koffeechat.domain.bookmark.domain.Bookmark;
import teamkiim.koffeechat.domain.bookmark.repository.BookmarkRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkServiceTest extends TestSupport {

    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void tearDown() {
        bookmarkRepository.deleteAllInBatch();
    }

    @DisplayName("회원이 게시글에 북마크를 했다면 true를 반환한다.")
    @Test
    void isMemberBookmarkedWhenBookmarked() {
        // given
        Member saveMember = memberRepository.save(createMember("test@test.com"));
        Post savePost = postRepository.save(createPost(saveMember));
        bookmarkRepository.save(new Bookmark(saveMember, savePost));

        // when
        boolean memberBookmarked = bookmarkService.isMemberBookmarked(saveMember, savePost);

        // then
        Assertions.assertThat(memberBookmarked).isTrue();
    }

    @DisplayName("회원이 게시글에 북마크를 하지 않았다면 false를 반환한다.")
    @Test
    void isMemberBookmarkedWhenNotBookmarked() {
        // given
        Member saveMember = memberRepository.save(createMember("test@test.com"));
        Post savePost = postRepository.save(createPost(saveMember));

        // when
        boolean memberBookmarked = bookmarkService.isMemberBookmarked(saveMember, savePost);

        // then
        Assertions.assertThat(memberBookmarked).isFalse();
    }

    @DisplayName("회원이 게시글에 북마크를 누르면 북마크가 생성된다.")
    @Test
    void bookmark() {
        // given
        Member saveMember = memberRepository.save(createMember("test@test.com"));
        Post savePost = postRepository.save(createPost(saveMember));

        // when
        bookmarkService.bookmark(saveMember, savePost);

        // then
        Optional<Bookmark> findBookmark = bookmarkRepository.findByPostAndMember(savePost, saveMember);

        Assertions.assertThat(findBookmark).isPresent();
        Assertions.assertThat(findBookmark.get().getMember().getId()).isEqualTo(saveMember.getId());
        Assertions.assertThat(findBookmark.get().getPost().getId()).isEqualTo(savePost.getId());
    }

    @DisplayName("회원이 북마크한 게시글의 북마크를 취소한다.")
    @Test
    void cancelBookmark() {
        // given
        Member saveMember = memberRepository.save(createMember("test@test.com"));
        Post savePost = postRepository.save(createPost(saveMember));
        bookmarkRepository.save(new Bookmark(saveMember, savePost));

        // when
        bookmarkService.cancelBookmark(saveMember, savePost);

        // then
        Optional<Bookmark> findBookmark = bookmarkRepository.findByPostAndMember(savePost, saveMember);

        Assertions.assertThat(findBookmark).isEmpty();
    }

    @DisplayName("회원이 북마크를 하지 않은 게시글의 북마크를 취소하려 하면 예외가 발생한다.")
    @Test
    void cancelBookmarkWithNotExistingBookmark() {
        // given
        Member saveMember = memberRepository.save(createMember("test@test.com"));
        Post savePost = postRepository.save(createPost(saveMember));

        // when & then
        Assertions.assertThatThrownBy(() -> bookmarkService.cancelBookmark(saveMember, savePost))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOOKMARK_NOT_FOUND);
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

    private CommunityPost createPost(Member member){
        return CommunityPost.builder()
                .member(member)
                .title("Sample Title")
                .bodyContent("Sample Body Content")
                .viewCount(0L)
                .likeCount(0L)
                .bookmarkCount(0L)
                .isEditing(false)
                .build();
    }
}