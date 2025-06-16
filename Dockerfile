FROM openjdk:17-jdk-slim

# 필수 패키지 설치를 위한 설정
RUN apt-get update && \
    apt-get install -y curl ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build/libs/scheduler-0.0.1-SNAPSHOT.jar scheduler.jar

CMD ["java","-Dspring.profiles.active=prod", "-jar", "/app/scheduler.jar"]
