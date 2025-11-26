FROM eclipse-temurin:17-jdk-jammy

# 필수 패키지 설치를 위한 설정
RUN apt-get update && \
    apt-get install -y curl ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build/libs/scheduler-0.0.1-SNAPSHOT.jar scheduler.jar

#CMD ["java", "-jar", "/app/scheduler.jar"]
