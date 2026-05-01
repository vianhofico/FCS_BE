# Kế hoạch Triển khai Backend APIs — Fashion Consignment System

## Context
Hệ thống ký gửi thời trang hiện có ~107 endpoints cơ bản. Theo FEATURE_SPECIFICATION.md, nhiều chức năng còn thiếu hoặc chưa đủ để vận hành đầy đủ 4 vai trò (Buyer, Seller, Manager, Admin). Kế hoạch này triển khai theo từng phase ưu tiên, bám sát cấu trúc dự án hiện tại.

## Kiến trúc & Pattern cần tuân thủ
- **Cấu trúc module**: `controller/ → dto/{request,response}/ → service/{interfaces,impl}/ → repository/ → entity/ → mapper/`
- **DTOs**: Java Records có `@Valid` annotations trên request
- **Mappers**: MapStruct interface, `componentModel = "spring"`
- **Response**: `ApiResponse<T>` wrapper cho mọi endpoint
- **Pagination**: `PageResponse<T>` (đã có, chưa dùng) + `Pageable`
- **Exceptions**: `EntityNotFoundException`, `IllegalStateException`, `IllegalArgumentException`
- **Tests**: `@SpringBootTest + @AutoConfigureMockMvc + @ActiveProfiles("test")` với H2 in-memory

## Critical files
- `FCS_BE/src/main/java/com/fcs/be/` — root package
- `FCS_BE/src/main/resources/application.yml` — config chính
- `FCS_BE/src/test/resources/application-test.yml` — H2 test config
- `FCS_BE/src/main/java/com/fcs/be/common/response/ApiResponse.java`
- `FCS_BE/src/main/java/com/fcs/be/common/response/PageResponse.java`
- `FCS_BE/src/main/java/com/fcs/be/common/exception/GlobalExceptionHandler.java`
- `FCS_BE/src/main/java/com/fcs/be/config/SecurityConfig.java`

---

## PHASE 1 — Enhancements cho API hiện có (🔴 Ưu tiên cao)

### 1.1 Product Search & Filter nâng cao

**Vấn đề:** `GET /products` chỉ có `?status=`, thiếu keyword, brandId, categoryId, price range, condition.

**Files cần sửa:**
- `modules/product/controller/ProductController.java` — thêm `@RequestParam` mới
- `modules/product/service/interfaces/ProductService.java` — thêm `ProductFilterRequest` param
- `modules/product/service/impl/ProductServiceImpl.java` — dùng JPA Specification
- `modules/product/repository/ProductRepository.java` — thêm `JpaSpecificationExecutor<Product>`
- `modules/product/dto/request/ProductFilterRequest.java` — **TẠO MỚI** (record với keyword, brandId, categoryId, minPrice, maxPrice, minCondition, maxCondition, status)

**API sau khi sửa:**
```
GET /api/v1/products?keyword=zara&brandId=uuid&categoryId=uuid&minPrice=100000&maxPrice=500000&minCondition=70&status=SELLING&page=0&size=20&sort=salePrice,asc
Response: ApiResponse<PageResponse<ProductResponse>>
```

**Implementation:** Dùng `JPA Specification` (tránh `@Query` cứng), `ProductSpecification` class với các static method `withKeyword()`, `withBrand()`, `withCategory()`, `withPriceRange()`, `withCondition()`, `withStatus()`.

---

### 1.2 Pagination cho tất cả List endpoints

**Vấn đề:** Tất cả API list trả về `List<T>` không có phân trang — nguy hiểm khi data lớn.

**Áp dụng cho:** products, orders, consignments, withdrawals, vouchers, activity-logs, notifications, users, wallets

**Cách làm đồng nhất:**
1. Controller thêm `@PageableDefault(size = 20) Pageable pageable` parameter
2. Service interface đổi return type từ `List<T>` → `PageResponse<T>`
3. Repository thêm method `findBy...(Pageable pageable)` hoặc dùng Specification
4. Response vẫn là `ApiResponse<PageResponse<T>>`

---

### 1.3 Search params cho Orders, Consignments, Users, Withdrawals

**Orders:** `GET /orders?status=&buyerId=&startDate=&endDate=&orderCode=`
**Consignments:** `GET /consignments?status=&consignorId=&code=&startDate=&endDate=`
**Users:** `GET /iam/users?status=&role=&keyword=` (search email/username)
**Withdrawals:** `GET /financial/withdrawals?status=&walletId=&startDate=&endDate=`

**Pattern giống 1.1:** Tạo `*FilterRequest` record, dùng Specification.

---

### 1.4 User Profile Management

**Vấn đề:** Không có API update thông tin user, đổi mật khẩu.

**Files cần tạo/sửa:**
- `modules/iam/controller/UserController.java` — thêm 2 endpoints
- `modules/iam/dto/request/UpdateUserProfileRequest.java` — **TẠO MỚI** (username, phone)
- `modules/iam/dto/request/ChangePasswordRequest.java` — **TẠO MỚI** (currentPassword, newPassword, confirmPassword)
- `modules/iam/service/interfaces/UserService.java` — thêm 2 methods
- `modules/iam/service/impl/UserServiceImpl.java` — implement

**APIs mới:**
```
PUT  /api/v1/iam/users/{id}          — update username, phone
PATCH /api/v1/iam/users/{id}/password — đổi mật khẩu (verify current password trước)
```

---

### 1.5 Forgot Password / Reset Password

**Vấn đề:** Không có luồng reset password.

**Flow:** Gửi email → Lưu token vào Redis (TTL 15 phút) → User click link → Reset

**Files cần tạo/sửa:**
- `modules/iam/controller/AuthController.java` — thêm 2 endpoints
- `modules/iam/dto/request/ForgotPasswordRequest.java` — **TẠO MỚI** (email)
- `modules/iam/dto/request/ResetPasswordRequest.java` — **TẠO MỚI** (token, newPassword)
- `modules/iam/service/interfaces/AuthService.java` — thêm methods
- `modules/iam/service/impl/AuthServiceImpl.java` — implement dùng Redis + email service

**APIs mới:**
```
POST /api/v1/auth/forgot-password   — body: { email } → gửi reset link qua email
POST /api/v1/auth/reset-password    — body: { token, newPassword } → reset
```

**Lưu ý:** Redis service đã có (`common/service/redis`). Email service cần kiểm tra có sẵn không.

---

### 1.6 Voucher Status Management

**Vấn đề:** Không có API bật/tắt voucher.

**Files cần sửa:**
- `modules/order/controller/VoucherController.java` — thêm PATCH endpoint
- `modules/order/dto/request/UpdateVoucherStatusRequest.java` — **TẠO MỚI**
- `modules/order/service/interfaces/VoucherService.java` — thêm method
- `modules/order/service/impl/VoucherServiceImpl.java` — implement

**API mới:**
```
PATCH /api/v1/vouchers/{id}/status   — body: { status: ACTIVE|INACTIVE }
```

---

### 1.7 Order Tracking Number

**Vấn đề:** Không có field tracking number trên Order entity.

**Files cần sửa:**
- `modules/order/entity/Order.java` — thêm `trackingNumber`, `shippingProvider` fields
- `modules/order/dto/response/OrderResponse.java` — thêm 2 fields
- `modules/order/dto/request/UpdateOrderTrackingRequest.java` — **TẠO MỚI**
- `modules/order/controller/OrderController.java` — thêm PATCH endpoint
- `modules/order/service/interfaces/OrderService.java` — thêm method
- `modules/order/service/impl/OrderServiceImpl.java` — implement

**API mới:**
```
PATCH /api/v1/orders/{id}/tracking   — body: { trackingNumber, shippingProvider }
```

---

### 1.8 Order Completion — Trigger thanh toán cho Seller

**Vấn đề:** Khi Order → COMPLETED, cần tự động tạo WalletTransaction (SALE_REVENUE) vào ví Seller và trừ hoa hồng.

**Files cần sửa:**
- `modules/order/service/impl/OrderServiceImpl.java` — trong `updateStatus()`, khi newStatus == COMPLETED: tính commission từ ConsignmentContract, tạo 2 WalletTransaction (SALE_REVENUE cho seller, FEE cho hệ thống), cập nhật Product status → SOLD
- `modules/financial/service/interfaces/WalletService.java` — thêm `recordTransaction()` method nếu chưa có
- Cần inject: `WalletService`, `ConsignmentContractRepository`, `ProductRepository`

**Logic:**
```
sellerAmount = agreedPrice × (1 - commissionRate)
→ WalletTransaction(seller wallet, SALE_REVENUE, sellerAmount)
→ Product.status = SOLD
→ Notification gửi seller
```

---

## PHASE 2 — Return/Refund Module (🔴 Module mới quan trọng)

**Tạo module hoàn toàn mới:** `modules/return/`

### Cấu trúc files

```
modules/return/
├── controller/
│   └── ReturnRequestController.java
├── dto/
│   ├── request/
│   │   ├── CreateReturnRequestRequest.java     (orderId, reason, evidenceUrls)
│   │   └── UpdateReturnStatusRequest.java      (status, reason)
│   └── response/
│       └── ReturnRequestResponse.java
├── entity/
│   ├── ReturnRequest.java                      (extends SoftDeleteEntity)
│   └── ReturnStatusHistory.java               (extends ImmutableLogEntity)
├── mapper/
│   └── ReturnRequestMapper.java
├── repository/
│   ├── ReturnRequestRepository.java
│   └── ReturnStatusHistoryRepository.java
└── service/
    ├── interfaces/
    │   └── ReturnRequestService.java
    └── impl/
        └── ReturnRequestServiceImpl.java
```

### Entity: ReturnRequest
```
Fields: order (ManyToOne), requestedBy (User FK), reason (String), evidenceUrls (JSON/Text),
        status (ReturnRequestStatus enum), reviewedBy (User FK), reviewNote (String),
        reviewedAt (Instant)
```

### Enum: ReturnRequestStatus
```
PENDING → APPROVED → ITEM_RECEIVED → REFUNDED
        ↘ REJECTED
```

### APIs
```
POST   /api/v1/returns              — Buyer tạo yêu cầu hoàn trả (body: orderId, reason, evidenceUrls)
GET    /api/v1/returns              — Danh sách (Manager/Admin: tất cả; Buyer: của mình)
GET    /api/v1/returns/{id}         — Chi tiết
PATCH  /api/v1/returns/{id}/status  — Manager cập nhật: APPROVED/REJECTED/ITEM_RECEIVED/REFUNDED
```

### Business logic khi REFUNDED:
- Tạo WalletTransaction(buyer wallet, REFUND, order.totalAmount)
- Order.status → REFUNDED
- Product.status → RETURNED (vào kho lại)
- Warehouse log: RETURN action
- Thông báo buyer & seller

---

## PHASE 3 — Rating & Review Module (🟡)

**Tạo module mới:** `modules/review/`

### Cấu trúc
```
modules/review/
├── controller/ProductReviewController.java
├── dto/request/CreateReviewRequest.java        (productId, rating 1-5, comment)
├── dto/response/ProductReviewResponse.java
├── entity/ProductReview.java                   (product, buyer, rating, comment, verifiedPurchase)
├── mapper/ProductReviewMapper.java
├── repository/ProductReviewRepository.java
└── service/{interfaces,impl}/ProductReviewService.java
```

### APIs
```
POST /api/v1/products/{productId}/reviews     — Buyer tạo review (chỉ khi đã mua & order COMPLETED)
GET  /api/v1/products/{productId}/reviews     — Xem reviews (public)
GET  /api/v1/products/{productId}/reviews/summary — Rating summary (avg, count by star)
```

### Validation: Chỉ buyer có order COMPLETED với product này mới được review.

---

## PHASE 4 — Wishlist Module (🟡)

**Tạo module mới:** `modules/wishlist/`

### Cấu trúc
```
modules/wishlist/
├── controller/WishlistController.java
├── dto/response/WishlistItemResponse.java
├── entity/WishlistItem.java                    (user, product, unique constraint)
├── mapper/WishlistItemMapper.java
├── repository/WishlistItemRepository.java
└── service/{interfaces,impl}/WishlistService.java
```

### APIs
```
GET    /api/v1/wishlist                 — Lấy wishlist của user hiện tại
POST   /api/v1/wishlist/{productId}     — Thêm sản phẩm
DELETE /api/v1/wishlist/{productId}     — Xoá sản phẩm
```

---

## PHASE 5 — Analytics/Dashboard APIs (🟡)

**Tạo module mới:** `modules/analytics/`

### APIs
```
GET /api/v1/analytics/dashboard         — KPIs tổng quan (Manager/Admin)
    Response: { totalRevenue, pendingConsignments, activeOrders, pendingWithdrawals,
                revenueToday, revenueThisMonth, newUsersThisMonth, topProducts[] }

GET /api/v1/analytics/revenue           — Doanh thu theo period
    Params: ?period=DAILY|WEEKLY|MONTHLY&startDate=&endDate=
    Response: [{ date, revenue, orders, commission }]

GET /api/v1/analytics/consignments      — Thống kê ký gửi
    Response: { totalByStatus: {DRAFT: n, SUBMITTED: n, ...}, conversionRate }

GET /api/v1/analytics/sellers/{sellerId} — Thống kê của 1 seller
    Response: { totalItems, soldItems, totalRevenue, pendingWithdrawal }
```

**Implementation:** Dùng JPQL `@Query` với `SUM`, `COUNT`, `GROUP BY` trực tiếp trong repository. Không cần entity mới.

---

## PHASE 6 — Chat/Messaging Module (🟡)

**Sử dụng WebSocket đã cấu hình (STOMP endpoint /ws)**

**Tạo module mới:** `modules/chat/`

### Cấu trúc
```
modules/chat/
├── controller/ConversationController.java      (REST cho history)
├── controller/ChatWebSocketController.java     (@MessageMapping)
├── dto/request/SendMessageRequest.java         (content)
├── dto/response/ConversationResponse.java
├── dto/response/MessageResponse.java
├── entity/Conversation.java                    (participant1, participant2, lastMessageAt)
├── entity/ChatMessage.java                     (conversation, sender, content, readAt)
├── mapper/
├── repository/
└── service/
```

### APIs (REST)
```
GET  /api/v1/conversations              — Danh sách cuộc trò chuyện của user
GET  /api/v1/conversations/{id}/messages — Lịch sử tin nhắn (paginated)
POST /api/v1/conversations              — Tạo conversation mới (body: participantId)
```

### WebSocket
```
SEND    /app/chat/{conversationId}      — Gửi tin nhắn
SUBSCRIBE /topic/chat/{conversationId}  — Nhận tin nhắn real-time
```

---

## PHASE 7 — Testing (toàn bộ)

### Chiến lược test

**Stack:** JUnit 5 + Mockito + `@SpringBootTest` + MockMvc + H2 in-memory

**Cấu trúc test** (bám theo src/main):
```
src/test/java/com/fcs/be/
├── modules/
│   ├── product/service/impl/ProductServiceImplTest.java     (search/filter)
│   ├── product/controller/ProductControllerTest.java         (MockMvc)
│   ├── order/service/impl/OrderServiceImplTest.java          (completion flow)
│   ├── return/service/impl/ReturnRequestServiceImplTest.java
│   ├── return/controller/ReturnRequestControllerTest.java
│   ├── review/service/impl/ProductReviewServiceImplTest.java
│   ├── wishlist/service/impl/WishlistServiceImplTest.java
│   ├── analytics/controller/AnalyticsControllerTest.java
│   └── iam/service/impl/UserServiceImplTest.java            (profile, password)
└── integration/
    ├── ProductSearchIT.java        — Full search flow
    ├── ReturnRefundFlowIT.java     — Return full flow
    ├── ReviewFlowIT.java           — Review after purchase
    └── WishlistFlowIT.java
```

### Test template (theo pattern hiện tại)
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class XxxServiceImplTest {
    @Autowired XxxService xxxService;
    @Autowired UserRepository userRepository;
    // ... other repos

    @BeforeEach void setUp() { /* cleanup + seed data */ }

    @Test void testHappyPath() { /* assert success */ }
    @Test void testValidationError() { assertThrows(...) }
    @Test void testNotFound() { assertThrows(EntityNotFoundException.class, ...) }
    @Test void testBusinessRuleViolation() { assertThrows(IllegalStateException.class, ...) }
}
```

### Danh sách test case cần viết

**ProductService (search):**
- Tìm theo keyword → trả về sản phẩm khớp
- Tìm theo brandId → chỉ trả về sản phẩm của brand đó
- Tìm theo price range → đúng khoảng giá
- Kết hợp nhiều filter → AND logic
- Pagination: page 0 size 5 → đúng số lượng
- Không có kết quả → trả về empty page (không throw)

**OrderService (completion):**
- Order DELIVERED → COMPLETED: wallet seller tăng đúng amount
- Order DELIVERED → COMPLETED: Product status = SOLD
- Order DELIVERED → COMPLETED: WalletTransaction loại SALE_REVENUE được tạo
- Order không phải DELIVERED → không thể COMPLETED (IllegalStateException)

**ReturnRequestService:**
- Buyer tạo return cho order COMPLETED → PENDING
- Buyer tạo return cho order không COMPLETED → IllegalStateException
- Manager APPROVED → status = APPROVED, history lưu
- Manager REFUNDED → WalletTransaction REFUND được tạo, Order = REFUNDED, Product = RETURNED
- Manager REJECTED + reason → status = REJECTED

**ProductReviewService:**
- Buyer có order COMPLETED với product → tạo review thành công
- Buyer chưa mua product → IllegalStateException
- Rating < 1 hoặc > 5 → validation error
- Duplicate review cùng product+buyer → IllegalStateException

**WishlistService:**
- Add product → item được lưu
- Add product đã có trong wishlist → IllegalStateException
- Remove product → xoá khỏi danh sách
- Get wishlist → trả về đúng items của user

**UserService (profile):**
- Update username/phone → cập nhật thành công
- Change password với currentPassword đúng → thành công
- Change password với currentPassword sai → IllegalArgumentException
- Change password newPassword != confirmPassword → validation error

**MockMvc controller tests:**
- Trả về HTTP 200 + ApiResponse.success = true cho happy path
- Trả về HTTP 400 + errorCode VALIDATION_FAILED khi thiếu field required
- Trả về HTTP 404 + errorCode RESOURCE_NOT_FOUND khi không tìm thấy entity
- Trả về HTTP 409 + errorCode BUSINESS_ERROR khi vi phạm business rule

---

## Thứ tự triển khai

```
Phase 1 (1-2 tuần):
  [x] 1.1 Product search filter + Specification
  [x] 1.2 Pagination tất cả endpoints
  [x] 1.3 Search params (orders, consignments, users, withdrawals)
  [x] 1.4 User profile + change password
  [x] 1.5 Forgot/reset password (cần email service)
  [x] 1.6 Voucher status PATCH
  [x] 1.7 Order tracking number
  [x] 1.8 Order COMPLETED → trigger wallet payment to seller

Phase 2 (1 tuần):
  [x] Return/Refund module hoàn chỉnh

Phase 3 (1 tuần):
  [x] Review module
  [x] Wishlist module

Phase 4 (1 tuần):
  [x] Analytics APIs

Phase 5 (1 tuần):
  [x] Chat module (WebSocket)

Phase Tests (song song với mỗi phase):
  [x] Unit + integration tests cho mỗi feature
  [x] MockMvc controller tests
  [x] Full flow IT tests
```

## Verification

1. **Build:** `mvn clean package -DskipTests` phải thành công
2. **Tests:** `mvn test -Dspring.profiles.active=test` tất cả pass
3. **Swagger:** `http://localhost:8080/swagger-ui.html` — hiện đủ endpoints mới
4. **Docker:** `docker compose up` khởi động thành công
5. **Manual test flow:**
   - Đăng ký → đăng nhập → lấy JWT
   - Search sản phẩm với filter → có kết quả đúng
   - Thêm giỏ hàng → đặt hàng → xác nhận nhận hàng → ví seller tăng
   - Tạo return request → manager duyệt → ví buyer nhận refund
