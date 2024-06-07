FROM amazoncorretto:17-alpine3.18
WORKDIR app
ARG JAR_FILE=build/libs/bob-works-finance-service.jar
COPY ${JAR_FILE} application.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "application.jar"]