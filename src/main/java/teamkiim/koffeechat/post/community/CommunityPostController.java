package teamkiim.koffeechat.post.community;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.request.PostCreateRequestDto;
import teamkiim.koffeechat.response.CommunityPostViewResponseDto;

@RestController
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    /**
     * 커뮤니티 게시글 생성
     */
    @PostMapping("/com-write")
    public ResponseEntity<CommunityPostViewResponseDto> createPost(@Valid @RequestBody PostCreateRequestDto postDto) {
        CommunityPostViewResponseDto comPostDto = communityPostService.createCommunityPost(postDto);

        //생성된 게시물 반환
        return ResponseEntity.ok(comPostDto);
    }
}
