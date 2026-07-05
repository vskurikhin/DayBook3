# -----------------------------------------------------------------------------
# Runtime image for compiled Go application
# -----------------------------------------------------------------------------
FROM debian:stable-slim

LABEL maintainer="svn"
LABEL description="Go auth-server runtime"

# Устанавливаем tzdata и не допускаем интерактивного запроса при сборке
ENV DEBIAN_FRONTEND=noninteractive

# Working directory
WORKDIR /app

# Install minimal dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends ca-certificates tzdata && \
    rm -rf /var/lib/apt/lists/*

# Задаем часовой пояс и настраиваем систему
ENV TZ=Europe/Moscow

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Copy executable binary
COPY cmd/server/auth-server /app/auth-server

# Copy configuration file
COPY .auth-server.yaml /app/.auth-server.yaml

# Make binary executable
RUN chmod +x /app/auth-server

# Expose application port (change if needed)
EXPOSE 8089

# Start application
ENTRYPOINT ["/app/auth-server", "run", "--config", "/app/.auth-server.yaml"]
