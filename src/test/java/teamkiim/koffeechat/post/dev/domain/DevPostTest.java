package teamkiim.koffeechat.post.dev.domain;

import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

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
                           List<SkillCategory> skillCategoryList){

        return DevPost.builder()
                .member(member)
                .title(title)
                .bodyContent(bodyContent)
                .viewCount(0L)
                .likeCount(0L)
                .bookmarkCount(0L)
                .skillCategoryList(skillCategoryList)
                .build();
    }

}