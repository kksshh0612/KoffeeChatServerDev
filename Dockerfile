# 빌드 단계 - 별칭 builder
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /build

COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# Gradle 빌드 명령 실행, 테스트는 제외
COPY . /build
RUN gradle clean build -x test --parallel

# 실행 단계 - 별칭 runner
FROM openjdk:21-jdk-slim AS runner

WORKDIR /app

# 프로젝트를 빌드한 파일을 첫 번째 단계에서 복사
COPY --from=builder /build/build/libs/koffeeChat-0.0.1-SNAPSHOT.jar ./koffeeChat-server.jar

# Entry point 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "koffeeChat-server.jar"]