package teamkiim.koffeechat.domain.tag.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.dto.response.TagInfoDto;
import teamkiim.koffeechat.domain.tag.domain.PostTag;
import teamkiim.koffeechat.domain.tag.domain.Tag;
import teamkiim.koffeechat.domain.tag.repository.PostTagRepository;
import teamkiim.koffeechat.domain.tag.repository.TagRepository;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

/**
 * 게시글 태그 관련 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 게시글 태그 추가
     */
    public void addTags(Post post, List<String> newTagContentList) {
        for (String tagContent : newTagContentList) {
            Tag tag = tagRepository.findByContent(tagContent)
                    .orElseGet(() -> tagRepository.save(new Tag(tagContent)));
            post.addTag(tag);
        }
    }

    /**
     * 게시글 태그 수정
     */
    public void updateTags(Post post, List<String> newTagContentList) {

        List<PostTag> postTagList = postTagRepository.findAllByPost(post);
        List<String> currentTagContentList = postTagList.stream()
                .map(postTag -> postTag.getTag().getContent()).toList();

        // 없어진 태그 삭제
        List<PostTag> tagsToRemove = postTagList.stream()
                .filter(postTag -> !newTagContentList.contains(postTag.getTag().getContent())).toList();

        for (PostTag tag : tagsToRemove) {
            post.removeTag(tag);
        }

        //새로운 태그 추가
        List<String> tagsToAdd = newTagContentList.stream()
                .filter(newTag -> !currentTagContentList.contains(newTag)).toList();

        addTags(post, tagsToAdd);

    }

    public List<TagInfoDto> toTagInfoDtoList(Post post) {
        return post.getPostTagList().stream()
                .map(postTag -> TagInfoDto.of(aesCipherUtil.encrypt(postTag.getTag().getId()), postTag.getTag()))
                .toList();
    }
}
