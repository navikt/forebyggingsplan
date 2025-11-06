FROM cgr.dev/chainguard/jre:latest
ENV TZ="Europe/Oslo"
ENV JAVA_TOOL_OPTIONS="-XX:+UseParallelGC -XX:MaxRAMPercentage=75"
WORKDIR /app
COPY app/build/install/app/ /app/
ENTRYPOINT ["java", "-cp", "/app/lib/*", "MainKt"]