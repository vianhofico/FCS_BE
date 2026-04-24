# FCS_BE

Dịch vụ backend cho Fashion Consignment System (FCS), xây dựng bằng Spring Boot, Maven và Java 21.

## Công nghệ sử dụng

- Java 21
- Spring Boot 3
- Maven
- MySQL
- Redis
- MinIO
- WebSocket (STOMP)

## Cấu trúc dự án

- `src/main/java/com/fcs/be/config`: cấu hình dùng chung (security, cors)
- `src/main/java/com/fcs/be/common`: response dùng chung và xử lý exception
- `src/main/java/com/fcs/be/modules/<feature>`: module theo tính năng (`controller`, `service`, `repository`, `entity`, `dto`, `mapper`)
- `src/main/resources`: cấu hình theo môi trường
- `src/test/java/com/fcs/be`: test tích hợp và test context

## Tài liệu

- [Thiết kế cơ sở dữ liệu](docs/DB_DESIGN.md)
- [Đặc tả use case theo vai trò](docs/USECASE_BY_ROLE.md)
- [Hướng dẫn service dùng chung](docs/COMMON_SERVICES_GUIDE.md)

## Yêu cầu môi trường

- JDK 21+
- Maven 3.9+
- MySQL (cho môi trường dev, trừ khi override bằng biến môi trường)
- Redis (cho cache/pub-sub)
- MinIO (cho lưu trữ object/file)

## Biến môi trường

- `SPRING_PROFILES_ACTIVE` (mặc định: `dev`)
- `SERVER_PORT` (mặc định: `8080`)
- `APP_CORS_ALLOWED_ORIGINS` (mặc định: `http://localhost:3000`)
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`
- `REDIS_DATABASE`
- `REDIS_TIMEOUT`
- `MINIO_ENABLED`
- `MINIO_ENDPOINT`
- `MINIO_ACCESS_KEY`
- `MINIO_SECRET_KEY`
- `MINIO_BUCKET`
- `MINIO_SECURE`
- `JWT_SECRET`

## Chạy local

```bash
mvn clean test
mvn spring-boot:run
```

## Build artifact

```bash
mvn clean package
```

## Kiểm tra health

- Endpoint: `GET /api/v1/health`

## Realtime (WebSocket)

- Endpoint handshake: `ws://localhost:8080/ws`
- Prefix đích broker:
  - Subscribe: `/topic/**`, `/queue/**`
  - Publish app message: `/app/**`

## Docker Compose (đầy đủ dịch vụ)

Chạy toàn bộ backend cùng MySQL + Redis + MinIO + app.

### Dev

Dùng mẫu môi trường local:

```bash
cp .env.example .env
docker compose --env-file .env up -d --build
```

### Production

Dùng file môi trường riêng cho production:

```bash
cp .env.production.example .env.production
docker compose --env-file .env.production up -d --build
```

### Vận hành

Kiểm tra trạng thái:

```bash
docker compose ps
```

Dừng hệ thống:

```bash
docker compose down
```

## Checklist production

- `SPRING_PROFILES_ACTIVE=prod`
- `JWT_SECRET` dài và ngẫu nhiên (không dùng lại giá trị dev)
- Thông tin truy cập Database và MinIO đủ mạnh, tách riêng theo môi trường
- `APP_CORS_ALLOWED_ORIGINS` chỉ chứa domain frontend production
- Các cổng cần thiết đã mở và không xung đột trên host
- Endpoint health phản hồi: `GET /api/v1/health`
