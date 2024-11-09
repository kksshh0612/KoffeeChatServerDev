package teamkiim.koffeechat.domain.post.community.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import teamkiim.koffeechat.domain.post.community.controller.dto.SaveCommunityPostRequest;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;
import teamkiim.koffeechat.domain.post.community.dto.request.ModifyCommunityPostServiceRequest;
import teamkiim.koffeechat.domain.post.community.dto.request.SaveCommunityPostServiceRequest;
import teamkiim.koffeechat.domain.post.community.dto.response.CommunityPostListResponse;
import teamkiim.koffeechat.domain.post.community.dto.response.CommunityPostResponse;
import teamkiim.koffeechat.domain.post.community.dto.response.VoteItemResponse;
import teamkiim.koffeechat.domain.post.community.dto.response.VoteResponse;
import teamkiim.koffeechat.domain.post.community.repository.CommunityPostRepository;
import teamkiim.koffeechat.domain.postlike.service.PostLikeService;
import teamkiim.koffeechat.domain.tag.service.TagService;
import teamkiim.koffeechat.domain.vote.domain.Vote;
import teamkiim.koffeechat.domain.vote.dto.request.ModifyVoteServiceRequest;
import teamkiim.koffeechat.domain.vote.repository.VoteRepository;
import teamkiim.koffeechat.domain.vote.service.VoteService;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;
    private final PostFileService postFileService;
    private final PostLikeService postLikeService;
    private final BookmarkService bookmarkService;
    private final VoteRepository voteRepository;
    private final VoteService voteService;
    private final NotificationService notificationService;
    private final PostService postService;
    private final TagService tagService;
    private final CommentService commentService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 게시글 최초 임시 저장
     *
     * @param memberId 작성자 PK
     * @return 암호화된 게시글 PK
     */
    @Transactional
    public Long saveInitCommunityPost(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = CommunityPost.builder()
                .member(member)
                .isEditing(true)
                .build();

        CommunityPost saveCommunityPost = communityPostRepository.save(communityPost);

        return saveCommunityPost.getId();
    }

    /**
     * 커뮤니티 게시글 작성 취소
     *
     * @param postId 게시글 PK
     */
    @Transactional
    public void cancelWriteCommunityPost(Long postId) {

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        postFileService.deleteImageFiles(communityPost);

        communityPostRepository.delete(communityPost);
    }

    /**
     * 게시글 저장
     *
     * @param postRequest 게시글 저장 dto
     */
    @Transactional
    public void saveCommunityPost(Long postId, SaveCommunityPostRequest postRequest, Long memberId,
                                  LocalDateTime createdTime) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        SaveCommunityPostServiceRequest postServiceRequest = postRequest.toPostServiceRequest();

        if (communityPost.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        if (!communityPost.isEditing()) {
            throw new CustomException(ErrorCode.POST_ALREADY_EXIST);
        }

        tagService.addTags(communityPost, postServiceRequest.getTagList());  //해시태그 추가
        communityPost.completeCommunityPost(postServiceRequest.getTitle(), postServiceRequest.getBodyContent(),
                createdTime);

        postFileService.deleteImageFiles(postServiceRequest.getFileUrlList(), communityPost);

        if (postRequest.getSaveVoteRequest() != null) {  //투표 저장
            voteService.saveVote(postRequest.toVoteServiceRequest(), postId);
        }

        //팔로워들에게 알림 발송
        notificationService.createPostNotification(member, communityPost);
    }

    /**
     * 게시글 목록 조회 (필터 : 태그)
     *
     * @param sortType    정렬 기준 (최신 | 좋아요순 | 조회순)
     * @param page        페이지 번호 ( ex) 0, 1,,,, )
     * @param size        페이지 당 조회할 데이터 수
     * @param keyword     제목 검색
     * @param tagContents 검색된 태그들
     * @return CommunityPostSearchListResponse
     */
    public List<CommunityPostListResponse> findCommunityPostList(SortType sortType, int page, int size,
                                                                 String keyword, List<String> tagContents) {

        PageRequest pageRequest = postService.sortBySortCategory(sortType, "id", "likeCount", "viewCount", page, size);

        Page<CommunityPost> communityPostList = searchFilter(keyword, tagContents, pageRequest);

        return communityPostList.stream().map(post -> {
            List<TagInfoDto> tagList = tagService.toTagInfoDtoList(post);
            return CommunityPostListResponse.of(aesCipherUtil.encrypt(post.getId()), post, tagList);
        }).toList();
    }

    private Page<CommunityPost> searchFilter(String keyword, List<String> tagContents, PageRequest pageRequest) {
        if (keyword == null && tagContents == null) { //전체 게시글
            return communityPostRepository.findAllCompletePost(pageRequest);
        }
        if (keyword == null) {  // 태그로만 검색
            return communityPostRepository.findAllCompletePostByTags(tagContents, pageRequest);
        }
        if (tagContents == null) {  // 제목으로만 검색
            return communityPostRepository.findAllCompletePostByKeyword(keyword, pageRequest);
        }
        return communityPostRepository.findAllCompletePostByKeywordAndTags(keyword, tagContents, pageRequest);
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId 게시글 PK
     * @return CommunityPostResponse
     */
    @Transactional
    public CommunityPostResponse findPost(Long postId, Long memberId, HttpServletRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (communityPost.isEditing() || communityPost.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        List<CommentInfoDto> commentInfoDtoList = commentService.toCommentDtoList(communityPost, memberId);

        Optional<Vote> vote = voteRepository.findByPost(communityPost);
        VoteResponse voteResponse = null;
        if (vote.isPresent()) {
            boolean isMemberVoted = voteService.hasMemberVoted(vote.get(), member);
            List<VoteItemResponse> voteItemResponseList = vote.get().getVoteItems().stream()
                    .map(voteItem -> VoteItemResponse.of(aesCipherUtil.encrypt(voteItem.getId()), voteItem,
                            isMemberVoted)).toList();
            voteResponse = VoteResponse.of(vote.get(), voteItemResponseList, isMemberVoted);
        }

        List<TagInfoDto> tagInfoDtoList = tagService.toTagInfoDtoList(communityPost);

        boolean isMemberLiked = postLikeService.isMemberLiked(communityPost, member);
        boolean isMemberBookmarked = bookmarkService.isMemberBookmarked(member, communityPost);
        boolean isMemberWritten = memberId.equals(communityPost.getMember().getId());

        //글 작성자 이외의 회원이 글을 읽었을 때 조회수 관리
        if (!isMemberWritten) {
            postService.viewPost(communityPost, request);
        }
        String encryptedPostId = aesCipherUtil.encrypt(communityPost.getId());
        String encryptedMemberId = aesCipherUtil.encrypt(communityPost.getMember().getId());

        return CommunityPostResponse.of(encryptedPostId, communityPost, encryptedMemberId, tagInfoDtoList,
                commentInfoDtoList, voteResponse, isMemberLiked,
                isMemberBookmarked, isMemberWritten);
    }

    /**
     * 게시글 수정
     *
     * @param modifyCommunityPostServiceRequest 게시글 수정 dto
     */
    @Transactional
    public void modifyPost(Long postId, ModifyCommunityPostServiceRequest modifyCommunityPostServiceRequest,
                           ModifyVoteServiceRequest modifyVoteServiceRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (communityPost.isEditing() || communityPost.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        communityPost.modifyCommunityPost(modifyCommunityPostServiceRequest.getTitle(),
                modifyCommunityPostServiceRequest.getBodyContent());

        postFileService.deleteImageFiles(modifyCommunityPostServiceRequest.getFileUrlList(), communityPost);

        //투표가 있다면 투표 내용 수정
        Optional<Vote> vote = voteRepository.findByPost(communityPost);
        vote.ifPresent(value -> voteService.modifyVote(modifyVoteServiceRequest, value));

        tagService.updateTags(communityPost, modifyCommunityPostServiceRequest.getTagContentList());
    }

}
