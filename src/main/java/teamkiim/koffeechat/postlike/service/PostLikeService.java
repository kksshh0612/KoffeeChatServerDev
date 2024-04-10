package teamkiim.koffeechat.postlike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.repository.MemberRepository;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.post.PostRepository;
import teamkiim.koffeechat.postlike.domain.PostLike;
import teamkiim.koffeechat.postlike.domain.repository.PostLikeRepository;

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

        if (existingLike.isPresent()) {  // PostLike 가 이미 존재하는 경우 -> 좋아요 취소 처리
            postLikeRepository.delete(existingLike.get());  //postLike 엔티티 삭제
            //게시글 likecount--
            findPost.removeLike();
        }else{                          // PostLike가 존재하지 않는 경우 -> 좋아요 처리
            PostLike postLike= new PostLike();
            Optional<Member> findMember = memberRepository.findById(memberId);  //좋아요 누른 회원 조회
            postLike.create(findMember.get(), findPost);
            postLikeRepository.save(postLike); //새로운 좋아요 생성
            //likecount 변경
            findPost.addLike();
        }
    }
}
