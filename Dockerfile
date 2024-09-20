# 빌드 단계 - 별칭 builder
FROM gradle:7.6.1-jdk17-alpine as builder

WORKDIR /app
COPY . .

# Gradle 빌드 명령 실행, 테스트는 제외
RUN ["gradle", "-x", "test", "build"]

# 실행 단계 - 별칭 runner
FROM openjdk:17-alpine as runner

WORKDIR /app

# 프로젝트를 빌드한 파일을 첫 번째 단계에서 복사
COPY --from=builder /app/build/libs/koffeeChat-0.0.1-SNAPSHOT.jar coffechat-server.jar

EXPOSE 8080

# Entry point 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "coffechat-server.jar"]