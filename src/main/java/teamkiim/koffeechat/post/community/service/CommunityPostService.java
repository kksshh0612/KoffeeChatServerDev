package teamkiim.koffeechat.post.community.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import teamkiim.koffeechat.file.service.FileService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.community.domain.CommunityPost;
import teamkiim.koffeechat.post.community.dto.request.ModifyCommunityPostServiceRequest;
import teamkiim.koffeechat.post.community.dto.request.SaveCommunityPostServiceRequest;
import teamkiim.koffeechat.post.community.dto.response.CommentInfoDto;
import teamkiim.koffeechat.post.community.dto.response.CommunityPostListResponse;
import teamkiim.koffeechat.post.community.dto.response.CommunityPostResponse;
import teamkiim.koffeechat.post.community.repository.CommunityPostRepository;
import teamkiim.koffeechat.post.dev.domain.DevPost;
import teamkiim.koffeechat.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.post.dev.dto.response.ImageFileInfoDto;
import teamkiim.koffeechat.postlike.domain.PostLike;
import teamkiim.koffeechat.postlike.repository.PostLikeRepository;
import teamkiim.koffeechat.postlike.service.PostLikeService;

import java.util.ArrayList;
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
    private final PostLikeService postLikeService;

    /**
     * 게시글 최초 임시 저장
     * @param memberId 작성자 PK
     * @return Long 게시글 PK
     */
    @Transactional
    public ResponseEntity<?> saveInitCommunityPost(Long memberId){

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
     * @param postId 게시글 PK
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> cancelWriteCommunityPost(Long postId){

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        fileService.deleteImageFiles(communityPost);

        communityPostRepository.delete(communityPost);

        return ResponseEntity.ok("게시글 삭제 완료");
    }

    /**
     * 게시글 저장
     * @param saveCommunityPostServiceRequest 게시글 저장 dto
     * @return CommunityPostResponse
     */
    @Transactional
    public ResponseEntity<?> saveCommunityPost(SaveCommunityPostServiceRequest saveCommunityPostServiceRequest, Long memberId){

        CommunityPost communityPost = communityPostRepository.findById(saveCommunityPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        communityPost.completeCommunityPost(saveCommunityPostServiceRequest.getTitle(),
                saveCommunityPostServiceRequest.getBodyContent(), saveCommunityPostServiceRequest.getCurrDateTime());

        fileService.deleteImageFiles(saveCommunityPostServiceRequest.getFileIdList(), communityPost);

        List<CommentInfoDto> commentInfoDtoList = new ArrayList<>();

        return ResponseEntity.ok(CommunityPostResponse.of(communityPost, commentInfoDtoList, memberId, false));

    }

    /**
     * 게시글 목록 조회
     * @param page 페이지 번호 ( ex) 0, 1,,,, )
     * @param size 페이지 당 조회할 데이터 수
     * @return List<CommunityPostListResponse>
     */
    public ResponseEntity<?> findCommunityPostList(int page, int size){

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        List<CommunityPost> communityPostList = communityPostRepository.findAllCompletePost(pageRequest).getContent();

        List<CommunityPostListResponse> communityPostResponseList = communityPostList.stream()
                .map(CommunityPostListResponse::of).collect(Collectors.toList());

        return ResponseEntity.ok(communityPostResponseList);
    }

    /**
     * 게시글 상세 조회
     * @param postId postId 게시글 PK
     * @return CommunityPostResponse
     */
    public ResponseEntity<?> findPost(Long postId, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<CommentInfoDto> commentInfoDtoList = communityPost.getCommentList().stream()
                .map(comment -> CommentInfoDto.of(comment, memberId)).collect(Collectors.toList());

        boolean isMemberLiked = postLikeService.isMemberLiked(communityPost, member);

        return ResponseEntity.ok(CommunityPostResponse.of(communityPost, commentInfoDtoList, memberId, isMemberLiked));
    }

    /**
     * 게시글 수정
     * @param modifyCommunityPostServiceRequest 게시글 수정 dto
     * @return CommunityPostResponse
     */
    public ResponseEntity<?> modifyPost(ModifyCommunityPostServiceRequest modifyCommunityPostServiceRequest, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommunityPost communityPost = communityPostRepository.findById(modifyCommunityPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        communityPost.modify(modifyCommunityPostServiceRequest.getTitle(),
                modifyCommunityPostServiceRequest.getBodyContent(), modifyCommunityPostServiceRequest.getCurrDateTime());

        List<CommentInfoDto> commentInfoDtoList = communityPost.getCommentList().stream()
                .map(comment -> CommentInfoDto.of(comment, memberId)).collect(Collectors.toList());

        boolean isMemberLiked = postLikeService.isMemberLiked(communityPost, member);

        return ResponseEntity.ok(CommunityPostResponse.of(communityPost, commentInfoDtoList, memberId, isMemberLiked));
    }
}
