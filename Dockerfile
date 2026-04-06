# [Stage 1] 빌드 단계
FROM amazoncorretto:17-alpine-jdk AS builder
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

RUN jlink \
    --add-modules java.base,java.logging,java.desktop,java.management,java.sql,java.naming,java.security.jgss,java.instrument,jdk.unsupported,java.net.http,jdk.crypto.ec,java.xml \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /custom-jre

# [Stage 2] 실행 단계
FROM alpine:latest
ENV JAVA_HOME=/custom-jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=builder /custom-jre $JAVA_HOME
COPY --from=builder /app/build/libs/*.jar /app.jar

ENTRYPOINT ["java", \
  "-Xms400m", "-Xmx400m", \
  "-XX:+UseSerialGC", \
  "-Xss256k", \
  "-XX:MaxMetaspaceSize=256m", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "/app.jar"]
