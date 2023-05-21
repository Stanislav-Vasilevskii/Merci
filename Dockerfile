# builder image
FROM openjdk:17.0.2-slim-buster as builder
RUN mkdir application
COPY . /application
RUN chmod u+x ./application/gradlew && cd /application && ./gradlew bootJar

# runnable image
FROM openjdk:17.0.2-slim-buster

COPY --from=builder application/build/libs/*.jar ./merci.jar
EXPOSE 8075/tcp

ENTRYPOINT [ \
"java", \
"-jar", \
"-Xms256m", \
"-Xmx512g", \
"-XX:+UseG1GC", \
"-XX:MaxGCPauseMillis=200", \
"-XX:G1ReservePercent=25", \
"-XX:+ExitOnOutOfMemoryError", \
"-XX:InitiatingHeapOccupancyPercent=30", \
"-XX:+DisableExplicitGC", \
"/merci.jar" \
]