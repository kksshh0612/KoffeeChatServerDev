package teamkiim.koffeechat.post.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.bookmark.service.BookmarkService;
import teamkiim.koffeechat.file.service.FileService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.community.domain.CommunityPost;
import teamkiim.koffeechat.post.community.service.dto.request.ModifyCommunityPostServiceRequest;
import teamkiim.koffeechat.post.community.service.dto.request.SaveCommunityPostServiceRequest;
import teamkiim.koffeechat.post.community.service.dto.request.SaveVoteServiceRequest;
import teamkiim.koffeechat.post.community.service.dto.response.CommentInfoDto;
import teamkiim.koffeechat.post.community.service.dto.response.CommunityPostListResponse;
import teamkiim.koffeechat.post.community.service.dto.response.CommunityPostResponse;
import teamkiim.koffeechat.post.community.repository.CommunityPostRepository;
import teamkiim.koffeechat.post.community.service.dto.response.VoteResponse;
import teamkiim.koffeechat.postlike.domain.PostLike;
import teamkiim.koffeechat.postlike.repository.PostLikeRepository;
import teamkiim.koffeechat.vote.domain.Vote;
import teamkiim.koffeechat.vote.repository.VoteRepository;
import teamkiim.koffeechat.vote.service.VoteService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkService bookmarkService;
    private final VoteRepository voteRepository;
    private final VoteService voteService;

    /**
     * 게시글 최초 임시 저장
     *
     * @param memberId 작성자 PK
     * @return Long 게시글 PK
     */
    @Transactional
    public ResponseEntity<?> saveInitCommunityPost(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = CommunityPost.builder()
                .member(member)
                .isEditing(true)
                .build();

        CommunityPost saveCommunityPost = communityPostRepository.save(communityPost);

        return ResponseEntity.ok(saveCommunityPost.getId());
    }

    /**
     * 커뮤니티 게시글 작성 취소
     *
     * @param postId 게시글 PK
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> cancelWriteCommunityPost(Long postId) {

        CommunityPost devPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        fileService.deleteImageFiles(devPost);

        communityPostRepository.delete(devPost);

        return ResponseEntity.ok("게시글 삭제 완료");
    }

    /**
     * 게시글 저장 : 투표 x
     *
     * @param saveCommunityPostServiceRequest 게시글 저장 dto
     * @return CommunityPostResponse
     */
    @Transactional
    public ResponseEntity<?> saveCommunityPostWithoutVote(SaveCommunityPostServiceRequest saveCommunityPostServiceRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(saveCommunityPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        communityPost.completeCommunityPost(saveCommunityPostServiceRequest.getTitle(), saveCommunityPostServiceRequest.getBodyContent());

        fileService.deleteImageFiles(saveCommunityPostServiceRequest.getFileIdList(), communityPost);

        List<CommentInfoDto> commentInfoDtoList = communityPost.getCommentList().stream()
                .map(CommentInfoDto::of).collect(Collectors.toList());

        return ResponseEntity.ok(CommunityPostResponse.of(communityPost, commentInfoDtoList,null, memberId, false, false));

    }

    /**
     * 게시글 저장 : 투표 o
     *
     * @param saveCommunityPostServiceRequest 게시글 저장 dto
     * @return CommunityPostResponse
     */
    @Transactional
    public ResponseEntity<?> saveCommunityPostWithVote(SaveCommunityPostServiceRequest saveCommunityPostServiceRequest,
                                               SaveVoteServiceRequest saveVoteServiceRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(saveCommunityPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        communityPost.completeCommunityPost(saveCommunityPostServiceRequest.getTitle(), saveCommunityPostServiceRequest.getBodyContent());

        fileService.deleteImageFiles(saveCommunityPostServiceRequest.getFileIdList(), communityPost);

        List<CommentInfoDto> commentInfoDtoList = communityPost.getCommentList().stream()
                .map(CommentInfoDto::of).collect(Collectors.toList());

        Vote savedVote=voteService.saveVote(saveVoteServiceRequest, saveCommunityPostServiceRequest.getId());  //투표 저장

        return ResponseEntity.ok(CommunityPostResponse.of(communityPost, commentInfoDtoList, VoteResponse.of(savedVote), memberId, false, false));

    }

    /**
     * 게시글 목록 조회
     *
     * @param page 페이지 번호 ( ex) 0, 1,,,, )
     * @param size 페이지 당 조회할 데이터 수
     * @return List<CommunityPostListResponse>
     */
    public ResponseEntity<?> findCommunityPostList(int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        List<CommunityPost> communityPostList = communityPostRepository.findAllCompletePost(pageRequest).getContent();

        List<CommunityPostListResponse> communityPostResponseList = communityPostList.stream()
                .map(CommunityPostListResponse::of).collect(Collectors.toList());

        return ResponseEntity.ok(communityPostResponseList);
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId postId 게시글 PK
     * @return CommunityPostResponse
     */
    public ResponseEntity<?> findPost(Long postId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<CommentInfoDto> commentInfoDtoList = communityPost.getCommentList().stream()
                .map(CommentInfoDto::of).collect(Collectors.toList());

        Optional<Vote> vote = voteRepository.findByPost(communityPost);
        VoteResponse voteResponse;
        if (vote.isPresent()) {
            voteResponse = VoteResponse.of(vote.get());
        } else {
            voteResponse= null;
        }

        boolean isMemberLiked;
        Optional<PostLike> postLike = postLikeRepository.findByPostAndMember(communityPost, member);

        if (postLike.isPresent()) isMemberLiked = true;
        else isMemberLiked = false;

        boolean isMemberBookmarked = bookmarkService.isMemberBookmarked(member, communityPost);

        return ResponseEntity.ok(CommunityPostResponse.of(communityPost, commentInfoDtoList, voteResponse, memberId, isMemberLiked, isMemberBookmarked));
    }

    /**
     * 게시글 수정
     *
     * @param modifyCommunityPostServiceRequest 게시글 수정 dto
     * @return CommunityPostResponse
     */
    public ResponseEntity<?> modifyPost(ModifyCommunityPostServiceRequest modifyCommunityPostServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(modifyCommunityPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        communityPost.modify(modifyCommunityPostServiceRequest.getTitle(), modifyCommunityPostServiceRequest.getBodyContent());

        List<CommentInfoDto> commentInfoDtoList = communityPost.getCommentList().stream()
                .map(CommentInfoDto::of).collect(Collectors.toList());

        Optional<Vote> vote = voteRepository.findByPost(communityPost);
        VoteResponse voteResponse;
        if (vote.isPresent()) {
            voteResponse = VoteResponse.of(vote.get());
        } else {
            voteResponse= null;
        }

        boolean isMemberLiked;
        Optional<PostLike> postLike = postLikeRepository.findByPostAndMember(communityPost, member);

        if (postLike.isPresent()) isMemberLiked = true;
        else isMemberLiked = false;

        boolean isMemberBookmarked = bookmarkService.isMemberBookmarked(member, communityPost);

        return ResponseEntity.ok(CommunityPostResponse.of(communityPost, commentInfoDtoList, voteResponse, memberId, isMemberLiked, isMemberBookmarked));
    }
}
