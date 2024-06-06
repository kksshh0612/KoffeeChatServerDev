package teamkiim.koffeechat.postlike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.postlike.domain.PostLike;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findById(Long id);

    Optional<PostLike> findByPostAndMember(Post post, Member member);
}
