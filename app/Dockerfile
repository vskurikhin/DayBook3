FROM nginx:1.27-alpine

ARG TZ=Europe/Moscow

RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

COPY ./nginx.conf /etc/nginx/nginx.conf
COPY ./html/index.html /usr/share/nginx/html/index.html
COPY ./html/vite.svg /usr/share/nginx/html/vite.svg
RUN mkdir -p /usr/share/nginx/html/assets
COPY ./html/assets/* /usr/share/nginx/html/assets/
RUN mkdir -p /var/www/html/{2019,2020,2021,2022,2023,2024,2025,2026}

EXPOSE 80
