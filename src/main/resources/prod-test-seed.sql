-- =============================================================================
-- Fashion Consignment System — Production Test Seed
-- =============================================================================
-- Mục đích : Tạo dữ liệu test cho môi trường prod.
-- Yêu cầu  : production-seed-data.sql đã chạy trước (roles, permissions, admin).
--
-- Tài khoản:
--   ADMIN   : admin     / admin@rewear.studio  / password123  (production-seed-data.sql)
--   MANAGER : manager   / manager@rewear.studio / password123
--   SELLER  : seller01  / seller01@test.com     / password123
--   BUYER   : buyer01   / buyer01@test.com      / password123
--
-- Giá bán: tất cả dưới 20,000 VND (dễ test thanh toán)
-- Brand : local brand Việt Nam
--
-- Chạy:
--   mysql --default-character-set=utf8mb4 -u USER -p DATABASE < prod-test-seed.sql
-- =============================================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = 'utf8mb4_unicode_ci';
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================================
-- PHẦN 1: TÀI KHOẢN MANAGER
-- =============================================================================
INSERT INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
SELECT 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'manager',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C',
       'manager@rewear.studio', 'Store Manager', '0901234567', 'ACTIVE', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

INSERT INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
SELECT 'ai000002-0002-0002-0002-000000000002', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
       'LOCAL', 'manager@rewear.studio', 'manager@rewear.studio',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM auth_identities WHERE user_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb' AND provider = 'LOCAL');

INSERT INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
SELECT 'ur000002-0002-0002-0002-000000000002', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
       '22222222-2222-2222-2222-222222222222', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM user_roles
  WHERE user_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'
    AND role_id  = '22222222-2222-2222-2222-222222222222'
    AND is_deleted = false);

INSERT INTO wallets (id, user_id, balance, available_balance, created_at, updated_at, is_deleted)
SELECT 'wa000002-0002-0002-0002-000000000002', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
       0.00, 0.00, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb' AND is_deleted = false);

-- =============================================================================
-- PHẦN 2: TÀI KHOẢN SELLER TEST
-- =============================================================================
INSERT INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
SELECT 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'seller01',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C',
       'seller01@test.com', 'Nguyễn Thị Mai', '0912345678', 'ACTIVE', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'cccccccc-cccc-cccc-cccc-cccccccccccc');

INSERT INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
SELECT 'ai000003-0003-0003-0003-000000000003', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
       'LOCAL', 'seller01@test.com', 'seller01@test.com',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM auth_identities WHERE user_id = 'cccccccc-cccc-cccc-cccc-cccccccccccc' AND provider = 'LOCAL');

INSERT INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
SELECT 'ur000003-0003-0003-0003-000000000003', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
       '33333333-3333-3333-3333-333333333333', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM user_roles
  WHERE user_id = 'cccccccc-cccc-cccc-cccc-cccccccccccc'
    AND role_id  = '33333333-3333-3333-3333-333333333333'
    AND is_deleted = false);

INSERT INTO wallets (id, user_id, balance, available_balance,
  bank_name, bank_account_name, bank_account_number, created_at, updated_at, is_deleted)
SELECT 'wa000003-0003-0003-0003-000000000003', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
       10000.00, 10000.00,
       'Vietcombank', 'NGUYEN THI MAI', '0123456789012', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id = 'cccccccc-cccc-cccc-cccc-cccccccccccc' AND is_deleted = false);

-- =============================================================================
-- PHẦN 3: TÀI KHOẢN BUYER TEST
-- =============================================================================
INSERT INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
SELECT 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'buyer01',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C',
       'buyer01@test.com', 'Trần Văn Hùng', '0987654321', 'ACTIVE', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'dddddddd-dddd-dddd-dddd-dddddddddddd');

INSERT INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
SELECT 'ai000004-0004-0004-0004-000000000004', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
       'LOCAL', 'buyer01@test.com', 'buyer01@test.com',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM auth_identities WHERE user_id = 'dddddddd-dddd-dddd-dddd-dddddddddddd' AND provider = 'LOCAL');

INSERT INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
SELECT 'ur000004-0004-0004-0004-000000000004', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
       '44444444-4444-4444-4444-444444444444', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM user_roles
  WHERE user_id = 'dddddddd-dddd-dddd-dddd-dddddddddddd'
    AND role_id  = '44444444-4444-4444-4444-444444444444'
    AND is_deleted = false);

INSERT INTO wallets (id, user_id, balance, available_balance, created_at, updated_at, is_deleted)
SELECT 'wa000004-0004-0004-0004-000000000004', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
       0.00, 0.00, NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id = 'dddddddd-dddd-dddd-dddd-dddddddddddd' AND is_deleted = false);

INSERT INTO user_addresses (id, user_id, full_name, phone, street, ward, district, city, is_default, type, created_at, updated_at, is_deleted)
SELECT 'addr0001-0001-0001-0001-000000000001', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
       'Trần Văn Hùng', '0987654321', '123 Nguyễn Huệ', 'Phường Bến Nghé', 'Quận 1',
       'TP. Hồ Chí Minh', true, 'HOME', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM user_addresses WHERE id = 'addr0001-0001-0001-0001-000000000001');

-- =============================================================================
-- PHẦN 4: THƯƠNG HIỆU — LOCAL BRAND VIỆT NAM
-- =============================================================================
INSERT INTO brands (id, name, logo_url, description, is_active, created_at, updated_at, is_deleted)
SELECT * FROM (
  SELECT 'br000001-0001-0001-0001-000000000001' AS id,
         'Seven.AM' AS name, NULL AS logo_url,
         'Thương hiệu thời trang nữ công sở nổi tiếng tại Việt Nam' AS description,
         true AS is_active, NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  UNION ALL SELECT 'br000002-0002-0002-0002-000000000002', 'HNOSS', NULL,
         'Local brand thời trang nữ phong cách tối giản, hiện đại', true, NOW(6), NOW(6), false
  UNION ALL SELECT 'br000003-0003-0003-0003-000000000003', 'Tokyolife', NULL,
         'Thương hiệu thời trang nam nữ phổ thông Việt Nam', true, NOW(6), NOW(6), false
  UNION ALL SELECT 'br000004-0004-0004-0004-000000000004', 'Routine', NULL,
         'Local brand streetwear Việt Nam dành cho giới trẻ', true, NOW(6), NOW(6), false
  UNION ALL SELECT 'br000005-0005-0005-0005-000000000005', 'Gumac', NULL,
         'Thương hiệu thời trang nữ trẻ trung, giá tầm trung', true, NOW(6), NOW(6), false
  UNION ALL SELECT 'br000006-0006-0006-0006-000000000006', 'Yame', NULL,
         'Thương hiệu streetwear Việt Nam, nổi bật với hoodies và graphic tee', true, NOW(6), NOW(6), false
  UNION ALL SELECT 'br000007-0007-0007-0007-000000000007', 'Phuong Linh', NULL,
         'Thương hiệu thời trang cao cấp Việt Nam, chuyên váy đầm và áo dài', true, NOW(6), NOW(6), false
  UNION ALL SELECT 'br000008-0008-0008-0008-000000000008', 'Ivy Moda', NULL,
         'Thương hiệu thời trang nữ công sở, phong cách thanh lịch', true, NOW(6), NOW(6), false
  UNION ALL SELECT 'br000009-0009-0009-0009-000000000009', 'Canifa', NULL,
         'Thương hiệu thời trang gia đình Việt, đặc biệt nổi tiếng với len và knitwear', true, NOW(6), NOW(6), false
  UNION ALL SELECT 'br000010-0010-0010-0010-000000000010', 'Owen', NULL,
         'Thương hiệu thời trang nam công sở Việt Nam', true, NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM brands b WHERE b.id = seed.id);

-- =============================================================================
-- PHẦN 5: SẢN PHẨM TEST (giá bán dưới 20,000 VND)
-- =============================================================================
-- Chuỗi: ConsignmentRequest → ConsignmentItem → ConsignmentContract → Product → ProductCategory

-- ---------------------------------------------------------------------------
-- 5.1 CONSIGNMENT REQUESTS
-- ---------------------------------------------------------------------------
INSERT INTO consignment_requests (id, consignor_id, code, status, note, created_at, updated_at, is_deleted)
SELECT * FROM (
  SELECT 'cr000001-0001-0001-0001-000000000001' AS id, 'cccccccc-cccc-cccc-cccc-cccccccccccc' AS consignor_id,
         'CGN-2025-0001' AS code, 'RECEIVED' AS status, NULL AS note, NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  UNION ALL SELECT 'cr000002-0002-0002-0002-000000000002', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CGN-2025-0002', 'RECEIVED', NULL, NOW(6), NOW(6), false
  UNION ALL SELECT 'cr000003-0003-0003-0003-000000000003', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CGN-2025-0003', 'RECEIVED', NULL, NOW(6), NOW(6), false
  UNION ALL SELECT 'cr000004-0004-0004-0004-000000000004', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CGN-2025-0004', 'RECEIVED', NULL, NOW(6), NOW(6), false
  UNION ALL SELECT 'cr000005-0005-0005-0005-000000000005', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CGN-2025-0005', 'RECEIVED', NULL, NOW(6), NOW(6), false
  UNION ALL SELECT 'cr000006-0006-0006-0006-000000000006', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CGN-2025-0006', 'RECEIVED', NULL, NOW(6), NOW(6), false
  UNION ALL SELECT 'cr000007-0007-0007-0007-000000000007', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CGN-2025-0007', 'RECEIVED', NULL, NOW(6), NOW(6), false
  UNION ALL SELECT 'cr000008-0008-0008-0008-000000000008', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CGN-2025-0008', 'RECEIVED', NULL, NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM consignment_requests r WHERE r.id = seed.id);

-- ---------------------------------------------------------------------------
-- 5.2 CONSIGNMENT ITEMS
-- ---------------------------------------------------------------------------
INSERT INTO consignment_items (id, request_id, suggested_name, suggested_price, original_price,
  suggested_brand_id, suggested_category_id, condition_note, status, rejection_reason, created_at, updated_at, is_deleted)
SELECT * FROM (
  SELECT 'ci000001-0001-0001-0001-000000000001' AS id,
         'cr000001-0001-0001-0001-000000000001' AS request_id,
         'Seven.AM Áo sơ mi lụa tay dài màu trắng' AS suggested_name,
         15000.0000 AS suggested_price, 490000.0000 AS original_price,
         'br000001-0001-0001-0001-000000000001' AS suggested_brand_id,
         'c1000003-0003-0000-0000-000000000000' AS suggested_category_id,
         'Tình trạng 95% — mặc 2 lần, còn tag' AS condition_note,
         'CONVERTED_TO_PRODUCT' AS status, NULL AS rejection_reason,
         NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  UNION ALL
  SELECT 'ci000002-0002-0002-0002-000000000002', 'cr000002-0002-0002-0002-000000000002',
         'HNOSS Quần wide-leg kẻ sọc be đen', 12000.0000, 650000.0000,
         'br000002-0002-0002-0002-000000000002', 'c1000003-0003-0000-0000-000000000000',
         'Tình trạng 97% — chỉ mặc 1 lần', 'CONVERTED_TO_PRODUCT', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'ci000003-0003-0003-0003-000000000003', 'cr000003-0003-0003-0003-000000000003',
         'Routine Hoodie Unisex Oversize Màu Đen', 18000.0000, 450000.0000,
         'br000004-0004-0004-0004-000000000004', 'c1000003-0003-0000-0000-000000000000',
         'Tình trạng 90% — đã qua giặt, không phai', 'CONVERTED_TO_PRODUCT', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'ci000004-0004-0004-0004-000000000004', 'cr000004-0004-0004-0004-000000000004',
         'Gumac Váy midi hoa nhí tay phồng', 9000.0000, 380000.0000,
         'br000005-0005-0005-0005-000000000005', 'c1000003-0003-0000-0000-000000000000',
         'Tình trạng 93% — còn đẹp, nhẹ li nhỏ vạt', 'CONVERTED_TO_PRODUCT', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'ci000005-0005-0005-0005-000000000005', 'cr000005-0005-0005-0005-000000000005',
         'Yame Graphic Tee In Logo Vintage', 7000.0000, 250000.0000,
         'br000006-0006-0006-0006-000000000006', 'c1000003-0003-0000-0000-000000000000',
         'Tình trạng 92% — mặc vài lần, không xù', 'CONVERTED_TO_PRODUCT', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'ci000006-0006-0006-0006-000000000006', 'cr000006-0006-0006-0006-000000000006',
         'Ivy Moda Túi xách tay da PU đen size nhỏ', 14000.0000, 550000.0000,
         'br000008-0008-0008-0008-000000000008', 'c1000001-0001-0000-0000-000000000000',
         'Tình trạng 96% — gần mới, còn dustbag', 'CONVERTED_TO_PRODUCT', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'ci000007-0007-0007-0007-000000000007', 'cr000007-0007-0007-0007-000000000007',
         'Canifa Áo len cổ tròn màu caramel size M', 11000.0000, 420000.0000,
         'br000009-0009-0009-0009-000000000009', 'c1000003-0003-0000-0000-000000000000',
         'Tình trạng 94% — mặc 3 lần, không xù lông', 'CONVERTED_TO_PRODUCT', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'ci000008-0008-0008-0008-000000000008', 'cr000008-0008-0008-0008-000000000008',
         'Owen Quần tây slim-fit xám nhạt size 32', 16000.0000, 680000.0000,
         'br000010-0010-0010-0010-000000000010', 'c2000003-0003-0000-0000-000000000000',
         'Tình trạng 95% — còn phẳng, chưa sửa', 'CONVERTED_TO_PRODUCT', NULL, NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM consignment_items i WHERE i.id = seed.id);

-- ---------------------------------------------------------------------------
-- 5.3 CONSIGNMENT CONTRACTS (commission 15%, đã ký)
-- ---------------------------------------------------------------------------
INSERT INTO consignment_contracts (id, request_id, commission_rate, agreed_price, signed_at,
  signed_by_user_id, signed_by_name, signature_method, signature_ip_address, signature_user_agent,
  signature_hash, valid_until, status, created_at, updated_at, is_deleted)
SELECT * FROM (
  SELECT 'cc000001-0001-0001-0001-000000000001' AS id, 'cr000001-0001-0001-0001-000000000001' AS request_id,
         0.1500 AS commission_rate, 15000.0000 AS agreed_price,
         NOW(6) AS signed_at, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb' AS signed_by_user_id,
         'Store Manager' AS signed_by_name, 'DIGITAL' AS signature_method,
         '127.0.0.1' AS signature_ip_address, 'Mozilla/5.0' AS signature_user_agent,
         SHA2('cr000001-cc000001', 256) AS signature_hash,
         DATE_ADD(NOW(6), INTERVAL 180 DAY) AS valid_until, 'SIGNED' AS status,
         NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  UNION ALL SELECT 'cc000002-0002-0002-0002-000000000002', 'cr000002-0002-0002-0002-000000000002', 0.1500, 12000.0000, NOW(6), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Store Manager', 'DIGITAL', '127.0.0.1', 'Mozilla/5.0', SHA2('cr000002-cc000002', 256), DATE_ADD(NOW(6), INTERVAL 180 DAY), 'SIGNED', NOW(6), NOW(6), false
  UNION ALL SELECT 'cc000003-0003-0003-0003-000000000003', 'cr000003-0003-0003-0003-000000000003', 0.1500, 18000.0000, NOW(6), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Store Manager', 'DIGITAL', '127.0.0.1', 'Mozilla/5.0', SHA2('cr000003-cc000003', 256), DATE_ADD(NOW(6), INTERVAL 180 DAY), 'SIGNED', NOW(6), NOW(6), false
  UNION ALL SELECT 'cc000004-0004-0004-0004-000000000004', 'cr000004-0004-0004-0004-000000000004', 0.1500,  9000.0000, NOW(6), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Store Manager', 'DIGITAL', '127.0.0.1', 'Mozilla/5.0', SHA2('cr000004-cc000004', 256), DATE_ADD(NOW(6), INTERVAL 180 DAY), 'SIGNED', NOW(6), NOW(6), false
  UNION ALL SELECT 'cc000005-0005-0005-0005-000000000005', 'cr000005-0005-0005-0005-000000000005', 0.1500,  7000.0000, NOW(6), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Store Manager', 'DIGITAL', '127.0.0.1', 'Mozilla/5.0', SHA2('cr000005-cc000005', 256), DATE_ADD(NOW(6), INTERVAL 180 DAY), 'SIGNED', NOW(6), NOW(6), false
  UNION ALL SELECT 'cc000006-0006-0006-0006-000000000006', 'cr000006-0006-0006-0006-000000000006', 0.1500, 14000.0000, NOW(6), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Store Manager', 'DIGITAL', '127.0.0.1', 'Mozilla/5.0', SHA2('cr000006-cc000006', 256), DATE_ADD(NOW(6), INTERVAL 180 DAY), 'SIGNED', NOW(6), NOW(6), false
  UNION ALL SELECT 'cc000007-0007-0007-0007-000000000007', 'cr000007-0007-0007-0007-000000000007', 0.1500, 11000.0000, NOW(6), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Store Manager', 'DIGITAL', '127.0.0.1', 'Mozilla/5.0', SHA2('cr000007-cc000007', 256), DATE_ADD(NOW(6), INTERVAL 180 DAY), 'SIGNED', NOW(6), NOW(6), false
  UNION ALL SELECT 'cc000008-0008-0008-0008-000000000008', 'cr000008-0008-0008-0008-000000000008', 0.1500, 16000.0000, NOW(6), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Store Manager', 'DIGITAL', '127.0.0.1', 'Mozilla/5.0', SHA2('cr000008-cc000008', 256), DATE_ADD(NOW(6), INTERVAL 180 DAY), 'SIGNED', NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM consignment_contracts c WHERE c.id = seed.id);

-- ---------------------------------------------------------------------------
-- 5.4 PRODUCTS  (7 × SELLING  +  1 × READY_TO_LIST, giá ≤ 20,000đ)
-- ---------------------------------------------------------------------------
INSERT INTO products (id, consignment_item_id, brand_id, sku, name, description,
  condition_percent, original_price, sale_price, status, reserved_until, created_at, updated_at, is_deleted)
SELECT * FROM (
  SELECT 'pd000001-0001-0001-0001-000000000001' AS id,
         'ci000001-0001-0001-0001-000000000001' AS consignment_item_id,
         'br000001-0001-0001-0001-000000000001' AS brand_id,
         'SKU-7AM-SML-001' AS sku,
         'Seven.AM Áo sơ mi lụa tay dài màu trắng' AS name,
         'Áo sơ mi lụa tay dài của Seven.AM, phong cách công sở thanh lịch. Chất lụa mịn, thoáng mát. Size M. Màu trắng ngà. Mặc 2 lần còn tag. Tình trạng 95%.' AS description,
         95.00 AS condition_percent, 490000.0000 AS original_price, 15000.0000 AS sale_price,
         'SELLING' AS status, NULL AS reserved_until, NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  UNION ALL
  SELECT 'pd000002-0002-0002-0002-000000000002',
         'ci000002-0002-0002-0002-000000000002',
         'br000002-0002-0002-0002-000000000002',
         'SKU-HNS-QWL-001',
         'HNOSS Quần wide-leg kẻ sọc be đen',
         'Quần wide-leg phong cách tối giản của HNOSS. Họa tiết kẻ sọc be-đen thanh lịch, chất vải dày dặn không nhăn. Size S. Chỉ mặc 1 lần. Tình trạng 97%.',
         97.00, 650000.0000, 12000.0000, 'SELLING', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'pd000003-0003-0003-0003-000000000003',
         'ci000003-0003-0003-0003-000000000003',
         'br000004-0004-0004-0004-000000000004',
         'SKU-RTN-HOD-001',
         'Routine Hoodie Unisex Oversize Màu Đen',
         'Hoodie unisex oversize của Routine, chất nỉ bông dày, in thêu logo ngực trái. Màu đen cơ bản dễ phối. Size L (mặc như M oversize). Tình trạng 90%.',
         90.00, 450000.0000, 18000.0000, 'SELLING', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'pd000004-0004-0004-0004-000000000004',
         'ci000004-0004-0004-0004-000000000004',
         'br000005-0005-0005-0005-000000000005',
         'SKU-GMC-VMH-001',
         'Gumac Váy midi hoa nhí tay phồng',
         'Váy midi vải voan hoa nhí của Gumac, tay phồng nhẹ vintage. Màu nền kem điểm hoa nhỏ xanh. Size M. Tình trạng 93% — li nhỏ ở vạt gần không thấy.',
         93.00, 380000.0000, 9000.0000, 'SELLING', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'pd000005-0005-0005-0005-000000000005',
         'ci000005-0005-0005-0005-000000000005',
         'br000006-0006-0006-0006-000000000006',
         'SKU-YME-GTE-001',
         'Yame Graphic Tee In Logo Vintage',
         'Áo thun unisex của Yame, in logo vintage tông nâu-cam trên nền trắng. Chất cotton 100% thoáng mát. Size M. Mặc vài lần, không xù, không bay màu. Tình trạng 92%.',
         92.00, 250000.0000, 7000.0000, 'SELLING', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'pd000006-0006-0006-0006-000000000006',
         'ci000006-0006-0006-0006-000000000006',
         'br000008-0008-0008-0008-000000000008',
         'SKU-IVY-TXD-001',
         'Ivy Moda Túi xách tay da PU đen size nhỏ',
         'Túi xách tay của Ivy Moda, da PU đen mềm, quai xách cứng cáp. Kích thước nhỏ nhắn phù hợp đi chơi, đi làm. Còn kèm dustbag gốc. Tình trạng 96%.',
         96.00, 550000.0000, 14000.0000, 'SELLING', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'pd000007-0007-0007-0007-000000000007',
         'ci000007-0007-0007-0007-000000000007',
         'br000009-0009-0009-0009-000000000009',
         'SKU-CNF-ALC-001',
         'Canifa Áo len cổ tròn màu caramel size M',
         'Áo len cổ tròn của Canifa, chất len mềm mại ấm áp, màu caramel dễ phối đồ. Size M vừa dáng. Mặc 3 lần, không xù lông, không giãn. Tình trạng 94%.',
         94.00, 420000.0000, 11000.0000, 'SELLING', NULL, NOW(6), NOW(6), false
  UNION ALL
  SELECT 'pd000008-0008-0008-0008-000000000008',
         'ci000008-0008-0008-0008-000000000008',
         'br000010-0010-0010-0010-000000000010',
         'SKU-OWN-QTX-001',
         'Owen Quần tây slim-fit xám nhạt size 32',
         'Quần tây nam slim-fit của Owen, màu xám nhạt sang trọng phù hợp công sở. Vải polyester-rayon không nhăn. Size 32. Tình trạng 95% — còn phẳng, chưa sửa.',
         95.00, 680000.0000, 16000.0000, 'READY_TO_LIST', NULL, NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM products p WHERE p.id = seed.id);

-- ---------------------------------------------------------------------------
-- 5.5 PRODUCT CATEGORIES
-- ---------------------------------------------------------------------------
INSERT INTO product_categories (id, product_id, category_id, is_primary, created_at, updated_at, is_deleted)
SELECT * FROM (
  -- pd1: Seven.AM sơ mi → Nữ > Quần áo (primary) + Nữ
  SELECT 'pc000001-0001-0001-0001-000000000001' AS id, 'pd000001-0001-0001-0001-000000000001' AS product_id, 'c1000003-0003-0000-0000-000000000000' AS category_id, true  AS is_primary, NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  UNION ALL SELECT 'pc000001-0001-0001-0001-000000000010', 'pd000001-0001-0001-0001-000000000001', 'c1000000-0000-0000-0000-000000000000', false, NOW(6), NOW(6), false
  -- pd2: HNOSS quần → Nữ > Quần áo
  UNION ALL SELECT 'pc000002-0002-0002-0002-000000000002', 'pd000002-0002-0002-0002-000000000002', 'c1000003-0003-0000-0000-000000000000', true,  NOW(6), NOW(6), false
  UNION ALL SELECT 'pc000002-0002-0002-0002-000000000020', 'pd000002-0002-0002-0002-000000000002', 'c1000000-0000-0000-0000-000000000000', false, NOW(6), NOW(6), false
  -- pd3: Routine hoodie → Nữ > Quần áo (unisex, đặt ở nữ) + Nam > Quần áo
  UNION ALL SELECT 'pc000003-0003-0003-0003-000000000003', 'pd000003-0003-0003-0003-000000000003', 'c1000003-0003-0000-0000-000000000000', true,  NOW(6), NOW(6), false
  UNION ALL SELECT 'pc000003-0003-0003-0003-000000000030', 'pd000003-0003-0003-0003-000000000003', 'c2000003-0003-0000-0000-000000000000', false, NOW(6), NOW(6), false
  -- pd4: Gumac váy → Nữ > Quần áo
  UNION ALL SELECT 'pc000004-0004-0004-0004-000000000004', 'pd000004-0004-0004-0004-000000000004', 'c1000003-0003-0000-0000-000000000000', true,  NOW(6), NOW(6), false
  UNION ALL SELECT 'pc000004-0004-0004-0004-000000000040', 'pd000004-0004-0004-0004-000000000004', 'c1000000-0000-0000-0000-000000000000', false, NOW(6), NOW(6), false
  -- pd5: Yame tee → Nữ > Quần áo + Nam > Quần áo
  UNION ALL SELECT 'pc000005-0005-0005-0005-000000000005', 'pd000005-0005-0005-0005-000000000005', 'c1000003-0003-0000-0000-000000000000', true,  NOW(6), NOW(6), false
  UNION ALL SELECT 'pc000005-0005-0005-0005-000000000050', 'pd000005-0005-0005-0005-000000000005', 'c2000003-0003-0000-0000-000000000000', false, NOW(6), NOW(6), false
  -- pd6: Ivy Moda túi → Nữ > Túi xách
  UNION ALL SELECT 'pc000006-0006-0006-0006-000000000006', 'pd000006-0006-0006-0006-000000000006', 'c1000001-0001-0000-0000-000000000000', true,  NOW(6), NOW(6), false
  UNION ALL SELECT 'pc000006-0006-0006-0006-000000000060', 'pd000006-0006-0006-0006-000000000006', 'c1000000-0000-0000-0000-000000000000', false, NOW(6), NOW(6), false
  -- pd7: Canifa len → Nữ > Quần áo
  UNION ALL SELECT 'pc000007-0007-0007-0007-000000000007', 'pd000007-0007-0007-0007-000000000007', 'c1000003-0003-0000-0000-000000000000', true,  NOW(6), NOW(6), false
  UNION ALL SELECT 'pc000007-0007-0007-0007-000000000070', 'pd000007-0007-0007-0007-000000000007', 'c1000000-0000-0000-0000-000000000000', false, NOW(6), NOW(6), false
  -- pd8: Owen quần tây → Nam > Quần áo
  UNION ALL SELECT 'pc000008-0008-0008-0008-000000000008', 'pd000008-0008-0008-0008-000000000008', 'c2000003-0003-0000-0000-000000000000', true,  NOW(6), NOW(6), false
  UNION ALL SELECT 'pc000008-0008-0008-0008-000000000080', 'pd000008-0008-0008-0008-000000000008', 'c2000000-0000-0000-0000-000000000000', false, NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM product_categories pc WHERE pc.id = seed.id);

-- =============================================================================
-- PHẦN 6: VOUCHER TEST
-- =============================================================================
INSERT INTO vouchers (id, code, discount_type, discount_value, min_order_value, max_discount,
  start_date, end_date, usage_limit, used_count, status, created_at, updated_at, is_deleted)
SELECT * FROM (
  -- Giảm 10%, tối đa 2,000đ, đơn từ 10,000đ
  SELECT 'vc000001-0001-0001-0001-000000000001' AS id, 'WELCOME10' AS code,
         'PERCENT' AS discount_type, 10.0000 AS discount_value,
         10000.0000 AS min_order_value, 2000.0000 AS max_discount,
         NOW(6) AS start_date, DATE_ADD(NOW(6), INTERVAL 90 DAY) AS end_date,
         100 AS usage_limit, 0 AS used_count, 'ACTIVE' AS status,
         NOW(6) AS created_at, NOW(6) AS updated_at, false AS is_deleted
  -- Giảm thẳng 2,000đ, đơn từ 15,000đ
  UNION ALL
  SELECT 'vc000002-0002-0002-0002-000000000002', 'FLAT2K',
         'FIXED_AMOUNT', 2000.0000, 15000.0000, NULL,
         NOW(6), DATE_ADD(NOW(6), INTERVAL 30 DAY),
         50, 0, 'ACTIVE', NOW(6), NOW(6), false
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM vouchers v WHERE v.id = seed.id);

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- VERIFY
-- =============================================================================
-- SELECT username, email, (SELECT name FROM roles r JOIN user_roles ur ON r.id=ur.role_id WHERE ur.user_id=u.id AND ur.is_deleted=false LIMIT 1) role
-- FROM users u WHERE id IN ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','cccccccc-cccc-cccc-cccc-cccccccccccc','dddddddd-dddd-dddd-dddd-dddddddddddd');
--
-- SELECT p.sku, p.name, p.sale_price, p.status, b.name brand
-- FROM products p LEFT JOIN brands b ON p.brand_id=b.id WHERE p.is_deleted=false;
