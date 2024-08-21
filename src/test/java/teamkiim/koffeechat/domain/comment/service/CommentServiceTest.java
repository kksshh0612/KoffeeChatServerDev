package teamkiim.koffeechat.domain.comment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import teamkiim.koffeechat.TestSupport;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.comment.dto.request.CommentServiceRequest;
import teamkiim.koffeechat.domain.comment.dto.request.ModifyCommentServiceRequest;
import teamkiim.koffeechat.domain.comment.repository.CommentRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class CommentServiceTest extends TestSupport {

    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;

    @AfterEach
    void tearDown(){
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("게시글에 댓글을 작성한다.")
    @Test
    void saveComment() {
        // given
        CommentServiceRequest commentServiceRequest = CommentServiceRequest.builder()
                .content("testContent")
                .currDateTime(LocalDateTime.of(2024, 06, 12, 11, 0, 0))
                .build();

        Member saveMember = memberRepository.save(createMember("test@test.com"));
        CommunityPost savePost = postRepository.save(createPost(saveMember));

        // when
        commentService.saveComment(savePost.getId(), commentServiceRequest, saveMember.getId());

        // then
        Post post = postRepository.findByIdWithComments(savePost.getId()).get();
        List<Comment> saveCommentList = commentRepository.findAll();

        // 댓글 자체가 잘 저장되었는지 검증
        Assertions.assertThat(saveCommentList).hasSize(1);
        Assertions.assertThat(saveCommentList.get(0).getContent()).isEqualTo(commentServiceRequest.getContent());
        // post에 댓글이 양방향 연관관계가 잘 맺어졌는지 검증
        Assertions.assertThat(post.getCommentList()).hasSize(1);
        Assertions.assertThat(post.getCommentList().get(0).getContent()).isEqualTo(commentServiceRequest.getContent());
    }

    @DisplayName("존재하지 않는 회원이 댓글 작성을 시도하면 예외가 발생한다.")
    @Test
    void saveCommentWithNoExistingMember() {
        // given
        CommentServiceRequest commentServiceRequest = CommentServiceRequest.builder()
                .content("testContent")
                .currDateTime(LocalDateTime.of(2024, 06, 12, 11, 0, 0))
                .build();

        Member saveMember = memberRepository.save(createMember("test@test.com"));
        CommunityPost savePost = postRepository.save(createPost(saveMember));

        Long notExistMemberId = Long.MAX_VALUE; // 존재하지 않는 ID

        // when & then
        Assertions.assertThatThrownBy(() -> commentService.saveComment(savePost.getId(), commentServiceRequest, notExistMemberId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @DisplayName("존재하지 않는 게시글에 댓글 작성을 시도하면 예외가 발생한다.")
    @Test
    void saveCommentWithNoExistingPost() {
        // given
        CommentServiceRequest commentServiceRequest = CommentServiceRequest.builder()
                .content("testContent")
                .currDateTime(LocalDateTime.of(2024, 06, 12, 11, 0, 0))
                .build();

        Member saveMember = memberRepository.save(createMember("test@test.com"));

        Long notExistPostId = Long.MAX_VALUE; // 존재하지 않는 ID

        // when & then
        Assertions.assertThatThrownBy(() -> commentService.saveComment(notExistPostId, commentServiceRequest, saveMember.getId()))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
    }

    @DisplayName("댓글을 수정한다.")
    @Test
    void modifyComment() {
        // given
        Member saveMember = memberRepository.save(createMember("test@test.com"));
        Post savePost = postRepository.save(createPost(saveMember));

        Comment comment = Comment.builder()
                .post(savePost)
                .member(saveMember)
                .content("testContent")
                .build();

        Comment saveComment = commentRepository.save(comment);

        ModifyCommentServiceRequest modifyCommentServiceRequest = ModifyCommentServiceRequest.builder()
                .id(saveComment.getId())
                .content("commentContent")
                .build();

        // when
        commentService.modifyComment(modifyCommentServiceRequest);

        // then
        Optional<Comment> findComment = commentRepository.findById(saveComment.getId());

        Assertions.assertThat(findComment).isPresent();
        Assertions.assertThat(findComment.get().getContent()).isEqualTo("commentContent");
    }

    @DisplayName("존재하지 않는 댓글을 수정하려 시도하면 예외가 발생한다.")
    @Test
    void modifyCommentWithNoExistingComment() {
        // given
        Long notExistCommentId = Long.MAX_VALUE; // 존재하지 않는 ID

        ModifyCommentServiceRequest modifyCommentServiceRequest = ModifyCommentServiceRequest.builder()
                .id(notExistCommentId)
                .content("commentContent")
                .build();

        // when & then
        Assertions.assertThatThrownBy(() -> commentService.modifyComment(modifyCommentServiceRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);
    }

    @DisplayName("댓글을 삭제한다.")
    @Test
    void deleteComment() {
        // given
        Member saveMember = memberRepository.save(createMember("test@test.com"));
        Post savePost = postRepository.save(createPost(saveMember));

        Comment comment = Comment.builder()
                .post(savePost)
                .member(saveMember)
                .content("testContent")
                .build();

        Comment saveComment = commentRepository.save(comment);

        // when
        commentService.deleteComment(saveComment.getId());

        // then
        List<Comment> findCommentList = commentRepository.findAll();

        Assertions.assertThat(findCommentList).hasSize(0);
    }

    @DisplayName("존재하지 않는 댓글을 삭제 시도하면 예외가 발생한다.")
    @Test
    void deleteCommentWithNoExistingComment() {
        // given
        Long notExistCommentId = Long.MAX_VALUE; // 존재하지 않는 ID

        // when & then
        Assertions.assertThatThrownBy(() -> commentService.deleteComment(notExistCommentId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);
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