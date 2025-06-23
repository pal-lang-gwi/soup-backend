FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/project
COPY --chown=gradle:gradle prompts/news-prompt.txt /home/gradle/project/src/main/resources/prompts/news-prompt.txt
WORKDIR /home/gradle/project
RUN gradle build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]