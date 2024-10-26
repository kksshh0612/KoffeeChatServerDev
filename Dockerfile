# 빌드 단계 - 별칭 builder
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app
COPY . .

# Gradle Wrapper 실행 권한 부여
RUN chmod +x gradlew

# Gradle 빌드 명령 실행, 테스트는 제외
RUN ./gradlew clean build -x test

# 실행 단계 - 별칭 runner
FROM openjdk:21-jdk-slim AS runner

WORKDIR /app

ARG JAR_FILE=./build/libs/koffeeChat-0.0.1-SNAPSHOT.jar

# 컨테이너 WORKDIR 위치에 jar 파일 복사
COPY ${JAR_FILE} koffeeChat-server.jar

# 프로젝트를 빌드한 파일을 첫 번째 단계에서 복사
#COPY --from=builder /app/build/libs/koffeeChat-0.0.1-SNAPSHOT.jar koffeecChat-server.jar

# Entry point 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "koffeeChat-server.jar"]