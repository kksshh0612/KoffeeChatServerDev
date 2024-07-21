package teamkiim.koffeechat.post.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.post.common.repository.PostRepository;
import teamkiim.koffeechat.postlike.service.PostLikeService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeService postLikeService;

    /**
     * 게시물 좋아요
     * @param postId 게시물 PK
     * @param memberId 회원 PK
     * @return Long -> 게시물 좋아요 수
     */
    @Transactional
    public ResponseEntity<?> like(Long postId, Long memberId){

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(postLikeService.isMemberLiked(post, member)){        // 이미 좋아요 눌렀으면
            postLikeService.cancelLike(post, member);
            post.removeLike();
        }
        else{                                                   // 좋아요 누르지 않았으면
            postLikeService.like(post, member);
            post.addLike();
        }

        return ResponseEntity.ok(post.getLikeCount());
    }

    /**
     * 게시글 삭제 (soft delete)
     * @param postId 삭제할 게시글 PK
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> softDelete(Long postId){

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.delete();

        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

}
