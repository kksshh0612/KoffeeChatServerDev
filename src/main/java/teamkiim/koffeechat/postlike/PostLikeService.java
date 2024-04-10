package teamkiim.koffeechat.postlike;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.member.Member;
import teamkiim.koffeechat.member.MemberRepository;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.post.PostRepository;

import java.util.Optional;

/**
 * 게시글 좋아요 기능 관련 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    //회원이 좋아요 버튼을 눌렀을때
    @Transactional
    public void toggleLike(Long memberId, Long postId) {
        Optional<PostLike> existingLike = postLikeRepository.findByMemberIdAndPostId(memberId, postId);
        Post findPost = postRepository.findOne(postId);  //게시글 조회
        if (existingLike.isPresent()) {  //회원이 게시글에 대해 좋아요를 취소한 경우
            postLikeRepository.delete(existingLike.get());  //postLike 엔티티 삭제
            //게시글 likecount--
            findPost.removeLike();
        }else{  //회원이 게시글에 대해 좋아요 한 경우
            PostLike postLike= new PostLike();
            Member findMember = memberRepository.findById(memberId);  //좋아요 누른 회원 조회
            postLike.create(findMember, findPost);
            postLikeRepository.save(postLike); //새로운 좋아요 생성
            //likecount 변경
            findPost.addLike();
        }
    }
}
