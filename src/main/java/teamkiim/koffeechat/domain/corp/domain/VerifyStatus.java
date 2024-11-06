package teamkiim.koffeechat.domain.corp.domain;

import lombok.Getter;

@Getter
public enum VerifyStatus {

    WAITING("대기"),
    APPROVED("승인"),
    REJECTED("거절");

    private final String name;

    VerifyStatus(String name) {
        this.name = name;
    }
}
