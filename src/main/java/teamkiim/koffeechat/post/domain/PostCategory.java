package teamkiim.koffeechat.post.domain;

import lombok.Getter;

@Getter
public enum PostCategory {
    DEV("개발"), COMMUNITY("커뮤니티");

    private String name;

    PostCategory(String name) {
        this.name = name;
    }
}
