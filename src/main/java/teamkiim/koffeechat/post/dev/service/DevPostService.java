package teamkiim.koffeechat.post.dev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.repository.MemberRepository;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.post.dev.domain.DevPost;
import teamkiim.koffeechat.post.dev.domain.repository.DevPostRepository;
import teamkiim.koffeechat.post.dev.dto.response.DevPostViewResponse;
import teamkiim.koffeechat.post.dto.request.CreatePostRequest;
import teamkiim.koffeechat.post.dto.request.UpdatePostRequest;
import teamkiim.koffeechat.skillcategory.domain.SkillCategory;
import teamkiim.koffeechat.skillcategory.domain.repository.SkillCategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 개발 게시글 관련 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevPostService {

    private final DevPostRepository devPostRepository;
    private final MemberRepository memberRepository;
    private final SkillCategoryRepository skillCategoryRepository;

    /**
     * 개발 게시글 생성
     */
    @Transactional
    public DevPostViewResponse createDevPost(CreatePostRequest postRequestDto, Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);  //게시글 작성자 조회
        List<SkillCategory> categories= skillCategoryRepository.findCategories(postRequestDto.getSkillCategories());  //카테고리 가져오기
        DevPost devPost = new DevPost();
        devPost.create(findMember.get(), postRequestDto.getTitle(), postRequestDto.getBodyContent(), categories);

        devPostRepository.save(devPost);  //게시글 저장

        return new DevPostViewResponse(devPost);
    }

    /**
     * 게시글 한 개 조회
     */
    public Post findOne(Long postId) {
        return devPostRepository.findOneDev(postId);
    }

    /**
     * 게시글 리스트 조회
     */
    public List<DevPostViewResponse> findDevPosts() {
        List<DevPost> posts= devPostRepository.findAllDev();
        List<DevPostViewResponse> collect = posts.stream()
                .map(post-> new DevPostViewResponse(post))
                .collect(Collectors.toList());

        return collect;
    }


    /**
     * 제목으로 게시글 조회
     */

    /**
     * 카테고리로 게시글 리스트 조회
     */
    public List<DevPostViewResponse> findDevPostsByCategories(List<String> categoryNames) {
        List<DevPost> posts = devPostRepository.findByCategories(categoryNames);
        List<DevPostViewResponse> collect = posts.stream()
                .map(post-> new DevPostViewResponse(post))
                .collect(Collectors.toList());
        return collect;

    }

    /**
     * 게시글 제목, 내용, 수정 시간 수정
     */
    @Transactional
    public DevPostViewResponse updatePost(Long postId, UpdatePostRequest postDto, Long memberId) {

        DevPost findDev = devPostRepository.findOneDev(postId);  // 게시글이 존재하는 지 확인
        //게시글 수정 권한이 없는 사용자가 게시글 수정을 요청하는 경우
        if (findDev.getMember().getId() != memberId) throw new CustomException(ErrorCode.UPDATE_FORBIDDEN);

        List<SkillCategory> categories = skillCategoryRepository.findCategories(postDto.getSkillCategories());
        findDev.update(postDto, categories);

        return new DevPostViewResponse(findDev);
    }

}
