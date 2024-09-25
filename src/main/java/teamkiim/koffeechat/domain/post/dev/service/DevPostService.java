package teamkiim.koffeechat.domain.post.dev.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.bookmark.service.BookmarkService;
import teamkiim.koffeechat.domain.file.service.FileService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.domain.post.common.domain.SortCategory;
import teamkiim.koffeechat.domain.post.common.dto.response.CommentInfoDto;
import teamkiim.koffeechat.domain.post.common.dto.response.TagInfoDto;
import teamkiim.koffeechat.domain.post.common.service.PostService;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.dto.request.ModifyDevPostServiceRequest;
import teamkiim.koffeechat.domain.post.dev.dto.request.SaveDevPostServiceRequest;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostSearchListResponse;
import teamkiim.koffeechat.domain.post.dev.repository.DevPostRepository;
import teamkiim.koffeechat.domain.postlike.service.PostLikeService;
import teamkiim.koffeechat.domain.tag.service.TagService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;

/**
 * 개발 게시글 관련 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevPostService {

    private final DevPostRepository devPostRepository;
    private final MemberRepository memberRepository;

    private final FileService fileService;
    private final PostLikeService postLikeService;
    private final BookmarkService bookmarkService;
    private final NotificationService notificationService;
    private final PostService postService;
    private final TagService tagService;

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
     */
    @Transactional
    public void cancelWriteDevPost(Long postId) {

        DevPost devPost = devPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        fileService.deleteImageFiles(devPost);

        devPostRepository.delete(devPost);
    }

    /**
     * 게시글 저장
     *
     * @param saveDevPostServiceRequest 게시글 저장 dto
     */
    @Transactional
    public void saveDevPost(SaveDevPostServiceRequest saveDevPostServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = devPostRepository.findById(saveDevPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        tagService.addTags(devPost, saveDevPostServiceRequest.getTagContentList());  //해시태그 추가

        devPost.completeDevPost(saveDevPostServiceRequest.getTitle(), saveDevPostServiceRequest.getBodyContent(),
                saveDevPostServiceRequest.getVisualData(), saveDevPostServiceRequest.getSkillCategoryList());

        fileService.deleteImageFiles(saveDevPostServiceRequest.getFileIdList(), devPost);

        notificationService.createPostNotification(member, devPost);  //팔로워들에게 알림 발송
    }


    /**
     * 게시글 목록 조회 (필터: 기술 카테고리, 태그)
     *
     * @param sortType               정렬 기준 (최신 | 좋아요순 | 조회순)
     * @param page                   페이지 번호 ( ex) 0, 1,,,, )
     * @param size                   페이지 당 조회할 데이터 수
     * @param childSkillCategoryList 검색된 기술 카테고리들
     * @param tagContents            검색된 태그들
     * @return DevPostSearchListResponse
     */
    public DevPostSearchListResponse getDevPostList(SortCategory sortType, int page, int size,
                                                    List<ChildSkillCategory> childSkillCategoryList, List<String> tagContents) {

        PageRequest pageRequest = postService.sortBySortCategory(sortType, "id", "likeCount", "viewCount", page, size);

        Page<DevPost> devPostList = searchFilter(childSkillCategoryList, tagContents, pageRequest);

        return DevPostSearchListResponse.of(devPostList.getTotalElements(), devPostList);
    }

    private Page<DevPost> searchFilter(List<ChildSkillCategory> childSkillCategoryList, List<String> tagContents, PageRequest pageRequest) {

        if (childSkillCategoryList == null && tagContents == null) { //전체 게시글
            return devPostRepository.findAllCompletePost(pageRequest);
        } else if (childSkillCategoryList == null) {  //태그로만 검색
            return devPostRepository.findAllCompletePostByTags(tagContents, pageRequest);
        } else if (tagContents == null) {  //기술 카테고리로만 검색
            return devPostRepository.findAllCompletePostBySkillCategory(childSkillCategoryList, pageRequest);
        } else {  //기술 카테고리, 태그로 검색
            return devPostRepository.findAllCompletePostBySkillCategoryAndTags(childSkillCategoryList, tagContents, pageRequest);
        }
    }

    /**
     * 제목으로 게시글 검색
     *
     * @param keyword 검색된 내용
     * @param page    페이지 번호 ( ex) 0, 1,,,, )
     * @param size    페이지 당 조회할 데이터 수
     * @return DevPostSearchListResponse
     */
    public DevPostSearchListResponse search(String keyword, SortCategory sortType, int page, int size) {
        PageRequest pageRequest = postService.sortBySortCategory(sortType, "id", "likeCount", "viewCount", page, size);
        Page<DevPost> devPostList = devPostRepository.findAllCompletePostByKeyword(keyword, pageRequest);

        return DevPostSearchListResponse.of(devPostList.getTotalElements(), devPostList);
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
                .map(comment -> CommentInfoDto.of(comment, memberId)).toList();

        List<TagInfoDto> tagInfoDtoList = devPost.getPostTagList().stream()
                .map(postTag -> TagInfoDto.of(postTag.getTag())).toList();

        boolean isMemberLiked = postLikeService.isMemberLiked(devPost, member);
        boolean isMemberBookmarked = bookmarkService.isMemberBookmarked(member, devPost);
        boolean isMemberWritten = memberId.equals(devPost.getMember().getId());

        //글 작성자 이외의 회원이 글을 읽었을 때 조회수 관리
        if (!isMemberWritten) {
            postService.viewPost(devPost, request);
        }

        return DevPostResponse.of(devPost, tagInfoDtoList, commentInfoDtoList, isMemberLiked, isMemberBookmarked, isMemberWritten);
    }

    /**
     * 게시글 수정
     *
     * @param modifyDevPostServiceRequest 게시글 수정 dto
     */
    @Transactional
    public void modifyPost(ModifyDevPostServiceRequest modifyDevPostServiceRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = devPostRepository.findById(modifyDevPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        tagService.updateTags(devPost, modifyDevPostServiceRequest.getTagContentList());  //해시태그 수정

        devPost.modify(modifyDevPostServiceRequest.getTitle(), modifyDevPostServiceRequest.getBodyContent(),
                modifyDevPostServiceRequest.getVisualData(), modifyDevPostServiceRequest.combineSkillCategory());

    }
}
