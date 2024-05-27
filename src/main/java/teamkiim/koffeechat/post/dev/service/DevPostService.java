package teamkiim.koffeechat.post.dev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.file.service.FileService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.dev.domain.DevPost;
import teamkiim.koffeechat.post.dev.dto.request.ModifyDevPostServiceRequest;
import teamkiim.koffeechat.post.dev.dto.request.SaveDevPostServiceRequest;
import teamkiim.koffeechat.post.dev.dto.response.DevPostListResponse;
import teamkiim.koffeechat.post.dev.dto.response.DevPostResponse;
import teamkiim.koffeechat.post.dev.dto.response.ImageFileInfoDto;
import teamkiim.koffeechat.post.dev.repository.DevPostRepository;

import java.util.List;
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
    private final FileService fileService;

    /**
     * 게시글 최초 임시 저장
     * @param memberId 작성자 PK
     * @return Long 게시글 PK
     */
    @Transactional
    public ResponseEntity<?> saveInitDevPost(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DevPost devPost = DevPost.builder()
                .member(member)
                .build();

        DevPost saveDevPost = devPostRepository.save(devPost);

        return ResponseEntity.ok(saveDevPost.getId());
    }

    /**
     * 게시글 저장
     * @param saveDevPostServiceRequest 게시글 저장 dto
     * @return DevPostResponse
     */
    @Transactional
    public ResponseEntity<?> saveDevPost(SaveDevPostServiceRequest saveDevPostServiceRequest){

        DevPost devPost = devPostRepository.findById(saveDevPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        devPost.completeDevPost(saveDevPostServiceRequest.getTitle(), saveDevPostServiceRequest.getBodyContent(),
                saveDevPostServiceRequest.getCurrDateTime(), saveDevPostServiceRequest.getSkillCategoryList());

        fileService.deleteImageFiles(saveDevPostServiceRequest.getFileIdList(), devPost);

        List<ImageFileInfoDto> imageFileInfoDtoList = devPost.getFileList().stream()
                .map(ImageFileInfoDto::of).collect(Collectors.toList());

        return ResponseEntity.ok(DevPostResponse.of(devPost, imageFileInfoDtoList));
    }

    /**
     * 게시글 목록 조회
     * @param page 페이지 번호 ( ex) 0, 1,,,, )
     * @param size 페이지 당 조회할 데이터 수
     * @return List<DevPostListResponse>
     */
    public ResponseEntity<?> findDevPostList(int page, int size){

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        List<DevPost> devPostList = devPostRepository.findAll(pageRequest).getContent();

        List<DevPostListResponse> devPostListResponseList = devPostList.stream()
                .map(DevPostListResponse::of).collect(Collectors.toList());

        return ResponseEntity.ok(devPostListResponseList);
    }

    /**
     * 게시글 상세 조회
     * @param postId 게시글 PK
     * @return DevPostResponse
     */
    @Transactional
    public ResponseEntity<?> findPost(Long postId){

        DevPost devPost = devPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<ImageFileInfoDto> imageFileInfoDtoList = devPost.getFileList().stream()
                .map(ImageFileInfoDto::of).collect(Collectors.toList());

        return ResponseEntity.ok(DevPostResponse.of(devPost, imageFileInfoDtoList));
    }

    /**
     * 게시글 수정
     * @param modifyDevPostServiceRequest 게시글 수정 dto
     * @return DevPostResponse
     */
    @Transactional
    public ResponseEntity<?> modifyPost(ModifyDevPostServiceRequest modifyDevPostServiceRequest){

        DevPost devPost = devPostRepository.findById(modifyDevPostServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        devPost.modify(modifyDevPostServiceRequest.getTitle(), modifyDevPostServiceRequest.getBodyContent(),
                modifyDevPostServiceRequest.getCurrDateTime(), modifyDevPostServiceRequest.combineSkillCategory());

        List<ImageFileInfoDto> imageFileInfoDtoList = devPost.getFileList().stream()
                .map(ImageFileInfoDto::of).collect(Collectors.toList());

        return ResponseEntity.ok(DevPostResponse.of(devPost, imageFileInfoDtoList));
    }




}
