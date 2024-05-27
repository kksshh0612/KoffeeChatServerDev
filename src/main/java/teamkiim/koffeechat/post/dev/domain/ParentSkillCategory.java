package teamkiim.koffeechat.post.dev.domain;

import lombok.Getter;

@Getter
public enum ParentSkillCategory {

    WEB("웹 개발"),
    MOBILE_APP("모바일 앱 개발"),
    PROGRAMMING_LANGUAGE("프로그래밍 언어"),
    ALGORITHM_DATA_STRUCTURE("알고리즘/자료구조"),
    DATABASE("데이터베이스"),
    DEVOPS_INFRA("데브옵스/인프라"),
    TESTING("소프트웨어 테스트"),
    DEVELOPMENT_TOOLS("개발 도구"),
    GAME_DEVELOPMENT("게임 개발"),
    DATA_ANALYSIS("데이터 분석"),
    ARTIFICIAL_INTELLIGENCE("인공지능"),
    SECURITY("보안"),
    NETWORK("네트워크"),
    CLOUD("클라우드"),
    BLOCKCHAIN("블록체인");

    private final String name;

    ParentSkillCategory(String name) {
        this.name = name;
    }
}
