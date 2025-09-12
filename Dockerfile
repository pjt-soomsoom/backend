# Dockerfile
# 베이스 이미지로 Java 17 버전을 사용합니다.
FROM openjdk:17.0.2-slim

# JAR 파일이 위치할 경로를 변수로 지정합니다.
ARG JAR_FILE=build/libs/*.jar

# 위에서 지정한 JAR 파일을 app.jar 라는 이름으로 컨테이너 내부에 복사합니다.
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 명령어입니다.
ENTRYPOINT ["java","-jar","/app.jar"]
