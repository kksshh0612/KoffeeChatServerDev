# 빌드 단계 - 별칭 builder
FROM gradle:8.5-jdk21-alpine as builder

WORKDIR /app
COPY . .

# Gradle Wrapper 실행 권한 부여
RUN chmod +x gradlew

# Gradle 빌드 명령 실행, 테스트는 제외
RUN ./gradlew clean build -x test

# 실행 단계 - 별칭 runner
FROM openjdk:21-jdk-slim as runner

WORKDIR /app

# 프로젝트를 빌드한 파일을 첫 번째 단계에서 복사
COPY --from=builder /app/build/libs/koffeeChat-0.0.1-SNAPSHOT.jar koffeecChat-server.jar

EXPOSE 8080

# Entry point 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "koffeechat-server.jar"]