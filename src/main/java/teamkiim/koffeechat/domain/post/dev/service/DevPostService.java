package teamkiim.koffeechat.domain.post.dev.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.bookmark.service.BookmarkService;
import teamkiim.koffeechat.domain.file.service.PostFileService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.domain.post.common.service.PostService;
import teamkiim.koffeechat.domain.post.community.dto.response.CommentInfoDto;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.dto.request.ModifyDevPostServiceRequest;
import teamkiim.koffeechat.domain.post.dev.dto.request.SaveDevPostServiceRequest;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.domain.post.dev.repository.DevPostRepository;
import teamkiim.koffeechat.domain.postlike.service.PostLikeService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 개발 게시글 관련 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevPostService {

    private final DevPostRepository devPostRepository;
    private final MemberRepository memberRepository;
    private final PostFileService postFileService;
    private final PostLikeService postLikeService;
    private final BookmarkService bookmarkService;
    private final NotificationService notificationService;
    private final PostService postService;

    /**
     * 게시글 최초 임시 저장
     *
     * @param memberId 작성자 PK
     * @return Long 게시글 PK
     */
    @Transactional
    public Long saveInitDevPost(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = DevPost.builder()
                .member(member)
                .isEditing(true)
                .build();

        DevPost saveDevPost = devPostRepository.save(devPost);

        return saveDevPost.getId();
    }

    /**
     * 개발 게시글 작성 취소
     *
     * @param postId 게시글 PK
     * @return ok
     */
    @Transactional
    public void cancelWriteDevPost(Long postId) {

        DevPost devPost = devPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        postFileService.deleteImageFiles(devPost);

        devPostRepository.delete(devPost);
    }

    /**
     * 게시글 저장
     *
     * @param saveDevPostServiceRequest 게시글 저장 dto
     * @return DevPostResponse
     */
    @Transactional
    public void saveDevPost(SaveDevPostServiceRequest saveDevPostServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = devPostRepository.findById(saveDevPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        devPost.completeDevPost(saveDevPostServiceRequest.getTitle(), saveDevPostServiceRequest.getBodyContent(),
                saveDevPostServiceRequest.getVisualData(), saveDevPostServiceRequest.getSkillCategoryList());

        postFileService.deleteImageFiles(saveDevPostServiceRequest.getFileIdList(), devPost);

        notificationService.createPostNotification(member, devPost);  //팔로워들에게 알림 발송
    }

    /**
     * 게시글 목록 조회
     *
     * @param page 페이지 번호 ( ex) 0, 1,,,, )
     * @param size 페이지 당 조회할 데이터 수
     * @return List<DevPostListResponse>
     */
    public List<DevPostListResponse> getDevPostList(int page, int size, List<ChildSkillCategory> childSkillCategoryList) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        List<DevPost> devPostList = childSkillCategoryList == null ?
                devPostRepository.findAllCompletePostBySkillCategory(pageRequest).getContent() : devPostRepository.findAllCompletePostBySkillCategory(childSkillCategoryList, pageRequest).getContent();

        return devPostList.stream().map(DevPostListResponse::of).toList();
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId 게시글 PK
     * @return DevPostResponse
     */
    @Transactional
    public DevPostResponse findPost(Long postId, Long memberId, HttpServletRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = devPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<CommentInfoDto> commentInfoDtoList = devPost.getCommentList().stream()
                .map(comment -> CommentInfoDto.of(comment, memberId)).collect(Collectors.toList());

        boolean isMemberLiked = postLikeService.isMemberLiked(devPost, member);
        boolean isMemberBookmarked = bookmarkService.isMemberBookmarked(member, devPost);
        boolean isMemberWritten = memberId.equals(devPost.getMember().getId());

        //글 작성자 이외의 회원이 글을 읽었을 때 조회수 관리
        if (!isMemberWritten) {
            postService.viewPost(devPost, request);
        }

        return DevPostResponse.of(devPost, commentInfoDtoList, isMemberLiked, isMemberBookmarked, isMemberWritten);
    }

    /**
     * 게시글 수정
     *
     * @param modifyDevPostServiceRequest 게시글 수정 dto
     * @return DevPostResponse
     */
    @Transactional
    public void modifyPost(ModifyDevPostServiceRequest modifyDevPostServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = devPostRepository.findById(modifyDevPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        devPost.modify(modifyDevPostServiceRequest.getTitle(), modifyDevPostServiceRequest.getBodyContent(),
                modifyDevPostServiceRequest.getVisualData(), modifyDevPostServiceRequest.combineSkillCategory());

        boolean isMemberLiked = postLikeService.isMemberLiked(devPost, member);
        boolean isMemberBookmarked = bookmarkService.isMemberBookmarked(member, devPost);
    }
}
