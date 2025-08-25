# -------- Stage 1: Build --------
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon || return 0

COPY src src

RUN ./gradlew clean bootJar --no-daemon


FROM eclipse-temurin:21-jre-jammy AS runner

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
