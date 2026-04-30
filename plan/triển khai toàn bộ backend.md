# Backend Completion Plan — Fashion Consignment System

## Context

Hệ thống ký gửi quần áo FCS có backend Spring Boot 3.3.5 (Java 21) với kiến trúc module rõ ràng. Entities, enums, và common layer đã hoàn chỉnh. Một số module đã có CRUD cơ bản nhưng còn nhiều chức năng nghiệp vụ quan trọng chưa có: Auth (login/register), JWT filter, ConsignmentItem/Contract API, Cart/Voucher, WithdrawalRequest, MediaAsset, phân quyền, và business logic phức tạp. Mục tiêu là hoàn thiện backend thành hệ thống production-ready theo các phase từ nền tảng đến nghiệp vụ phức tạp.

---

## Hiện trạng

### Đã có
| Module | Files | Ghi chú |
|---|---|---|
| Common | BaseEntity, SoftDeleteEntity, ImmutableLogEntity, ApiResponse, GlobalExceptionHandler, NotificationService, FileService (Minio), RedisService | Đầy đủ |
| IAM | User, Role, Permission, UserRole, RolePermission, UserPermission, UserAddress, AuthIdentity, RefreshToken | Entities OK |
| IAM | UserController (chỉ GET /{id}), UserTokenPreviewController (preview token), JwtTokenService, UserService | Rất thiếu |
| Catalog | Category, Brand, SystemSetting + đầy đủ CRUD controllers/services | Tương đối đầy đủ |
| Consignment | ConsignmentRequest CRUD + status update | Thiếu ConsignmentItem, ConsignmentContract APIs |
| Product | Product CRUD + status update | Thiếu MediaAsset, ProductCategory, WarehouseLog APIs |
| Order | Order CRUD + status update | Thiếu Cart, Voucher APIs; thiếu business logic (lock product khi order) |
| Financial | Wallet GET/UPDATE chỉ | Thiếu WalletTransaction, WithdrawalRequest APIs |
| Notification | UserNotification GET + markRead | Đủ cơ bản, thiếu trigger logic |
| Audit | ActivityLog GET | Thiếu write/trigger logic |

### Thiếu hoàn toàn
- Authentication: login, register, logout, refresh token, JWT filter
- Authorization: Spring Security với JWT filter thực sự (hiện chỉ có httpBasic)
- ConsignmentItem API (inspect, accept, reject, return)
- ConsignmentContract API (create contract, sign, terminate)
- Cart & CartItem API
- Voucher & VoucherUsage API
- WithdrawalRequest API (create, approve, reject, pay)
- WalletTransaction read API
- MediaAsset upload/management API
- ProductCategory assignment API
- WarehouseLog API
- Business rules: product lock on order, revenue calculation, commission split

---

## Stack và conventions

- **Spring Boot 3.3.5, Java 21**
- **MySQL** (production), **H2** (test)
- **MapStruct 1.6.2** — mapper `@Mapper(componentModel = "spring")`, method naming `toResponse`/`toEntity`
- **Lombok** — entities dùng `@Getter @Setter`
- **JWT (JJWT 0.12.6)** — access + refresh token
- **Redis** — cache/token blacklist
- **Minio** — file storage
- **PayOS** — thanh toán
- **WebSocket** — real-time notification
- **Springdoc OpenAPI 2.6.0** — Swagger UI

### Coding rules (từ .vscode/rules)
- Controller: transport only (no business logic)
- Service: business rules + `@Transactional`
- Repository: interface-based, no business logic
- Mapper: entity ↔ DTO only, no repo access
- Response: `ApiResponse<T>` nhất quán
- Exception: `EntityNotFoundException` → 404, `MethodArgumentNotValidException` → 400, custom exceptions cho domain errors
- DTOs trong `dto/request` và `dto/response`

---

## Phân chia Phase

---

## Phase 1 — Security Foundation (Auth + JWT Filter)

**Mục tiêu**: Hệ thống có thể xác thực người dùng thực sự qua JWT, thay thế httpBasic tạm thời.

### 1.1 — Auth DTOs và Entities
- `dto/request/RegisterRequest.java` — username, email, password, phone
- `dto/request/LoginRequest.java` — username/email, password
- `dto/response/AuthResponse.java` — accessToken, refreshToken, user info
- `dto/request/RefreshTokenRequest.java`

### 1.2 — AuthService
- `service/interfaces/AuthService.java`
- `service/impl/AuthServiceImpl.java`
  - `register(RegisterRequest)` → tạo User (status=ACTIVE) + AuthIdentity (provider=LOCAL) + Wallet tự động
  - `login(LoginRequest)` → verify password (BCrypt), generate access+refresh token, lưu RefreshToken entity, trả AuthResponse
  - `refresh(token)` → validate RefreshToken entity, generate new pair, rotate refresh token
  - `logout(userId)` → xóa/revoke RefreshToken entity
- Repository: `AuthIdentityRepository`, `RefreshTokenRepository`

### 1.3 — JWT Security Filter
- `config/JwtAuthenticationFilter.java` extends `OncePerRequestFilter`
  - Đọc `Authorization: Bearer <token>`
  - Parse với `JwtTokenService.parseAccessToken()`
  - Set `SecurityContextHolder` với UserId
- `config/SecurityConfig.java` — thêm `addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)`, bỏ `httpBasic`, giữ stateless
- `config/SecurityConfig.java` — mở public: `/api/v1/auth/**`, `/api/v1/health`, `/v3/api-docs/**`, `/swagger-ui/**`, `/ws/**`

### 1.4 — AuthController
- `controller/AuthController.java` → `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `POST /api/v1/auth/refresh`, `POST /api/v1/auth/logout`
- Thêm `PasswordEncoder` bean (BCryptPasswordEncoder) vào config

### 1.5 — UserController mở rộng
- `GET /api/v1/iam/users` — list all users
- `PUT /api/v1/iam/users/{id}/status` — activate/deactivate user

### Kiểm thử Phase 1
- `HealthControllerTest` hiện có phải pass
- Thêm `AuthControllerTest` — register → login → access protected endpoint với token → refresh → logout
- Test H2 (đã có scope test)

---

## Phase 2 — ConsignmentItem & ConsignmentContract APIs

**Mục tiêu**: Hoàn thiện luồng ký gửi: từ request → kiểm tra item → ký hợp đồng.

### 2.1 — ConsignmentItem API
File mới trong `modules/consignment`:
- `dto/request/CreateConsignmentItemRequest.java` — requestId, suggestedName, suggestedPrice, conditionNote
- `dto/request/UpdateConsignmentItemStatusRequest.java` — status (UNDER_INSPECTION/ACCEPTED/REJECTED/RETURNED), rejectionReason
- `dto/response/ConsignmentItemResponse.java`
- `repository/ConsignmentItemRepository.java` — tồn tại rồi, bổ sung queries nếu cần
- `service/interfaces/ConsignmentItemService.java`
- `service/impl/ConsignmentItemServiceImpl.java`
  - `createItem(requestId, request)` — validate request status = APPROVED trước khi tạo item
  - `getItemsByRequest(requestId)` 
  - `updateItemStatus(id, status, reason)` — khi CONVERTED_TO_PRODUCT cập nhật ConsignmentItem.status
- `controller/ConsignmentItemController.java` → `GET /api/v1/consignments/{requestId}/items`, `POST /api/v1/consignments/{requestId}/items`, `PATCH /api/v1/consignments/items/{id}/status`

### 2.2 — ConsignmentContract API
- `dto/request/CreateConsignmentContractRequest.java` — requestId, commissionRate, agreedPrice, validUntil
- `dto/request/UpdateContractStatusRequest.java` — status (SIGNED/TERMINATED), reason
- `dto/response/ConsignmentContractResponse.java`
- `repository/ConsignmentContractRepository.java`
- `service/interfaces/ConsignmentContractService.java`
- `service/impl/ConsignmentContractServiceImpl.java`
  - `createContract(request)` — chỉ tạo được khi request.status = APPROVED
  - `signContract(id)` — set signedAt = now, status = SIGNED
  - `terminateContract(id, reason)` — status = TERMINATED
  - `getContractByRequest(requestId)`
- `controller/ConsignmentContractController.java` → `GET /api/v1/consignments/{requestId}/contract`, `POST /api/v1/consignments/contracts`, `PATCH /api/v1/consignments/contracts/{id}/status`

### 2.3 — ConsignmentResponse cập nhật
- `ConsignmentResponse` thêm `item` (nullable) và `contract` (nullable)
- `ConsignmentMapper` cập nhật

### Kiểm thử Phase 2
- `ConsignmentServiceImplTest` — tạo request → tạo item → sign contract → verify status flow

---

## Phase 3 — Product: Media, Categories, WarehouseLog

**Mục tiêu**: Sản phẩm có đầy đủ ảnh, danh mục, và lịch sử kho.

### 3.1 — MediaAsset API
- `dto/request/RequestMediaUploadRequest.java` — ownerType (PRODUCT/...), ownerId, mimeType, filename
- `dto/response/MediaAssetResponse.java` — id, uploadUrl, downloadUrl, ownerType, ownerId
- `repository/MediaAssetRepository.java`
- `service/interfaces/MediaAssetService.java`
- `service/impl/MediaAssetServiceImpl.java`
  - `requestUpload(request)` → gọi `FileService.generateUploadUrl()` → lưu MediaAsset với objectKey, trả presigned URL
  - `getMediaByOwner(ownerType, ownerId)` → list assets
  - `deleteMedia(id)` → soft delete
- `controller/MediaAssetController.java` → `POST /api/v1/media/upload-url`, `GET /api/v1/media?ownerType=PRODUCT&ownerId=...`, `DELETE /api/v1/media/{id}`
- `ProductResponse` thêm `List<MediaAssetResponse> media`

### 3.2 — ProductCategory API
- `dto/request/AssignProductCategoryRequest.java` — productId, categoryIds
- `repository/ProductCategoryRepository.java`
- `service` methods thêm vào `ProductService`: `assignCategories(productId, categoryIds)`, `getCategories(productId)`
- `controller/ProductController.java` thêm: `PUT /api/v1/products/{id}/categories`, `GET /api/v1/products/{id}/categories`
- `ProductResponse` thêm `List<CategoryResponse> categories`

### 3.3 — WarehouseLog API
- `entity/WarehouseLog.java` — đã có entity, kiểm tra fields
- `dto/response/WarehouseLogResponse.java`
- `repository/WarehouseLogRepository.java`
- `service` — tự động tạo log khi product status thay đổi sang RECEIVED/RETURNED/SOLD
- `controller` thêm: `GET /api/v1/products/{id}/warehouse-logs`

### Kiểm thử Phase 3
- Upload URL generation test (mock FileService)
- Category assignment + retrieval test

---

## Phase 4 — Cart, Voucher, Order Business Logic

**Mục tiêu**: Luồng mua hàng đầy đủ với giỏ hàng, voucher, và product locking.

### 4.1 — Cart API
- `dto/request/AddToCartRequest.java` — userId, productId
- `dto/response/CartResponse.java` — cartId, items list với product snapshot
- `repository/CartRepository.java`, `CartItemRepository.java`
- `service/interfaces/CartService.java`
- `service/impl/CartServiceImpl.java`
  - `getCart(userId)` — lấy/tạo cart của user
  - `addItem(userId, productId)` — validate product.status = SELLING, thêm CartItem
  - `removeItem(cartItemId)` — xóa
  - `clearCart(userId)` — xóa hết
- `controller/CartController.java` → `GET /api/v1/cart/{userId}`, `POST /api/v1/cart/{userId}/items`, `DELETE /api/v1/cart/items/{itemId}`, `DELETE /api/v1/cart/{userId}`

### 4.2 — Voucher API
- `dto/request/CreateVoucherRequest.java` — code, discountType, discountValue, minOrderAmount, maxUsage, validFrom, validUntil
- `dto/response/VoucherResponse.java`
- `repository/VoucherRepository.java`, `VoucherUsageRepository.java`
- `service/interfaces/VoucherService.java`
- `service/impl/VoucherServiceImpl.java`
  - `createVoucher(request)` — admin creates
  - `validateVoucher(code, userId, orderAmount)` → tính discount amount
  - `applyVoucher(code, userId, orderId)` → tạo VoucherUsage
- `controller/VoucherController.java` → CRUD + `POST /api/v1/vouchers/validate`

### 4.3 — Order Business Logic nâng cao
Cập nhật `OrderServiceImpl`:
- `createOrder()` — trước khi save: validate mỗi product.status = SELLING, set product.status = RESERVED, set product.reservedUntil
- `updateStatus()` — khi COMPLETED: gọi Financial module để ghi `WalletTransaction(SALE_REVENUE)` vào ví consignor; khi CANCELLED/REFUNDED: release product về SELLING, xóa reservedUntil

### 4.4 — PayOS Integration (Payment)
- `service/interfaces/PaymentService.java`
- `service/impl/PayosPaymentServiceImpl.java`
  - `createPaymentLink(orderId, amount)` → gọi PayOS SDK
  - `handleWebhook(payload)` → verify signature, update order status
- `controller/PaymentController.java` → `POST /api/v1/payments/create-link`, `POST /api/v1/payments/webhook` (public)

### Kiểm thử Phase 4
- Cart add/remove test
- Voucher validation test (amount thresholds, expired, max usage)
- Order create test — verify product status = RESERVED sau khi tạo order
- Order complete test — verify WalletTransaction tạo ra

---

## Phase 5 — Financial: WalletTransaction & WithdrawalRequest

**Mục tiêu**: Consignor nhận tiền và có thể rút tiền.

### 5.1 — WalletTransaction API
- `dto/response/WalletTransactionResponse.java`
- `repository/WalletTransactionRepository.java`
- `service/interfaces/WalletTransactionService.java`
- `service/impl/WalletTransactionServiceImpl.java`
  - `getTransactionsByWallet(walletId)` — list với pagination
  - `recordTransaction(walletId, type, amount, description)` — internal method gọi từ Order module
- `controller/WalletTransactionController.java` → `GET /api/v1/financial/wallets/{walletId}/transactions`

### 5.2 — WithdrawalRequest API
- `dto/request/CreateWithdrawalRequest.java` — walletId, amount
- `dto/request/UpdateWithdrawalStatusRequest.java` — status, reason
- `dto/response/WithdrawalRequestResponse.java`
- `repository/WithdrawalRequestRepository.java`, `WithdrawalStatusHistoryRepository.java`
- `service/interfaces/WithdrawalService.java`
- `service/impl/WithdrawalServiceImpl.java`
  - `createWithdrawal(request)` — validate availableBalance >= amount, hold amount (WITHDRAWAL_HOLD transaction), set status = PENDING
  - `approveWithdrawal(id)` — status = APPROVED
  - `markPaid(id)` — status = PAID, record WITHDRAWAL_PAID transaction, deduct balance
  - `rejectWithdrawal(id, reason)` — status = REJECTED, release hold (WITHDRAWAL_RELEASE transaction)
- `controller/WithdrawalController.java` → `POST /api/v1/financial/withdrawals`, `GET /api/v1/financial/withdrawals`, `PATCH /api/v1/financial/withdrawals/{id}/status`

### Kiểm thử Phase 5
- Withdrawal create test — verify balance hold
- Withdrawal paid test — verify balance deducted + transaction recorded
- Withdrawal reject test — verify balance released

---

## Phase 6 — IAM: RBAC, Address, User Management

**Mục tiêu**: Phân quyền thực sự và quản lý người dùng đầy đủ.

### 6.1 — Role & Permission Management
- `repository/RoleRepository.java`, `PermissionRepository.java`, `UserRoleRepository.java`
- `service/interfaces/RoleService.java`, `PermissionService.java`
- `service/impl/RoleServiceImpl.java`, `PermissionServiceImpl.java`
- `controller/RoleController.java` → CRUD `/api/v1/iam/roles`, assign permissions `POST /api/v1/iam/roles/{id}/permissions`
- `controller/PermissionController.java` → CRUD `/api/v1/iam/permissions`
- `controller/UserRoleController.java` → `POST /api/v1/iam/users/{userId}/roles`, `DELETE /api/v1/iam/users/{userId}/roles/{roleId}`

### 6.2 — UserAddress API
- `dto/request/CreateUserAddressRequest.java`
- `dto/response/UserAddressResponse.java`
- `service/interfaces/UserAddressService.java`
- `service/impl/UserAddressServiceImpl.java`
- `controller/UserAddressController.java` → `GET /api/v1/iam/users/{userId}/addresses`, `POST`, `PUT /{id}`, `DELETE /{id}`

### 6.3 — JWT với Roles trong Claims
- Cập nhật `AuthServiceImpl.login()` → load user roles → đưa vào JWT claims
- Cập nhật `JwtAuthenticationFilter` → extract roles từ claims → set `GrantedAuthority`
- Cập nhật `SecurityConfig` → thêm role-based access rules (ví dụ: chỉ ADMIN mới dùng `/api/v1/iam/roles/**`)

### Kiểm thử Phase 6
- Role assignment test
- Address CRUD test
- Authorization test — ADMIN endpoint từ chối USER token

---

## Phase 7 — Notification, Audit, Refinement

**Mục tiêu**: Notification tự động trigger và audit log đầy đủ.

### 7.1 — Notification Trigger Logic
Cập nhật các services để gọi `NotificationServiceImpl` (WebSocket):
- ConsignmentService.updateStatus → notify consignor khi APPROVED/REJECTED
- OrderServiceImpl.updateStatus → notify buyer khi SHIPPED/DELIVERED
- WithdrawalServiceImpl.markPaid → notify consignor khi withdrawal paid
- `UserNotificationServiceImpl.createNotification(userId, type, message, entityId)` — internal method ghi vào DB + WebSocket push

### 7.2 — ActivityLog Trigger
- `ActivityLogServiceImpl.log(userId, action, entityName, entityId, oldValues, newValues, ip, userAgent)` — internal method
- Cập nhật AuthServiceImpl (LOGIN/LOGOUT), UserServiceImpl (UPDATE), ConsignmentServiceImpl (status changes) → gọi ActivityLogService

### 7.3 — GlobalExceptionHandler nâng cao
- Thêm handler cho `IllegalStateException` → 409 Conflict (business rule violation)
- Thêm handler cho `AccessDeniedException` → 403 Forbidden
- Thêm handler cho `EntityNotFoundException` → 404 (bỏ try-catch trong từng controller)
- Refactor tất cả controllers để bỏ try-catch thủ công, chuyển về GlobalExceptionHandler

### 7.4 — Pagination
- Thêm pagination cho tất cả list endpoints (GET /products, /orders, /consignments, /activity-logs, v.v.)
- `dto/response/PageResponse.java` — content, page, size, totalElements, totalPages

### Kiểm thử Phase 7
- Notification trigger test (mock WebSocket)
- ActivityLog write test
- Pagination test

---

## Phase 8 — Final Polish

### 8.1 — Application Config hoàn thiện
- `application.yml` — verify datasource, redis, minio, JWT keys, payos, CORS origins, websocket config
- `application-test.yml` — H2 in-memory cho tests

### 8.2 — Flyway/Liquibase DB Migration (optional nhưng khuyến nghị)
- Thêm Flyway dependency
- `V1__init_schema.sql` — full DDL từ entities

### 8.3 — Swagger/OpenAPI Documentation
- Thêm `@Operation` / `@Tag` annotations vào controllers
- Config Swagger security scheme cho Bearer token
- Public Swagger UI docs tại `/swagger-ui.html`

### 8.4 — Integration Tests
- `ConsignmentFlowIT` — full flow: register → login → submit request → approve → create item → sign contract → create product → buyer order → complete → verify wallet
- Chạy với H2 in-memory

---

## File paths quan trọng cần tạo/sửa

### Phase 1 (Auth)
- `FCS_BE/src/main/java/com/fcs/be/modules/iam/controller/AuthController.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/modules/iam/service/interfaces/AuthService.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/modules/iam/service/impl/AuthServiceImpl.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/config/JwtAuthenticationFilter.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/config/SecurityConfig.java` — SỬA
- `FCS_BE/src/main/java/com/fcs/be/modules/iam/repository/AuthIdentityRepository.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/modules/iam/repository/RefreshTokenRepository.java` — TẠO MỚI

### Phase 2 (Consignment Item/Contract)
- `FCS_BE/src/main/java/com/fcs/be/modules/consignment/controller/ConsignmentItemController.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/modules/consignment/service/interfaces/ConsignmentItemService.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/modules/consignment/service/impl/ConsignmentItemServiceImpl.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/modules/consignment/controller/ConsignmentContractController.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/modules/consignment/service/interfaces/ConsignmentContractService.java` — TẠO MỚI
- `FCS_BE/src/main/java/com/fcs/be/modules/consignment/service/impl/ConsignmentContractServiceImpl.java` — TẠO MỚI

### Phases 3-8: Tương tự pattern trên cho mỗi module

---

## Thứ tự triển khai đề xuất

```
Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 → Phase 6 → Phase 7 → Phase 8
```

Mỗi phase là một commit/PR riêng, đảm bảo build pass trước khi chuyển phase tiếp.

---

## Verification (cách kiểm thử cuối cùng)

1. `mvn clean test` — tất cả unit tests pass
2. `mvn spring-boot:run` — ứng dụng khởi động thành công
3. Swagger UI tại `http://localhost:8080/swagger-ui.html` — hiển thị đầy đủ APIs
4. Postman/curl full flow:
   - Register → Login → lấy JWT
   - Tạo ConsignmentRequest → approve → tạo ConsignmentItem → sign Contract
   - Tạo Product → add to Cart → create Order → complete Order
   - Verify WalletTransaction tạo cho consignor
   - Create WithdrawalRequest → approve → paid
