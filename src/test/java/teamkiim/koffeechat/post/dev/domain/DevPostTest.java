package teamkiim.koffeechat.post.dev.domain;

import teamkiim.koffeechat.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;

class DevPostTest {

//    @DisplayName("게시글을 수정할 수 있다.")
//    @Test
//    void modify() {
//        // given
//        DevPost devPost = create()
//
//        // when
//
//        // then
//    }

    private DevPost create(Member member, String title, String bodyContent,
                           LocalDateTime createdTime, List<SkillCategory> skillCategoryList){

        return DevPost.builder()
                .member(member)
                .title(title)
                .bodyContent(bodyContent)
                .viewCount(0L)
                .likeCount(0L)
                .createdTime(createdTime)
                .modifiedTime(null)
                .skillCategoryList(skillCategoryList)
                .build();
    }

}