package teamkiim.koffeechat.domain.post.common.domain;

import lombok.Getter;

@Getter
public enum SortType {

    NEW("최신순"),
    LIKE("좋아요순"),
    VIEW("조회순");

    private String name;
    SortType(String name) {
        this.name = name;
    }
}
