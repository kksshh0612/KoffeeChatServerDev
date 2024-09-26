package teamkiim.koffeechat.domain.post.common.domain;

import lombok.Getter;

@Getter
public enum SortCategory {

    NEW("최신순"),
    LIKE("좋아요순"),
    VIEW("조회순");

    private String name;

    SortCategory(String name) {
        this.name = name;
    }
}
