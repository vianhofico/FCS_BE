# Fashion Consignment System - Thiết Kế Cơ Sở Dữ Liệu

## 1) Mục tiêu Tài liệu

Tài liệu nay mô tả chi tiết thiết kế persistence hiện tại của `FCS_BE`, bao gồm:

- Kiến trúc chung của hệ thống DB.
- Quy ước base entity và soft delete.
- Danh sách enum và ý nghĩa.
- mô tả từng entity, từng Trường, và quan hệ giữa các bảng.

Tài liệu Được viết theo source code entity trong `src/main/java/com/fcs/be`.

---

## 2) Nguyên tắc thiết kế tổng quát

- Khóa chính dung `UUID` thông qua `BaseEntity.id`.
- Mỗi entity đều có `createdAt`, `updatedAt` để audit thời gian.
- Phần lớn bang nghiệp vụ dung soft delete (`isDeleted`).
- các bảng log/history/ledger là immutable, không dung soft delete.
- Trường type/status dung `enum` + `@Enumerated(EnumType.STRING)` để tránh magic string.
- Tiền tệ dung `BigDecimal` với `precision/scale` để đảm bảo độ chính xác.

---

## 3) Lớp Entity Cơ Sở

### `BaseEntity`

Dùng cho tất cả entity:

- `id` (`UUID`): Khóa chính.
- `createdAt` (`Instant`): thời điểm tạo bản ghi.
- `updatedAt` (`Instant`): thời điểm cập nhật gần nhất.

### `SoftDeleteEntity` (extends `BaseEntity`)

Dùng cho bang nghiệp vụ có the xóa mềm:

- `isDeleted` (`boolean`): đánh dấu da xóa mềm hay chưa.

### `ImmutableLogEntity` (extends `BaseEntity`)

Dùng cho bang log/history/transaction bất biến:

- không có `isDeleted`.

---

## 4) Danh sách Enum và tác dụng

### IAM

- `UserStatus`: `PENDING_ACTIVATION`, `ACTIVE`, `SUSPENDED`, `LOCKED`, `DELETED`.
- `AddressType`: `HOME`, `OFFICE`, `OTHER`.

### Consignment

- `ConsignmentRequestStatus`: `DRAFT`, `SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `RECEIVED`, `CANCELLED`.
- `ConsignmentItemStatus`: `PROPOSED`, `UNDER_INSPECTION`, `ACCEPTED`, `REJECTED`, `RETURNED`, `CONVERTED_TO_PRODUCT`.
- `ConsignmentContractStatus`: `DRAFT`, `SIGNED`, `EXPIRED`, `TERMINATED`.

### Product / Inventory

- `ProductStatus`: `DRAFT`, `READY_TO_LIST`, `SELLING`, `RESERVED`, `SOLD`, `HOLD`, `RETURNED`, `ARCHIVED`.
- `MediaType`: `IMAGE`, `VIDEO`.
- `MediaOwnerType`: `CONSIGNMENT_ITEM`, `PRODUCT`.
- `WarehouseActionType`: `IN`, `OUT`, `RETURN`, `MOVE`, `ADJUST`.

### Order / Voucher

- `OrderStatus`: `PENDING_PAYMENT`, `PAID`, `CONFIRMED`, `PACKING`, `SHIPPED`, `DELIVERED`, `COMPLETED`, `CANCELLED`, `REFUNDED`.
- `VoucherDiscountType`: `PERCENT`, `FIXED_AMOUNT`.
- `VoucherStatus`: `DRAFT`, `ACTIVE`, `INACTIVE`, `EXPIRED`.

### Financial

- `WithdrawalStatus`: `PENDING`, `APPROVED`, `PROCESSING_TRANSFER`, `PAID`, `REJECTED`, `CANCELLED`.
- `WalletTransactionType`: `SALE_REVENUE`, `WITHDRAWAL_HOLD`, `WITHDRAWAL_PAID`, `WITHDRAWAL_RELEASE`, `REFUND`, `FEE`, `ADJUSTMENT`.
- `WalletTransactionStatus`: `PENDING`, `POSTED`, `FAILED`, `REVERSED`.

### Audit / Notification

- `NotificationType`: `SYSTEM`, `ORDER`, `CONSIGNMENT`, `FINANCIAL`.
- `ActivityAction`: `CREATE`, `UPDATE`, `DELETE`, `LOGIN`, `APPROVE`.

---

## 5) Module IAM

### `Role` (`roles`)

Loại: `SoftDeleteEntity`.

Trường:

- `name`: tên vai trò, unique.
- `description`: mô tả vai trò.

Quan hệ:

- Được tham chiếu bởi `UserRole.role`.
- Được tham chiếu bởi `RolePermission.role`.

### `Permission` (`permissions`)

Loại: `SoftDeleteEntity`.

Trường:

- `code`: mã permission, unique.
- `name`: tên hiển thị.
- `description`: mô tả.
- `module`: nhóm module.

Quan hệ:

- Được tham chiếu bởi `RolePermission.permission`.
- Được tham chiếu bởi `UserPermission.permission`.

### `User` (`users`)

Loại: `SoftDeleteEntity`.

Trường:

- `username`: tên đăng nhập, unique.
- `passwordHash`: mật khẩu đã băm.
- `email`: email, unique.
- `phone`: số điện thoại.
- `status`: enum `UserStatus`.
- `lastLoginAt`: lần đăng nhập gần nhất.

Quan hệ:

- 1-n đến `UserRole`, `UserPermission`, `UserAddress`.
- Được tham chiếu bởi nhiều module khác (`Order.buyer`, `ConsignmentRequest.consignor`, ...).

### `UserRole` (`user_roles`)

Loại: `SoftDeleteEntity`.

Trường:

- `user`: FK đến `User`.
- `role`: FK đến `Role`.

tác dụng:

- Bảng mapping n-n giữa user và role.

### `RolePermission` (`role_permissions`)

Loại: `SoftDeleteEntity`.

Trường:

- `role`: FK đến `Role`.
- `permission`: FK đến `Permission`.
- `grantedBy`: FK đến `User`, người cap quyền.

tác dụng:

- Bảng mapping n-n giữa role và permission.

### `UserPermission` (`user_permissions`)

Loại: `SoftDeleteEntity`.

Trường:

- `user`: FK đến `User`.
- `permission`: FK đến `Permission`.
- `effect`: allow/đếny theo business rule.
- `reason`: lý do override.
- `expiresAt`: hạn hiệu lực override.
- `grantedBy`: FK đến `User`.

tác dụng:

- Override quyền trực tiếp cho user.

### `UserAddress` (`user_addresses`)

Loại: `SoftDeleteEntity`.

Trường:

- `user`: FK đến `User`.
- `fullName`: tên người nhan.
- `phone`: sdt người nhan.
- `street`, `ward`, `district`, `city`: thông tin địa chỉ.
- `isDefault`: địa chỉ mặc định.
- `type`: enum `AddressType`.

Quan hệ:

- Được tham chiếu bởi `Order.shippingAddress`.

### `AuthIdentity` (`auth_identities`)

Loại: `BaseEntity`.

Trường:

- `user`: FK đến `User`.
- `provider`: enum `AuthProvider` (`LOCAL`, `GOOGLE`).
- `providerUserId`: định danh từ provider (Google `sub`), dùng để map tài khoản social ổn định.
- `providerEmail`: email do provider trả về.
- `emailVerified`: trạng thái xác thực email từ provider.
- `passwordHash`: chỉ dùng cho `LOCAL`, để trống với `GOOGLE`.
- `isPrimary`: đánh dấu identity ưu tiên của user.

Ràng buộc:

- Unique `(provider, provider_user_id)` để tránh trùng identity social.
- Unique `(user_id, provider)` để tránh 1 user có nhiều identity cùng provider.

tác dụng:

- Hỗ trợ đồng thời đăng nhập thường và đăng nhập Google trên cùng mô hình user.

### `RefreshToken` (`refresh_tokens`)

Loại: `BaseEntity`.

Trường:

- `user`: FK đến `User`.
- `identity`: FK đến `AuthIdentity`.
- `tokenHash`: hash của refresh token (không lưu raw token).
- `issuedAt`, `expiresAt`: thời điểm phát hành và hết hạn.
- `revokedAt`: thời điểm revoke token.
- `revokeReason`: lý do revoke.
- `deviceInfo`: metadata thiết bị.
- `ipAddress`: IP lúc cấp token.

Index/Ràng buộc:

- Unique `token_hash`.
- Index `(user_id, revoked_at)` và `(identity_id, revoked_at)` để truy vấn session nhanh.

tác dụng:

- Quản lý lifecycle refresh token (rotate, revoke, logout nhiều thiết bị).
- Access token giữ stateless, chỉ refresh token cần lưu DB.

---

## 6) Module Catalog

### `Category` (`categories`)

Loại: `SoftDeleteEntity`.

Trường:

- `parent`: FK self-reference, tạo cay danh muc.
- `name`: tên danh muc.
- `slug`: slug unique.
- `isActive`: danh muc hoat dong hay không.

Quan hệ:

- N-n với `Product` thông qua `ProductCategory`.

### `Brand` (`brands`)

Loại: `SoftDeleteEntity`.

Trường:

- `name`: tên thuong hieu, unique.
- `logoUrl`: URL logo.
- `description`: mô tả.
- `isActive`: thuong hieu hoat dong hay không.

Quan hệ:

- Được tham chiếu bởi `Product.brand`.

### `SystemSetting` (`system_settings`)

Loại: `SoftDeleteEntity`.

Trường:

- `key`: khoa cau hinh (unique).
- `value`: gia tri cau hinh.
- `description`: mô tả.
- `updatedBy`: FK đến `User`.

tác dụng:

- Luu cau hinh hệ thống có the thay doi theo van hanh.

---

## 7) Module Consignment

### `ConsignmentRequest` (`consignment_requests`)

Loại: `SoftDeleteEntity`.

Trường:

- `consignor`: FK đến `User`.
- `code`: mã request, unique.
- `status`: enum `ConsignmentRequestStatus`.
- `note`: ghi chu.

Quan hệ:

- 1-1 với `ConsignmentItem` (qua `ConsignmentItem.request` unique).
- 1-1 với `ConsignmentContract` (qua `ConsignmentContract.request` unique).

### `ConsignmentItem` (`consignment_items`)

Loại: `SoftDeleteEntity`.

Trường:

- `request`: FK 1-1 đến `ConsignmentRequest`.
- `suggestedName`: tên để xuat.
- `suggestedPrice`: gia để xuat.
- `conditionNote`: ghi chu tinh trang.
- `status`: enum `ConsignmentItemStatus`.
- `rejectionReason`: lý do tu choi.

Quan hệ:

- 1-1 với `Product` (qua `Product.consignmentItem` unique).

### `ConsignmentContract` (`consignment_contracts`)

Loại: `SoftDeleteEntity`.

Trường:

- `request`: FK 1-1 đến `ConsignmentRequest`.
- `commissionRate`: ti le hoa hong.
- `agreedPrice`: gia da chot.
- `signedAt`: thời điểm ky.
- `validUntil`: han hop dong.
- `status`: enum `ConsignmentContractStatus`.

### `ConsignmentStatusHistory` (`consignment_status_history`)

Loại: `ImmutableLogEntity`.

Trường:

- `entityType`: loai doi tuong (request/item/contract theo business).
- `entityId`: UUID doi tuong.
- `fromStatus`: trang thai truoc.
- `toStatus`: trang thai sau.
- `changedBy`: FK đến `User`.
- `reason`: lý do chuyen trang thai.
- `metadataJson`: du lieu bo sung.

tác dụng:

- Luu lich su thay doi trang thai trong nghiệp vụ ky gui.

---

## 8) Module Product / Inventory

### `Product` (`products`)

Loại: `SoftDeleteEntity`.

Trường:

- `consignmentItem`: FK 1-1 đến `ConsignmentItem`.
- `brand`: FK đến `Brand`.
- `sku`: mã hang, unique.
- `name`: tên san pham.
- `description`: mô tả.
- `conditionPercent`: phan tram tinh trang.
- `originalPrice`: gia goc tham chiếu.
- `salePrice`: gia ban.
- `status`: enum `ProductStatus`.
- `reservedUntil`: han giu san pham tam thoi.

Quan hệ:

- N-n với `Category` qua `ProductCategory`.
- 1-n log/history qua `WarehouseLog`, `ProductStatusHistory`.
- Được tham chiếu bởi `OrderItem`, `CartItem`.

### `ProductCategory` (`product_categories`)

Loại: `SoftDeleteEntity`.

Trường:

- `product`: FK đến `Product`.
- `category`: FK đến `Category`.
- `isPrimary`: có phai danh muc chinh hay không.

tác dụng:

- Bảng mapping n-n giữa product và category.

### `MediaAsset` (`media_assets`)

Loại: `SoftDeleteEntity`.

Trường:

- `ownerType`: enum `MediaOwnerType`.
- `ownerId`: UUID của owner (consignment item hoac product).
- `mediaType`: enum `MediaType`.
- `url`: URL media.
- `thumbnailUrl`: URL thumbnail.
- `mimeType`: loai MIME.
- `sizeBytes`: kich thuoc file.
- `durationSec`: thoi luong (video).
- `displayOrder`: thu tu hiển thị.
- `isPrimary`: međịa chỉnh.
- `uploadedBy`: FK đến `User`.

tác dụng:

- Mo hinh polymorphic để luu nhiều anh/video cho item/product.

### `WarehouseLog` (`warehouse_logs`)

Loại: `ImmutableLogEntity`.

Trường:

- `product`: FK đến `Product`.
- `location`: vi tri kho.
- `actionType`: enum `WarehouseActionType`.
- `note`: ghi chu.
- `createdBy`: FK đến `User`.

tác dụng:

- Theo doi lich su vao/ra/chuyen vi tri hang hoa.

### `ProductStatusHistory` (`product_status_history`)

Loại: `ImmutableLogEntity`.

Trường:

- `product`: FK đến `Product`.
- `fromStatus`, `toStatus`: trang thai truoc/sau.
- `changedBy`: FK đến `User`.
- `reason`: lý do.

tác dụng:

- Luu track log trang thai product.

---

## 9) Module Order / Shopping

### `Cart` (`carts`)

Loại: `SoftDeleteEntity`.

Trường:

- `user`: FK đến `User` (nullable cho guest cart).
- `sessionId`: dinh danh guest session.

Quan hệ:

- 1-n đến `CartItem`.

### `CartItem` (`cart_items`)

Loại: `SoftDeleteEntity`.

Trường:

- `cart`: FK đến `Cart`.
- `product`: FK đến `Product`.

tác dụng:

- Luu san pham trong gio hang.

### `Voucher` (`vouchers`)

Loại: `SoftDeleteEntity`.

Trường:

- `code`: mã voucher unique.
- `discountType`: enum `VoucherDiscountType`.
- `value`: gia tri giam.
- `minOrderValue`: gia tri don toi thieu.
- `maxDiscount`: muc giam toi da.
- `startDate`, `endDate`: khoang hieu luc.
- `usageLimit`: tong so lan su dung toi da.
- `usedCount`: so lan da dung.
- `status`: enum `VoucherStatus`.

Quan hệ:

- 1-n đến `VoucherUsage`.

### `VoucherUsage` (`voucher_usages`)

Loại: `SoftDeleteEntity`.

Trường:

- `voucher`: FK đến `Voucher`.
- `user`: FK đến `User`.
- `order`: FK đến `Order`.

tác dụng:

- Luu lich su ap dung voucher theo user và order.

### `Order` (`orders`)

Loại: `SoftDeleteEntity`.

Trường:

- `buyer`: FK đến `User`.
- `orderCode`: mã don unique.
- `subTotal`: tong tien hang.
- `shippingFee`: phi van chuyen.
- `discountAmount`: tong giam gia.
- `totalAmount`: tong thanh toan cuoi.
- `paymentMethod`: phuong thuc thanh toan.
- `shippingAddress`: FK đến `UserAddress`.
- `shippingSnapshot`: snapshot địa chỉ luc dat.
- `status`: enum `OrderStatus`.

Quan hệ:

- 1-n đến `OrderItem`, `OrderStatusHistory`, `VoucherUsage`.

### `OrderItem` (`order_items`)

Loại: `SoftDeleteEntity`.

Trường:

- `order`: FK đến `Order`.
- `product`: FK đến `Product`.
- `skuSnapshot`: snapshot sku.
- `productNameSnapshot`: snapshot ten.
- `conditionSnapshot`: snapshot tinh trang.
- `priceAtPurchase`: gia tai thời điểm mua.

tác dụng:

- Luu immutable thông tin item trong don để doi soat.

### `OrderStatusHistory` (`order_status_history`)

Loại: `ImmutableLogEntity`.

Trường:

- `order`: FK đến `Order`.
- `fromStatus`, `toStatus`: trang thai truoc/sau.
- `changedBy`: FK đến `User`.
- `reason`: lý do.

tác dụng:

- Luu timeline thay doi trang thai don hang.

---

## 10) Module Financial

### `Wallet` (`wallets`)

Loại: `SoftDeleteEntity`.

Trường:

- `user`: FK đến `User` (consignor).
- `balance`: tong so du.
- `availableBalance`: so du kha dung.
- `bankName`, `bankAccountName`, `bankAccountNumber`: thông tin ngan hang.

Quan hệ:

- 1-n đến `WalletTransaction`, `WithdrawalRequest`.

### `WalletTransaction` (`wallet_transactions`)

Loại: `ImmutableLogEntity`.

Trường:

- `wallet`: FK đến `Wallet`.
- `order`: FK đến `Order` (nullable).
- `amount`: so tien giao dich.
- `type`: enum `WalletTransactionType`.
- `referenceType`, `referenceId`: tham chiếu nghiệp vụ.
- `description`: mô tả.
- `status`: enum `WalletTransactionStatus`.
- `idempotencyKey`: key chong ghi trung.
- `createdBy`: FK đến `User`.

tác dụng:

- So cai giao dich vi, là nguon doi soat tai chinh.

### `WithdrawalRequest` (`withdrawal_requests`)

Loại: `SoftDeleteEntity`.

Trường:

- `requestCode`: mã yeu cau rut unique.
- `wallet`: FK đến `Wallet`.
- `amount`: so tien yeu cau.
- `status`: enum `WithdrawalStatus`.
- `reviewedBy`: FK user xu ly.
- `reviewedAt`: thoi gian duyet.
- `rejectReason`: lý do tu choi.
- `bankSnapshotName`, `bankSnapshotNumber`, `bankSnapshotBranch`: snapshot thông tin ngan hang luc request.
- `transferReference`: mã giao dich chuyen khoan thu cong.
- `receiptImageUrl`: link bien lai.
- `transferredAt`: thời điểm da chuyen.

tác dụng:

- Quan ly workflow rut tien thu cong.

### `WithdrawalStatusHistory` (`withdrawal_status_history`)

Loại: `ImmutableLogEntity`.

Trường:

- `withdrawalRequest`: FK đến `WithdrawalRequest`.
- `fromStatus`, `toStatus`: trang thai truoc/sau.
- `changedBy`: FK đến `User`.
- `reason`: lý do.

tác dụng:

- Theo doi lich su xu ly yeu cau rut tien.

---

## 11) Module Audit / Notification

### `ActivityLog` (`activity_logs`)

Loại: `ImmutableLogEntity`.

Trường:

- `user`: FK đến `User`.
- `action`: enum `ActivityAction`.
- `entityName`: tên entity bi tac dong.
- `entityId`: UUID entity bi tac dong.
- `oldValues`, `newValues`: du lieu truoc/sau (JSON string theo van hanh).
- `ipAddress`: địa chỉ IP.
- `userAgent`: user-agent.

tác dụng:

- Audit ai da thay doi gi, phuc vu kiem soat rui ro.

### `Notification` (`notifications`)

Loại: `SoftDeleteEntity`.

Trường:

- `title`: tieu để thong bao.
- `content`: noi dung thong bao.
- `type`: enum `NotificationType`.
- `createdBy`: FK đến `User`.

Quan hệ:

- 1-n đến `UserNotification`.

### `UserNotification` (`user_notifications`)

Loại: `SoftDeleteEntity`.

Trường:

- `user`: FK đến `User`.
- `notification`: FK đến `Notification`.
- `isRead`: da doc hay chưa.
- `readAt`: thời điểm da doc.

tác dụng:

- Mapping thong bao toi từng user và trang thai da doc.

---

## 12) Tổng hợp quan hệ quan trọng

- IAM:
  - `User` n-n `Role` qua `UserRole`.
  - `Role` n-n `Permission` qua `RolePermission`.
  - `User` n-n `Permission` qua `UserPermission` (override).
  - `User` 1-n `AuthIdentity`.
  - `AuthIdentity` 1-n `RefreshToken`.
- Catalog/Product:
  - `Product` n-n `Category` qua `ProductCategory`.
  - `Product` n-1 `Brand`.
- Consignment/Product:
  - `ConsignmentRequest` 1-1 `ConsignmentItem`.
  - `ConsignmentRequest` 1-1 `ConsignmentContract`.
  - `ConsignmentItem` 1-1 `Product`.
- Order:
  - `Order` 1-n `OrderItem`.
  - `Order` 1-n `OrderStatusHistory`.
  - `Voucher` 1-n `VoucherUsage`.
- Financial:
  - `Wallet` 1-n `WalletTransaction`.
  - `Wallet` 1-n `WithdrawalRequest`.
  - `WithdrawalRequest` 1-n `WithdrawalStatusHistory`.
- Audit:
  - `ActivityLog`, `WarehouseLog`, các bảng `*StatusHistory` là immutable log.

---

## 13) Ghi chú vận hành

- Khi tạo migration SQL, can map chinh xac:
  - `UUID` cho PK/FK.
  - `decimal(19,4)` cho Tiền tệ.
  - Unique index cho Các mã nghiệp vụ (`code`, `orderCode`, `requestCode`, `sku`, ...).
- Các Trường log dang để kieu `String` (JSON string) có the doi sang kieu JSON native theo DB engine neu can.
- Media là mo hinh polymorphic (`ownerType` + `ownerId`), rang buoc owner hop le Được enforce tai service layer.
