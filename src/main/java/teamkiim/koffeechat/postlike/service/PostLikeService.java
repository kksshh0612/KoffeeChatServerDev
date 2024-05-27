package teamkiim.koffeechat.postlike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.postlike.repository.PostLikeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeService {

    private PostLikeRepository postLikeRepository;

}
