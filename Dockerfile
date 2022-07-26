FROM eclipse-temurin:17-alpine

COPY app/build/libs/app-all.jar app.jar

ENV TZ="Europe/Oslo"

RUN addgroup -S app && adduser -S -G app app

USER app

CMD ["java", "-jar", "app.jar"]