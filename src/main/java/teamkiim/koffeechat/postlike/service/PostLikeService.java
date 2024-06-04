package teamkiim.koffeechat.postlike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.postlike.domain.PostLike;
import teamkiim.koffeechat.postlike.repository.PostLikeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    public boolean isMemberLiked(Post post, Member member){

        if(postLikeRepository.findByPostAndMember(post, member).isPresent()) return true;

        return false;
    }

    /**
     * 게시물 좋아요
     * @param post 게시물
     * @param member 좋아요 누른 회원
     */
    @Transactional
    public void like(Post post, Member member){

        PostLike postLike = PostLike.create(member, post);

        postLikeRepository.save(postLike);
    }

    /**
     * 게시물 좋아요 취소
     * @param post 게시물
     * @param member 좋아요 취소한 회원
     */
    @Transactional
    public void cancelLike(Post post, Member member){

        PostLike postLike = postLikeRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        postLikeRepository.delete(postLike);
    }
}
