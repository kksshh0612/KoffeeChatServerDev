package teamkiim.koffeechat.domain.post.community.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
import teamkiim.koffeechat.domain.post.community.controller.dto.SaveCommunityPostRequest;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;
import teamkiim.koffeechat.domain.post.community.dto.request.ModifyCommunityPostServiceRequest;
import teamkiim.koffeechat.domain.post.community.dto.request.SaveCommunityPostServiceRequest;
import teamkiim.koffeechat.domain.post.community.dto.response.CommunityPostListResponse;
import teamkiim.koffeechat.domain.post.community.dto.response.CommunityPostResponse;
import teamkiim.koffeechat.domain.post.community.dto.response.VoteResponse;
import teamkiim.koffeechat.domain.post.community.repository.CommunityPostRepository;
import teamkiim.koffeechat.domain.postlike.service.PostLikeService;
import teamkiim.koffeechat.domain.tag.service.TagService;
import teamkiim.koffeechat.domain.vote.domain.Vote;
import teamkiim.koffeechat.domain.vote.dto.request.ModifyVoteServiceRequest;
import teamkiim.koffeechat.domain.vote.repository.VoteRepository;
import teamkiim.koffeechat.domain.vote.service.VoteService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;
    private final VoteRepository voteRepository;

    private final FileService fileService;
    private final PostLikeService postLikeService;
    private final BookmarkService bookmarkService;
    private final VoteService voteService;
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

        fileService.deleteImageFiles(communityPost);

        communityPostRepository.delete(communityPost);
    }

    /**
     * 게시글 저장
     *
     * @param postRequest 게시글 저장 dto
     */
    @Transactional
    public void saveCommunityPost(Long postId, SaveCommunityPostRequest postRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        SaveCommunityPostServiceRequest postServiceRequest = postRequest.toPostServiceRequest(postId);

        CommunityPost communityPost = communityPostRepository.findById(postServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (communityPost.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        if (!communityPost.isEditing()) {
            throw new CustomException(ErrorCode.POST_ALREADY_EXIST);
        }

        tagService.addTags(communityPost, postServiceRequest.getTagList());  //해시태그 추가

        communityPost.completeCommunityPost(postServiceRequest.getTitle(), postServiceRequest.getBodyContent());

        fileService.deleteImageFiles(postServiceRequest.getFileIdList(), communityPost);

        if (postRequest.getSaveVoteRequest() != null) {  //투표 저장
            voteService.saveVote(postRequest.toVoteServiceRequest(), postServiceRequest.getId());
        }

        //팔로워들에게 알림 발송
        notificationService.createPostNotification(member, communityPost);

    }

    /**
     * 게시글 목록 조회
     *
     * @param sortType 정렬 기준 (최신 | 좋아요순 | 조회순)
     * @param page     페이지 번호 ( ex) 0, 1,,,, )
     * @param size     페이지 당 조회할 데이터 수
     * @return List<CommunityPostListResponse>
     */
    public List<CommunityPostListResponse> findCommunityPostList(SortCategory sortType, int page, int size) {

        PageRequest pageRequest = postService.sortBySortCategory(sortType, "id", "likeCount", "viewCount", page, size);

        List<CommunityPost> communityPostList = communityPostRepository.findAllCompletePost(pageRequest).getContent();

        return communityPostList.stream().map(CommunityPostListResponse::of).toList();
    }

    /**
     * 태그로 게시글 검색
     *
     * @param tagContents 검색된 태그들
     * @param page        페이지 번호 ( ex) 0, 1,,,, )
     * @param size        페이지 당 조회할 데이터 수
     * @return List<CommunityPostListResponse>
     */
    public List<CommunityPostListResponse> searchByTag(List<String> tagContents, SortCategory sortType, int page, int size) {
        PageRequest pageRequest = postService.sortBySortCategory(sortType, "id", "likeCount", "viewCount", page, size);
        List<CommunityPost> communityPostList = communityPostRepository.findAllCompletePostByTags(tagContents, pageRequest).getContent();

        return communityPostList.stream().map(CommunityPostListResponse::of).toList();
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId postId 게시글 PK
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

        List<CommentInfoDto> commentInfoDtoList = communityPost.getCommentList().stream()
                .map(comment -> CommentInfoDto.of(comment, memberId)).toList();

        Optional<Vote> vote = voteRepository.findByPost(communityPost);
        VoteResponse voteResponse = vote.map(value -> VoteResponse.of(value, voteService.hasMemberVoted(value, member))).orElse(null);

        List<TagInfoDto> tagInfoDtoList = communityPost.getPostTagList().stream()
                .map(postTag -> TagInfoDto.of(postTag.getTag())).toList();

        boolean isMemberLiked = postLikeService.isMemberLiked(communityPost, member);
        boolean isMemberBookmarked = bookmarkService.isMemberBookmarked(member, communityPost);
        boolean isMemberWritten = memberId.equals(communityPost.getMember().getId());

        //글 작성자 이외의 회원이 글을 읽었을 때 조회수 관리
        if (!isMemberWritten) postService.viewPost(communityPost, request);

        return CommunityPostResponse.of(communityPost, tagInfoDtoList, commentInfoDtoList, voteResponse, isMemberLiked, isMemberBookmarked, isMemberWritten);
    }

    /**
     * 게시글 수정
     *
     * @param modifyCommunityPostServiceRequest 게시글 수정 dto
     */
    @Transactional
    public void modifyPost(ModifyCommunityPostServiceRequest modifyCommunityPostServiceRequest,
                           ModifyVoteServiceRequest modifyVoteServiceRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(modifyCommunityPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (communityPost.isEditing() || communityPost.isDeleted()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        communityPost.modifyCommunityPost(modifyCommunityPostServiceRequest.getTitle(), modifyCommunityPostServiceRequest.getBodyContent());

        //투표가 있다면 투표 내용 수정
        Optional<Vote> vote = voteRepository.findByPost(communityPost);
        vote.ifPresent(value -> voteService.modifyVote(modifyVoteServiceRequest, value));

        tagService.updateTags(communityPost, modifyCommunityPostServiceRequest.getTagContentList());
    }

}
