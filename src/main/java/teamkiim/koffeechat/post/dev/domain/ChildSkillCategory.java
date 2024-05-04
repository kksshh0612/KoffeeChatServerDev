package teamkiim.koffeechat.post.dev.domain;

import lombok.Getter;

@Getter
public enum ChildSkillCategory {

    // 프로그래밍 언어
    JAVA_SCRIPT("JavaScript"),
    JAVA("Java"),
    HTML_CSS("HTML/CSS"),
    KOTLIN("Kotlin"),
    PYTHON("Python"),
    TYPE_SCRIPT("TypeScript"),
    SWIFT("Swift"),
    FLUTTER("Flutter"),
    SWIFT_UI("SwiftUI"),
    C_SHARP("C#"),
    C("C"),
    CPP("cpp"),

    // 프레임워크&라이브러리
    REACT("React"),
    SPRING("Spring"),
    SPRING_BOOT("Spring Boot"),
    SPRING_SECURITY("Spring Security"),
    NODE_JS("Node.js"),
    VUE_JS("Vue.js"),
    NEXT_JS("Next.js"),
    NEST_JS("Nest.js"),
    JQUERY("jQuery"),
    DJANGO("Django"),
    JPA("JPA"),
    REACT_NATIVE("React Native"),

    // 앱 분류
    IOS("iOS"),
    ANDROID("Android"),

    // 게임 개발
    UNITY("Unity"),
    UNREAL_ENGINE("Unreal Engine"),
    OPEN_GL("OpenGL"),
    UE_BLUEPRINT("UE Blueprint"),
    GLSL("glsl"),
    COMPUTER_GRAPHICS("computer graphics"),
    UNREAL_CPP("언리언 C++"),

    // 데이터베이스
    MYSQL("Mysql"),
    ORACLE("Oracle"),
    MONGODB("MongoDB"),
    POSTGRE_SQL("Postgre_SQL"),
    MARIA_DB("MariaDB"),
    MS_SQL("MS SQL"),
    REDIS("Redis"),
    NEO4J("Neo4j"),
    APOLLO("Apollo"),
    CASSANDRA("Cassandra"),

    KAFKA("Kafka"),

    // 인공지능
    MACHINE_LEARNING("머신러닝"),
    DEEP_LEARNING("딥러닝"),
    TENSORFLOW("Tensorflow"),
    KERAS("Keras"),
    PYTORCH("PyTorch"),
    CNN("CNN"),
    RNN("RNN"),
    DNN("DNN"),
    LLM("LLM"),
    NLP("NLP"),

    // 클라우드 / devops
    DOCKER("Docker"),
    AWS("AWS"),
    KUBERNETES("Kubernetes"),
    CI_CD("CI/CD"),
    MSA("MSA"),
    AZURE("Azure"),
    CLOUD("클라우드"),

    // 데이터 엔지니어링
    PANDAS("Pandas"),
    EXCEL("Excel"),
    WEB_CRAWLING("웹크롤링"),
    DATA_LITERACY("데이터 리터러시"),
    R("R"),
    DATA_ENGINEERING("데이터 엔지니어링"),

    // 형상관리
    GIT("Git"),
    GITHUB("GitHub"),
    JIRA("JIRA"),
    JENKINS("Jenkins"),

    // 개발 도구
    INTELLIJ("IntelliJ"),
    VSC("Visual Studio Code"),
    ECLIPSE("eclipse"),
    ATOM("Atom"),
    VISUAL_STUDIO("Visual Studio"),
    ANDROID_STUDIO("안드로이드 스튜디오"),
    WEBSTORM("WebStorm"),
    XCODE("Xcode");

    private String name;

    ChildSkillCategory(String name) {
        this.name = name;
    }
}



