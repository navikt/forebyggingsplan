FROM gradle:7.5.1-jdk17-alpine as builder
RUN gradle build


FROM eclipse-temurin:17-alpine as runner
COPY --from=builder app/build/libs/app-all.jar app.jar
ENV TZ="Europe/Oslo"
RUN addgroup -S app && adduser -S -G app app
USER app
CMD ["java", "-jar", "app.jar"]