# Hướng Dẫn Service Dùng Chung

Tài liệu này hướng dẫn cách sử dụng các service dùng chung trong `com.fcs.be.common.service` một cách thống nhất giữa các module tính năng.

## Mục đích

Các service dùng chung cung cấp hành vi hạ tầng có thể tái sử dụng để module tính năng tập trung vào business logic:

- Sinh pre-signed URL cho file trên MinIO
- Thao tác cache và pub/sub qua Redis
- Gửi thông báo WebSocket theo user

Mọi shared service đều theo pattern của dự án:

- Interface nằm trong `service/<domain>/interfaces`
- Implementation nằm trong `service/<domain>/implement`
- Service ở module tính năng phải inject interface, không inject trực tiếp class implementation

## Danh sách service dùng chung

### 1) File Service

Interface: `com.fcs.be.common.service.file.interfaces.FileService`  
Implementation: `com.fcs.be.common.service.file.implement.FileServiceImpl`

#### Trách nhiệm của File Service

- Sinh pre-signed URL để upload
- Sinh pre-signed URL để download
- Bắt buộc thời gian hết hạn không vượt ngưỡng cấu hình (`app.storage.minio.presigned-max-expiry-seconds`)

#### API của File Service

- `PresignedUrlResult generateUploadUrl(String objectKey, Duration expiry)`
- `PresignedUrlResult generateDownloadUrl(String objectKey, Duration expiry)`

Kiểu dữ liệu trả về:

- `PresignedUrlResult` includes:
  - `objectKey`
  - `url`
  - `expiresAt`

#### Quy tắc validate của File Service

`FileServiceImpl` ném `IllegalArgumentException` khi:

- `objectKey` is blank
- `expiry` is null
- `expiry <= 0`
- `expiry` exceeds configured max

Ném `IllegalStateException` khi MinIO SDK không thể sinh pre-signed URL.

#### Ví dụ sử dụng File Service

```java
import com.fcs.be.common.service.file.PresignedUrlResult;
import com.fcs.be.common.service.file.interfaces.FileService;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class MediaUseCaseService {

    private final FileService fileService;

    public MediaUseCaseService(FileService fileService) {
        this.fileService = fileService;
    }

    public PresignedUrlResult createUploadUrl(String objectKey) {
        return fileService.generateUploadUrl(objectKey, Duration.ofMinutes(10));
    }
}
```

#### Lưu ý

- `FileServiceImpl` chỉ hoạt động khi tồn tại bean `MinioClient` (`@ConditionalOnBean(MinioClient.class)`).
- Cần đảm bảo cấu hình MinIO đã có ở môi trường runtime.

### 2) Redis Service

Interface: `com.fcs.be.common.service.redis.interfaces.RedisService`  
Implementation: `com.fcs.be.common.service.redis.implement.RedisServiceImpl`

#### Trách nhiệm của Redis Service

- Ghi/đọc/xóa cặp key-value
- Publish message lên Redis channel

#### API của Redis Service

- `void set(String key, String value, Duration ttl)`
- `Optional<String> get(String key)`
- `void delete(String key)`
- `long publish(String channel, String message)`

#### Quy tắc validate của Redis Service

`RedisServiceImpl` ném `IllegalArgumentException` khi:

- `key` is blank (set/get/delete)
- `value` is null (set)
- `ttl` is null or `ttl <= 0` (set)
- `channel` is blank (publish)
- `message` is null (publish)

#### Ví dụ sử dụng Redis Service

```java
import com.fcs.be.common.service.redis.interfaces.RedisService;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class OtpCacheService {

    private final RedisService redisService;

    public OtpCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void storeOtp(String key, String otp) {
        redisService.set(key, otp, Duration.ofMinutes(5));
    }
}
```

### 3) Notification Service

Interface: `com.fcs.be.common.service.notification.interfaces.NotificationService`  
Implementation: `com.fcs.be.common.service.notification.implement.NotificationServiceImpl`

#### Trách nhiệm của Notification Service

- Gửi message WebSocket tới destination theo user thông qua `SimpMessagingTemplate`

#### API của Notification Service

- `void sendToUser(UUID userId, String destination, Object payload)`

#### Quy tắc validate của Notification Service

`NotificationServiceImpl` ném `IllegalArgumentException` khi:

- `userId` is null
- `destination` is blank

#### Ví dụ sử dụng Notification Service

```java
import com.fcs.be.common.service.notification.interfaces.NotificationService;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderRealtimeService {

    private final NotificationService notificationService;

    public OrderRealtimeService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void notifyOrderUpdated(UUID userId, String orderCode) {
        notificationService.sendToUser(
            userId,
            "/queue/orders",
            Map.of("event", "ORDER_UPDATED", "orderCode", orderCode)
        );
    }
}
```

## Phụ thuộc cấu hình

Các service dùng chung phụ thuộc cấu hình từ `application.yml` + profile đang active:

- MinIO:
  - `MINIO_ENABLED`
  - `MINIO_ENDPOINT`
  - `MINIO_ACCESS_KEY`
  - `MINIO_SECRET_KEY`
  - `MINIO_BUCKET`
  - `MINIO_PRESIGNED_MAX_EXPIRY_SECONDS`
- Redis:
  - `REDIS_HOST`
  - `REDIS_PORT`
  - `REDIS_PASSWORD`
  - `REDIS_DATABASE`
  - `REDIS_TIMEOUT`

Cần giữ truy cập cấu hình tập trung qua `AppConfigHelper` cho các app-level settings.

## Nên làm và không nên làm

### Nên làm

- Inject qua interface (`FileService`, `RedisService`, `NotificationService`)
- Validate input ở feature service trước khi gọi shared service nếu cần
- Xử lý `IllegalArgumentException` và các lỗi hạ tầng đúng ở service boundary
- Giữ controller tập trung vào transport layer và delegate xuống service

### Không nên làm

- Không inject trực tiếp class `*Impl`
- Không đưa business workflow vào shared service
- Không bypass shared service để gọi thẳng client hạ tầng ở feature module (trừ khi có lý do rõ ràng)
- Không hardcode giá trị phụ thuộc môi trường trong service logic

## Hướng dẫn kiểm thử

Khi test feature service có sử dụng shared services:

- Mock interface (`FileService`, `RedisService`, `NotificationService`)
- Verify interaction contract (gọi đúng method và đúng tham số)
- Tách bạch integration test hành vi hạ tầng thật với unit test

Với unit test của shared service, tham chiếu:

- `src/test/java/com/fcs/be/common/service/file/implement/FileServiceImplTest.java`
- `src/test/java/com/fcs/be/common/service/redis/implement/RedisServiceImplTest.java`
- `src/test/java/com/fcs/be/common/service/notification/implement/NotificationServiceImplTest.java`
