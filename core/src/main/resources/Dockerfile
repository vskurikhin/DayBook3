# -----------------------------------------------------------------------------
# Spring Boot Java 21 Runtime Image
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre

LABEL maintainer="svn"
LABEL description="Spring Boot application runtime"

ENV TZ=Europe/Moscow

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Working directory
WORKDIR /app

# Create directories
RUN mkdir -p /app/config /app/logs

# Copy compiled Spring Boot jar
COPY build/libs/core-*.jar /app/app.jar

# Copy Spring configuration
COPY build/resources/main/*.yaml /app/config/

# Spring Boot external configuration
ENV SPRING_CONFIG_LOCATION=file:/app/config/application.yaml

# Optional JVM options
ENV JAVA_OPTS=""

# Application port
EXPOSE 8082

# Optional healthcheck
#HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
#    CMD sh -c "wget -qO- http://localhost:8081/actuator/health || exit 1"

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
