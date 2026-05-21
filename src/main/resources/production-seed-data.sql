-- =============================================================================
-- Fashion Consignment System — Production seed (dữ liệu gốc)
-- =============================================================================
-- Mục đích : Khởi tạo dữ liệu bắt buộc sau khi deploy (Hibernate ddl-auto=update).
-- Không chứa: đơn hàng, sản phẩm demo, voucher, chat — tạo qua ứng dụng.
--
-- Chạy (MySQL 8+, utf8mb4):
--   mysql --default-character-set=utf8mb4 -u USER -p DATABASE < production-seed-data.sql
--
-- Tài khoản bootstrap (đổi mật khẩu ngay sau lần đăng nhập đầu):
--   admin / admin@rewear.studio / password123
--
-- Lưu ý:
--   - Đăng ký qua API chỉ tạo user + wallet; Admin gán role qua IAM.
--   - Đăng nhập Google yêu cầu role BUYER tồn tại trong bảng roles.
--   - Muốn dataset demo đầy đủ: dùng sample-data.sql (chỉ môi trường dev/staging).
-- =============================================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = 'utf8mb4_unicode_ci';
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------------------------
-- 1. Permissions
-- -----------------------------------------------------------------------------
INSERT INTO permissions (id, code, name, description, module, created_at, updated_at, is_deleted)
SELECT * FROM (
  SELECT '11111111-1111-1111-1111-000000000001' AS id, 'IAM_ROLE_MANAGE' AS code, 'Quản lý vai trò' AS name, 'Tạo/sửa role và gán permission' AS description, 'IAM' AS module, NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  UNION ALL SELECT '11111111-1111-1111-1111-000000000002', 'IAM_USER_VIEW', 'Xem người dùng', 'Xem danh sách và chi tiết user', 'IAM', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000003', 'IAM_USER_EDIT', 'Chỉnh sửa người dùng', 'Cập nhật hồ sơ người dùng', 'IAM', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000004', 'IAM_USER_LOCK', 'Khóa/mở tài khoản', 'Đổi trạng thái ACTIVE/LOCKED', 'IAM', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000005', 'CATALOG_MANAGE', 'Quản lý danh mục', 'Category, brand, system settings', 'CATALOG', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000006', 'CONSIGNMENT_APPROVE', 'Phê duyệt ký gửi', 'Duyệt request và item ký gửi', 'CONSIGNMENT', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000007', 'CONSIGNMENT_CONTRACT', 'Quản lý hợp đồng', 'Tạo/ký hợp đồng ký gửi', 'CONSIGNMENT', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000008', 'PRODUCT_MANAGE', 'Quản lý sản phẩm', 'Đăng bán, kho, media sản phẩm', 'PRODUCT', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000009', 'ORDER_VIEW', 'Xem đơn hàng', 'Xem và điều phối đơn hàng', 'ORDER', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000010', 'ORDER_MANAGE', 'Vận hành đơn hàng', 'Cập nhật trạng thái đơn ngoại lệ', 'ORDER', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000011', 'VOUCHER_MANAGE', 'Quản lý voucher', 'Tạo/sửa voucher khuyến mãi', 'ORDER', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000012', 'FINANCIAL_WITHDRAW', 'Duyệt rút tiền', 'Phê duyệt yêu cầu rút ví', 'FINANCIAL', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000013', 'AUDIT_READ', 'Xem audit log', 'Đọc activity_logs', 'AUDIT', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000014', 'NOTIFICATION_PUBLISH', 'Gửi thông báo', 'Tạo thông báo hệ thống', 'NOTIFICATION', NOW(6), NOW(6), false
  UNION ALL SELECT '11111111-1111-1111-1111-000000000015', 'ANALYTICS_VIEW', 'Xem báo cáo', 'Dashboard doanh thu/thống kê', 'ANALYTICS', NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM permissions p WHERE p.id = seed.id);

-- -----------------------------------------------------------------------------
-- 2. Roles (bắt buộc cho JWT + route guard FE: ADMIN, MANAGER, SELLER, BUYER)
-- -----------------------------------------------------------------------------
INSERT INTO roles (id, name, description, created_at, updated_at, is_deleted)
SELECT * FROM (
  SELECT '11111111-1111-1111-1111-111111111111' AS id, 'ADMIN' AS name, 'Quyền truy cập toàn hệ thống' AS description, NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  UNION ALL SELECT '22222222-2222-2222-2222-222222222222', 'MANAGER', 'Vận hành cửa hàng và duyệt ký gửi', NOW(6), NOW(6), false
  UNION ALL SELECT '33333333-3333-3333-3333-333333333333', 'SELLER', 'Người ký gửi / bán hàng', NOW(6), NOW(6), false
  UNION ALL SELECT '44444444-4444-4444-4444-444444444444', 'BUYER', 'Người mua hàng (mặc định OAuth2)', NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (
  SELECT 1 FROM roles r
  WHERE BINARY r.name = seed.name AND r.is_deleted = false
);

-- -----------------------------------------------------------------------------
-- 3. Role ↔ Permission
-- -----------------------------------------------------------------------------
INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000001', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000001', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000001');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000002', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000002', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000002');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000003', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000003', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000003');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000004', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000004', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000004');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000005', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000005', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000005');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000006', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000006', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000006');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000007', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000007', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000007');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000008', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000008', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000008');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000009', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000009', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000009');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000010', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000010', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000010');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000011', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000011', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000011');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000012', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000012', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000012');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000013', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000013', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000013');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000014', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000014', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000014');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000001-0001-0001-0001-000000000015', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000015', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000001-0001-0001-0001-000000000015');

-- MANAGER: vận hành (không quản lý role/permission)
INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000001', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000002', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000001');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000002', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000005', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000002');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000003', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000006', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000003');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000004', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000007', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000004');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000005', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000008', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000005');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000006', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000009', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000006');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000007', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000010', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000007');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000008', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000011', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000008');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000009', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000012', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000009');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000010', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000013', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000010');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000011', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000014', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000011');

INSERT INTO role_permissions (id, role_id, permission_id, granted_by, created_at, updated_at, is_deleted)
SELECT 'rp000002-0002-0002-0002-000000000012', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000015', NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM role_permissions WHERE id = 'rp000002-0002-0002-0002-000000000012');

-- -----------------------------------------------------------------------------
-- 4. Tài khoản Admin bootstrap
--    BCrypt: password123 — đổi ngay trên production
-- -----------------------------------------------------------------------------
INSERT INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
SELECT 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', 'admin@rewear.studio', 'System Administrator', NULL, 'ACTIVE', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

INSERT INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
SELECT 'ur000001-0001-0001-0001-000000000001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', NOW(6), NOW(6), false
WHERE NOT EXISTS (
  SELECT 1 FROM user_roles ur
  WHERE ur.user_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
    AND ur.role_id = '11111111-1111-1111-1111-111111111111'
    AND ur.is_deleted = false
);

INSERT INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
SELECT 'ai000001-0001-0001-0001-000000000001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'LOCAL', 'admin@rewear.studio', 'admin@rewear.studio', '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM auth_identities ai
  WHERE ai.user_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
    AND BINARY ai.provider = 'LOCAL'
);

INSERT INTO wallets (id, user_id, balance, available_balance, bank_name, bank_account_name, bank_account_number, created_at, updated_at, is_deleted)
SELECT 'wa000001-0001-0001-0001-000000000001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 0.00, 0.00, NULL, NULL, NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM wallets w WHERE w.user_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa' AND w.is_deleted = false);

-- -----------------------------------------------------------------------------
-- 5. Cấu hình hệ thống
-- -----------------------------------------------------------------------------
INSERT INTO system_settings (id, setting_key, setting_value, description, created_at, updated_at, is_deleted)
SELECT 'ss000001-0001-0001-0001-000000000001', 'COMMISSION_RATE', '0.10', 'Tỷ lệ hoa hồng mặc định (10%)', NOW(6), NOW(6), false
WHERE NOT EXISTS (
  SELECT 1 FROM system_settings
  WHERE BINARY setting_key = 'COMMISSION_RATE' AND is_deleted = false
);

INSERT INTO system_settings (id, setting_key, setting_value, description, created_at, updated_at, is_deleted)
SELECT 'ss000002-0002-0002-0002-000000000002', 'SHIPPING_FEE_FLAT', '30000', 'Phí vận chuyển đồng giá (VND)', NOW(6), NOW(6), false
WHERE NOT EXISTS (
  SELECT 1 FROM system_settings
  WHERE BINARY setting_key = 'SHIPPING_FEE_FLAT' AND is_deleted = false
);

INSERT INTO system_settings (id, setting_key, setting_value, description, created_at, updated_at, is_deleted)
SELECT 'ss000003-0003-0003-0003-000000000003', 'MIN_ORDER_VALUE', '500000', 'Giá trị đơn hàng tối thiểu (VND)', NOW(6), NOW(6), false
WHERE NOT EXISTS (
  SELECT 1 FROM system_settings
  WHERE BINARY setting_key = 'MIN_ORDER_VALUE' AND is_deleted = false
);

-- -----------------------------------------------------------------------------
-- 6. Danh mục sản phẩm (cây category — dùng khi tạo sản phẩm / lọc catalog)
-- -----------------------------------------------------------------------------
INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c1000000-0000-0000-0000-000000000000', NULL, 'Nữ', 'women', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'women' AND is_deleted = false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c1000001-0001-0000-0000-000000000000', 'c1000000-0000-0000-0000-000000000000', 'Túi xách', 'women-handbags', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'women-handbags' AND is_deleted = false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c1000002-0002-0000-0000-000000000000', 'c1000000-0000-0000-0000-000000000000', 'Giày', 'women-shoes', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'women-shoes' AND is_deleted = false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c1000003-0003-0000-0000-000000000000', 'c1000000-0000-0000-0000-000000000000', 'Quần áo', 'women-clothing', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'women-clothing' AND is_deleted = false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c2000000-0000-0000-0000-000000000000', NULL, 'Nam', 'men', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'men' AND is_deleted = false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c2000001-0001-0000-0000-000000000000', 'c2000000-0000-0000-0000-000000000000', 'Túi xách', 'men-handbags', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'men-handbags' AND is_deleted = false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c2000002-0002-0000-0000-000000000000', 'c2000000-0000-0000-0000-000000000000', 'Giày', 'men-shoes', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'men-shoes' AND is_deleted = false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c2000003-0003-0000-0000-000000000000', 'c2000000-0000-0000-0000-000000000000', 'Quần áo', 'men-clothing', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'men-clothing' AND is_deleted = false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted)
SELECT 'c3000000-0000-0000-0000-000000000000', NULL, 'Phụ kiện', 'accessories', true, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE BINARY slug = 'accessories' AND is_deleted = false);

SET FOREIGN_KEY_CHECKS = 1;

-- Xác minh nhanh sau khi chạy:
-- SELECT name FROM roles WHERE is_deleted = false;
-- SELECT username, email, status FROM users WHERE is_deleted = false;
-- SELECT setting_key, setting_value FROM system_settings WHERE is_deleted = false;
