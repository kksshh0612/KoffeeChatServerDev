package teamkiim.koffeechat.member.domain;

import lombok.Getter;

@Getter
public enum MemberRole {

    COMPANY_EMPLOYEE("현직자"),
    FREELANCER("프리랜서"),
    STUDENT("학생"),
    GENERAL("일반"),
    COMPANY_EMPLOYEE_TEMP("인증 전 현직자"),
    TEMP("임시"),
    MANAGER("관리자"),
    ADMIN("admin");

    private String name;

    MemberRole(String name) {
        this.name = name;
    }
}
