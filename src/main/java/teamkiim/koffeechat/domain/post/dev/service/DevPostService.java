package teamkiim.koffeechat.domain.post.dev.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.bookmark.service.BookmarkService;
import teamkiim.koffeechat.domain.comment.service.CommentService;
import teamkiim.koffeechat.domain.file.service.PostFileService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.domain.post.common.domain.SortType;
import teamkiim.koffeechat.domain.post.common.dto.response.CommentInfoDto;
import teamkiim.koffeechat.domain.post.common.dto.response.TagInfoDto;
import teamkiim.koffeechat.domain.post.common.service.PostService;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.dto.request.ModifyDevPostServiceRequest;
import teamkiim.koffeechat.domain.post.dev.dto.request.SaveDevPostServiceRequest;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostListRelatedSkill;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.domain.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.domain.post.dev.repository.DevPostRepository;
import teamkiim.koffeechat.domain.postlike.service.PostLikeService;
import teamkiim.koffeechat.domain.tag.service.TagService;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

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
    private final TagService tagService;
    private final CommentService commentService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 게시글 최초 임시 저장
     *
     * @param memberId 작성자 PK
     * @return String 암호화된 게시글 PK
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

        postFileService.deleteImageFiles(devPost);

        devPostRepository.delete(devPost);
    }

    /**
     * 게시글 저장
     *
     * @param saveDevPostServiceRequest 게시글 저장 dto
     */
    @Transactional
    public void saveDevPost(Long postId, SaveDevPostServiceRequest saveDevPostServiceRequest, Long memberId,
                            LocalDateTime createdTime) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = devPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        tagService.addTags(devPost, saveDevPostServiceRequest.getTagContentList());  //해시태그 추가

        devPost.completeDevPost(saveDevPostServiceRequest.getTitle(), saveDevPostServiceRequest.getBodyContent(),
                saveDevPostServiceRequest.getVisualData(), saveDevPostServiceRequest.getSkillCategoryList(),
                createdTime);

        postFileService.deleteImageFiles(saveDevPostServiceRequest.getFileUrlList(), devPost);

        notificationService.createPostNotification(member, devPost);  //팔로워들에게 알림 발송
        notificationService.createSkillPostNotification(saveDevPostServiceRequest.getSkillCategoryList(),
                devPost);  //기술 채팅방 사용자들에게 알림 발송
    }


    /**
     * 게시글 목록 조회 (필터: 기술 카테고리, 태그)
     *
     * @param sortType               정렬 기준 (최신 | 좋아요순 | 조회순)
     * @param page                   페이지 번호 ( ex) 0, 1,,,, )
     * @param size                   페이지 당 조회할 데이터 수
     * @param keyword                제목에 대한 키워드 검색
     * @param childSkillCategoryList 검색된 기술 카테고리들
     * @param tagContents            검색된 태그들
     * @return DevPostSearchListResponse
     */
    public List<DevPostListResponse> getDevPostList(String sortType, int page, int size,
                                                    String keyword, List<ChildSkillCategory> childSkillCategoryList,
                                                    List<String> tagContents) {

        PageRequest pageRequest = postService.sortBySortCategory(SortType.valueOf(sortType), "id", "likeCount",
                "viewCount", page, size);

        Page<DevPost> devPostList = searchFilter(keyword, childSkillCategoryList, tagContents, pageRequest);

        return devPostList.stream().map(post -> {
            List<TagInfoDto> tagList = tagService.toTagInfoDtoList(post);
            return DevPostListResponse.of(aesCipherUtil.encrypt(post.getId()), post, tagList);
        }).toList();
    }

    private Page<DevPost> searchFilter(String keyword, List<ChildSkillCategory> childSkillCategoryList,
                                       List<String> tagContents, PageRequest pageRequest) {

        if (keyword == null && childSkillCategoryList == null && tagContents == null) { //전체 게시글
            return devPostRepository.findAllCompletePost(pageRequest);
        }
        if (keyword == null && childSkillCategoryList == null) {  // 태그로만 검색
            return devPostRepository.findAllCompletePostByTags(tagContents, pageRequest);
        }
        if (keyword == null && tagContents == null) {  // 기술 카테고리로만 검색
            return devPostRepository.findAllCompletePostBySkillCategoryList(childSkillCategoryList, pageRequest);
        }
        if (childSkillCategoryList == null && tagContents == null) {  // 제목으로만 검색
            return devPostRepository.findAllCompletePostByKeyword(keyword, pageRequest);
        }
        if (keyword == null) {   //기술 카테고리, 태그로 검색
            return devPostRepository.findAllCompletePostBySkillCategoryAndTags(childSkillCategoryList, tagContents,
                    pageRequest);
        }
        if (childSkillCategoryList == null) {   //제목, 태그로 검색
            return devPostRepository.findAllCompletePostByKeywordAndTags(keyword, tagContents, pageRequest);
        }
        if (tagContents == null) {  //제목, 기술 카테고리로 검색
            return devPostRepository.findAllCompletePostByKeywordAndSkillCategory(keyword, childSkillCategoryList,
                    pageRequest);
        }
        return devPostRepository.findAllCompletePost(pageRequest);  //전체 게시글
    }

    /**
     * 기술 채팅방 연관 게시글 커서 기반 페이징 조회
     *
     * @param childSkillCategory 하위 기술 카테고리
     * @param cursorId           마지막 게시글 PK
     * @param size
     * @return List<DevPostListRelatedSkill>
     */
    public List<DevPostListRelatedSkill> findDevPostListByChildSkillCategory(String childSkillCategory,
                                                                             String cursorId, int size) {

        Long decryptCursorId = null;

        if (cursorId != null) {
            decryptCursorId = aesCipherUtil.decrypt(cursorId);
        }

        // cursor 기반 페이지 조회
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<DevPost> devPostList = devPostRepository.findAllByChildSkillCategory(
                ChildSkillCategory.valueOf(childSkillCategory), decryptCursorId, pageRequest).getContent();

        return devPostList.stream()
                .map(devPost -> DevPostListRelatedSkill.of(
                        devPost, aesCipherUtil.encrypt(devPost.getId()), devPost.getMember().getProfileImageUrl()))
                .collect(Collectors.toList());
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

        List<CommentInfoDto> commentInfoDtoList = commentService.toCommentDtoList(devPost, memberId);

        List<TagInfoDto> tagInfoDtoList = tagService.toTagInfoDtoList(devPost);

        boolean isMemberLiked = postLikeService.isMemberLiked(devPost, member);
        boolean isMemberBookmarked = bookmarkService.isMemberBookmarked(member, devPost);
        boolean isMemberWritten = memberId.equals(devPost.getMember().getId());

        //글 작성자 이외의 회원이 글을 읽었을 때 조회수 관리
        if (!isMemberWritten) {
            postService.viewPost(devPost, request);
        }

        return DevPostResponse.of(aesCipherUtil.encrypt(devPost.getId()), devPost,
                aesCipherUtil.encrypt(devPost.getMember().getId()), tagInfoDtoList, commentInfoDtoList, isMemberLiked,
                isMemberBookmarked, isMemberWritten);
    }

    /**
     * 게시글 수정
     *
     * @param modifyDevPostServiceRequest 게시글 수정 dto
     */
    @Transactional
    public void modifyPost(Long postId, ModifyDevPostServiceRequest modifyDevPostServiceRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = devPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        tagService.updateTags(devPost, modifyDevPostServiceRequest.getTagContentList());  //해시태그 수정

        devPost.modify(modifyDevPostServiceRequest.getTitle(), modifyDevPostServiceRequest.getBodyContent(),
                modifyDevPostServiceRequest.getVisualData(), modifyDevPostServiceRequest.getSkillCategoryList());

        postFileService.deleteImageFiles(modifyDevPostServiceRequest.getFileUrlList(), devPost);
    }
}
