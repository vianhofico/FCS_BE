-- =============================================================================
-- Fashion Consignment System — Production Test Seed (v3 — valid UUIDs only)
-- =============================================================================
-- UUID chỉ cho phép ký tự hex: 0-9 và a-f
-- Các ký tự i, r, w, v, p, s, t, u, ... KHÔNG hợp lệ trong UUID.
-- MySQL lưu CHAR(36) bất kỳ, nhưng Hibernate gọi UUID.fromString() → exception.
--
-- Tài khoản:
--   ADMIN   : admin     / admin@rewear.studio  / password123  (production-seed-data.sql)
--   MANAGER : manager   / manager@rewear.studio / password123
--   SELLER  : seller01  / seller01@test.com     / password123
--   BUYER   : buyer01   / buyer01@test.com      / password123
--
-- Chạy: mysql --default-character-set=utf8mb4 -u USER -p DATABASE < prod-test-seed.sql
-- =============================================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = 'utf8mb4_unicode_ci';
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================================
-- PHẦN 0: DỌN DẸP DỮ LIỆU CŨ CÓ UUID KHÔNG HỢP LỆ
-- (id chứa ký tự ngoài 0-9,a-f: pd, pc, cr, ci, br, vc, wa, ur, ai, addr...)
-- =============================================================================
DELETE FROM product_categories WHERE id LIKE 'pc0000%';
DELETE FROM products              WHERE id LIKE 'pd0000%';
DELETE FROM consignment_contracts WHERE id LIKE 'cc0000%';
DELETE FROM consignment_items     WHERE id LIKE 'ci0000%';
DELETE FROM consignment_requests  WHERE id LIKE 'cr0000%';
DELETE FROM brands                WHERE id LIKE 'br0000%';
DELETE FROM vouchers              WHERE id LIKE 'vc0000%';
DELETE FROM user_addresses  WHERE user_id IN ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','cccccccc-cccc-cccc-cccc-cccccccccccc','dddddddd-dddd-dddd-dddd-dddddddddddd');
DELETE FROM wallets         WHERE user_id IN ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','cccccccc-cccc-cccc-cccc-cccccccccccc','dddddddd-dddd-dddd-dddd-dddddddddddd');
DELETE FROM user_roles      WHERE user_id IN ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','cccccccc-cccc-cccc-cccc-cccccccccccc','dddddddd-dddd-dddd-dddd-dddddddddddd');
DELETE FROM auth_identities WHERE user_id IN ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','cccccccc-cccc-cccc-cccc-cccccccccccc','dddddddd-dddd-dddd-dddd-dddddddddddd');

-- =============================================================================
-- PHẦN 1: TÀI KHOẢN MANAGER
-- user id: bbbbbbbb-... (valid — chỉ dùng ký tự b)
-- supporting records dùng prefix hex hợp lệ: eeee, acdc, beef
-- =============================================================================
INSERT INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
SELECT 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'manager',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C',
       'manager@rewear.studio', 'Store Manager', '0901234567', 'ACTIVE', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

INSERT IGNORE INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
VALUES ('eeee0002-0000-0000-0000-000000000001', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        'LOCAL', 'manager@rewear.studio', 'manager@rewear.studio',
        '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6));

INSERT IGNORE INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
VALUES ('acdc0002-0000-0000-0000-000000000001', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        '22222222-2222-2222-2222-222222222222', NOW(6), NOW(6), false);

INSERT IGNORE INTO wallets (id, user_id, balance, available_balance, created_at, updated_at, is_deleted)
VALUES ('beef0002-0000-0000-0000-000000000001', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        0.00, 0.00, NOW(6), NOW(6), false);

-- =============================================================================
-- PHẦN 2: TÀI KHOẢN SELLER TEST
-- =============================================================================
INSERT INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
SELECT 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'seller01',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C',
       'seller01@test.com', 'Nguyễn Thị Mai', '0912345678', 'ACTIVE', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'cccccccc-cccc-cccc-cccc-cccccccccccc');

INSERT IGNORE INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
VALUES ('eeee0003-0000-0000-0000-000000000001', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
        'LOCAL', 'seller01@test.com', 'seller01@test.com',
        '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6));

INSERT IGNORE INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
VALUES ('acdc0003-0000-0000-0000-000000000001', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
        '33333333-3333-3333-3333-333333333333', NOW(6), NOW(6), false);

INSERT IGNORE INTO wallets (id, user_id, balance, available_balance,
  bank_name, bank_account_name, bank_account_number, created_at, updated_at, is_deleted)
VALUES ('beef0003-0000-0000-0000-000000000001', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
        10000.00, 10000.00,
        'Vietcombank', 'NGUYEN THI MAI', '0123456789012', NOW(6), NOW(6), false);

-- =============================================================================
-- PHẦN 3: TÀI KHOẢN BUYER TEST
-- =============================================================================
INSERT INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
SELECT 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'buyer01',
       '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C',
       'buyer01@test.com', 'Trần Văn Hùng', '0987654321', 'ACTIVE', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'dddddddd-dddd-dddd-dddd-dddddddddddd');

INSERT IGNORE INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
VALUES ('eeee0004-0000-0000-0000-000000000001', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
        'LOCAL', 'buyer01@test.com', 'buyer01@test.com',
        '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6));

INSERT IGNORE INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
VALUES ('acdc0004-0000-0000-0000-000000000001', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
        '44444444-4444-4444-4444-444444444444', NOW(6), NOW(6), false);

INSERT IGNORE INTO wallets (id, user_id, balance, available_balance, created_at, updated_at, is_deleted)
VALUES ('beef0004-0000-0000-0000-000000000001', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
        0.00, 0.00, NOW(6), NOW(6), false);

INSERT IGNORE INTO user_addresses (id, user_id, full_name, phone, street, ward, district, city, is_default, type, created_at, updated_at, is_deleted)
-- caff = c,a,f,f — tất cả là hex hợp lệ
VALUES ('caff0001-0000-0000-0000-000000000001', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
        'Trần Văn Hùng', '0987654321', '123 Nguyễn Huệ', 'Phường Bến Nghé', 'Quận 1',
        'TP. Hồ Chí Minh', true, 'HOME', NOW(6), NOW(6), false);

-- =============================================================================
-- PHẦN 4: THƯƠNG HIỆU — LOCAL BRAND VIỆT NAM
-- prefix b00b = b,0,0,b — tất cả hex hợp lệ
-- =============================================================================
INSERT IGNORE INTO brands (id, name, logo_url, description, is_active, created_at, updated_at, is_deleted)
VALUES
  ('b00b0001-0000-0000-0000-000000000001', 'Seven.AM',     NULL, 'Thương hiệu thời trang nữ công sở nổi tiếng tại Việt Nam',                       true, NOW(6), NOW(6), false),
  ('b00b0002-0000-0000-0000-000000000001', 'HNOSS',        NULL, 'Local brand thời trang nữ phong cách tối giản, hiện đại',                         true, NOW(6), NOW(6), false),
  ('b00b0003-0000-0000-0000-000000000001', 'Tokyolife',    NULL, 'Thương hiệu thời trang nam nữ phổ thông Việt Nam',                                 true, NOW(6), NOW(6), false),
  ('b00b0004-0000-0000-0000-000000000001', 'Routine',      NULL, 'Local brand streetwear Việt Nam dành cho giới trẻ',                                true, NOW(6), NOW(6), false),
  ('b00b0005-0000-0000-0000-000000000001', 'Gumac',        NULL, 'Thương hiệu thời trang nữ trẻ trung, giá tầm trung',                               true, NOW(6), NOW(6), false),
  ('b00b0006-0000-0000-0000-000000000001', 'Yame',         NULL, 'Thương hiệu streetwear Việt Nam, nổi bật với hoodies và graphic tee',              true, NOW(6), NOW(6), false),
  ('b00b0007-0000-0000-0000-000000000001', 'Phuong Linh',  NULL, 'Thương hiệu thời trang cao cấp Việt Nam, chuyên váy đầm và áo dài',                true, NOW(6), NOW(6), false),
  ('b00b0008-0000-0000-0000-000000000001', 'Ivy Moda',     NULL, 'Thương hiệu thời trang nữ công sở, phong cách thanh lịch',                        true, NOW(6), NOW(6), false),
  ('b00b0009-0000-0000-0000-000000000001', 'Canifa',       NULL, 'Thương hiệu thời trang gia đình Việt, đặc biệt nổi tiếng với len và knitwear',    true, NOW(6), NOW(6), false),
  ('b00b000a-0000-0000-0000-000000000001', 'Owen',         NULL, 'Thương hiệu thời trang nam công sở Việt Nam',                                     true, NOW(6), NOW(6), false);

-- =============================================================================
-- PHẦN 5: SẢN PHẨM TEST (giá ≤ 20,000đ)
-- prefix cafe=consignment requests, fade=items, dead=contracts, face=products
-- babe=product_categories   (c,a,f,e,d,b — tất cả hex hợp lệ)
-- =============================================================================

-- 5.1 CONSIGNMENT REQUESTS (café0001–café0008)
INSERT IGNORE INTO consignment_requests (id, consignor_id, code, status, note, created_at, updated_at, is_deleted)
VALUES
  ('cafe0001-0000-0000-0000-000000000001','cccccccc-cccc-cccc-cccc-cccccccccccc','CGN-2025-0001','RECEIVED',NULL,NOW(6),NOW(6),false),
  ('cafe0002-0000-0000-0000-000000000001','cccccccc-cccc-cccc-cccc-cccccccccccc','CGN-2025-0002','RECEIVED',NULL,NOW(6),NOW(6),false),
  ('cafe0003-0000-0000-0000-000000000001','cccccccc-cccc-cccc-cccc-cccccccccccc','CGN-2025-0003','RECEIVED',NULL,NOW(6),NOW(6),false),
  ('cafe0004-0000-0000-0000-000000000001','cccccccc-cccc-cccc-cccc-cccccccccccc','CGN-2025-0004','RECEIVED',NULL,NOW(6),NOW(6),false),
  ('cafe0005-0000-0000-0000-000000000001','cccccccc-cccc-cccc-cccc-cccccccccccc','CGN-2025-0005','RECEIVED',NULL,NOW(6),NOW(6),false),
  ('cafe0006-0000-0000-0000-000000000001','cccccccc-cccc-cccc-cccc-cccccccccccc','CGN-2025-0006','RECEIVED',NULL,NOW(6),NOW(6),false),
  ('cafe0007-0000-0000-0000-000000000001','cccccccc-cccc-cccc-cccc-cccccccccccc','CGN-2025-0007','RECEIVED',NULL,NOW(6),NOW(6),false),
  ('cafe0008-0000-0000-0000-000000000001','cccccccc-cccc-cccc-cccc-cccccccccccc','CGN-2025-0008','RECEIVED',NULL,NOW(6),NOW(6),false);

-- 5.2 CONSIGNMENT ITEMS (fade0001–fade0008)
INSERT IGNORE INTO consignment_items (id, request_id, suggested_name, suggested_price, original_price,
  suggested_brand_id, suggested_category_id, condition_note, status, rejection_reason, created_at, updated_at, is_deleted)
VALUES
  ('fade0001-0000-0000-0000-000000000001','cafe0001-0000-0000-0000-000000000001',
   'Seven.AM Áo sơ mi lụa tay dài màu trắng', 15000.0000, 490000.0000,
   'b00b0001-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',
   'Tình trạng 95% — mặc 2 lần, còn tag','CONVERTED_TO_PRODUCT',NULL,NOW(6),NOW(6),false),

  ('fade0002-0000-0000-0000-000000000001','cafe0002-0000-0000-0000-000000000001',
   'HNOSS Quần wide-leg kẻ sọc be đen', 12000.0000, 650000.0000,
   'b00b0002-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',
   'Tình trạng 97% — chỉ mặc 1 lần','CONVERTED_TO_PRODUCT',NULL,NOW(6),NOW(6),false),

  ('fade0003-0000-0000-0000-000000000001','cafe0003-0000-0000-0000-000000000001',
   'Routine Hoodie Unisex Oversize Màu Đen', 18000.0000, 450000.0000,
   'b00b0004-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',
   'Tình trạng 90% — đã qua giặt, không phai','CONVERTED_TO_PRODUCT',NULL,NOW(6),NOW(6),false),

  ('fade0004-0000-0000-0000-000000000001','cafe0004-0000-0000-0000-000000000001',
   'Gumac Váy midi hoa nhí tay phồng', 9000.0000, 380000.0000,
   'b00b0005-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',
   'Tình trạng 93% — còn đẹp, nhẹ li nhỏ vạt','CONVERTED_TO_PRODUCT',NULL,NOW(6),NOW(6),false),

  ('fade0005-0000-0000-0000-000000000001','cafe0005-0000-0000-0000-000000000001',
   'Yame Graphic Tee In Logo Vintage', 7000.0000, 250000.0000,
   'b00b0006-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',
   'Tình trạng 92% — mặc vài lần, không xù','CONVERTED_TO_PRODUCT',NULL,NOW(6),NOW(6),false),

  ('fade0006-0000-0000-0000-000000000001','cafe0006-0000-0000-0000-000000000001',
   'Ivy Moda Túi xách tay da PU đen size nhỏ', 14000.0000, 550000.0000,
   'b00b0008-0000-0000-0000-000000000001','c1000001-0001-0000-0000-000000000000',
   'Tình trạng 96% — gần mới, còn dustbag','CONVERTED_TO_PRODUCT',NULL,NOW(6),NOW(6),false),

  ('fade0007-0000-0000-0000-000000000001','cafe0007-0000-0000-0000-000000000001',
   'Canifa Áo len cổ tròn màu caramel size M', 11000.0000, 420000.0000,
   'b00b0009-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',
   'Tình trạng 94% — mặc 3 lần, không xù lông','CONVERTED_TO_PRODUCT',NULL,NOW(6),NOW(6),false),

  ('fade0008-0000-0000-0000-000000000001','cafe0008-0000-0000-0000-000000000001',
   'Owen Quần tây slim-fit xám nhạt size 32', 16000.0000, 680000.0000,
   'b00b000a-0000-0000-0000-000000000001','c2000003-0003-0000-0000-000000000000',
   'Tình trạng 95% — còn phẳng, chưa sửa','CONVERTED_TO_PRODUCT',NULL,NOW(6),NOW(6),false);

-- 5.3 CONSIGNMENT CONTRACTS (dead0001–dead0008, commission 15%)
INSERT IGNORE INTO consignment_contracts (id, request_id, commission_rate, agreed_price, signed_at,
  signed_by_user_id, signed_by_name, signature_method, signature_ip_address, signature_user_agent,
  signature_hash, valid_until, status, created_at, updated_at, is_deleted)
VALUES
  ('dead0001-0000-0000-0000-000000000001','cafe0001-0000-0000-0000-000000000001',0.1500, 15000.0000,NOW(6),'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Store Manager','DIGITAL','127.0.0.1','Mozilla/5.0',SHA2('dead0001',256),DATE_ADD(NOW(6),INTERVAL 180 DAY),'SIGNED',NOW(6),NOW(6),false),
  ('dead0002-0000-0000-0000-000000000001','cafe0002-0000-0000-0000-000000000001',0.1500, 12000.0000,NOW(6),'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Store Manager','DIGITAL','127.0.0.1','Mozilla/5.0',SHA2('dead0002',256),DATE_ADD(NOW(6),INTERVAL 180 DAY),'SIGNED',NOW(6),NOW(6),false),
  ('dead0003-0000-0000-0000-000000000001','cafe0003-0000-0000-0000-000000000001',0.1500, 18000.0000,NOW(6),'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Store Manager','DIGITAL','127.0.0.1','Mozilla/5.0',SHA2('dead0003',256),DATE_ADD(NOW(6),INTERVAL 180 DAY),'SIGNED',NOW(6),NOW(6),false),
  ('dead0004-0000-0000-0000-000000000001','cafe0004-0000-0000-0000-000000000001',0.1500,  9000.0000,NOW(6),'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Store Manager','DIGITAL','127.0.0.1','Mozilla/5.0',SHA2('dead0004',256),DATE_ADD(NOW(6),INTERVAL 180 DAY),'SIGNED',NOW(6),NOW(6),false),
  ('dead0005-0000-0000-0000-000000000001','cafe0005-0000-0000-0000-000000000001',0.1500,  7000.0000,NOW(6),'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Store Manager','DIGITAL','127.0.0.1','Mozilla/5.0',SHA2('dead0005',256),DATE_ADD(NOW(6),INTERVAL 180 DAY),'SIGNED',NOW(6),NOW(6),false),
  ('dead0006-0000-0000-0000-000000000001','cafe0006-0000-0000-0000-000000000001',0.1500, 14000.0000,NOW(6),'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Store Manager','DIGITAL','127.0.0.1','Mozilla/5.0',SHA2('dead0006',256),DATE_ADD(NOW(6),INTERVAL 180 DAY),'SIGNED',NOW(6),NOW(6),false),
  ('dead0007-0000-0000-0000-000000000001','cafe0007-0000-0000-0000-000000000001',0.1500, 11000.0000,NOW(6),'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Store Manager','DIGITAL','127.0.0.1','Mozilla/5.0',SHA2('dead0007',256),DATE_ADD(NOW(6),INTERVAL 180 DAY),'SIGNED',NOW(6),NOW(6),false),
  ('dead0008-0000-0000-0000-000000000001','cafe0008-0000-0000-0000-000000000001',0.1500, 16000.0000,NOW(6),'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Store Manager','DIGITAL','127.0.0.1','Mozilla/5.0',SHA2('dead0008',256),DATE_ADD(NOW(6),INTERVAL 180 DAY),'SIGNED',NOW(6),NOW(6),false);

-- 5.4 PRODUCTS (face0001–face0008, 7×SELLING + 1×READY_TO_LIST)
INSERT IGNORE INTO products (id, consignment_item_id, brand_id, sku, name, description,
  condition_percent, original_price, sale_price, status, reserved_until, created_at, updated_at, is_deleted)
VALUES
  ('face0001-0000-0000-0000-000000000001','fade0001-0000-0000-0000-000000000001','b00b0001-0000-0000-0000-000000000001',
   'SKU-7AM-SML-001','Seven.AM Áo sơ mi lụa tay dài màu trắng',
   'Áo sơ mi lụa tay dài của Seven.AM, phong cách công sở thanh lịch. Chất lụa mịn, thoáng mát. Size M. Màu trắng ngà. Mặc 2 lần còn tag. Tình trạng 95%.',
   95.00,490000.0000,15000.0000,'SELLING',NULL,NOW(6),NOW(6),false),

  ('face0002-0000-0000-0000-000000000001','fade0002-0000-0000-0000-000000000001','b00b0002-0000-0000-0000-000000000001',
   'SKU-HNS-QWL-001','HNOSS Quần wide-leg kẻ sọc be đen',
   'Quần wide-leg phong cách tối giản của HNOSS. Họa tiết kẻ sọc be-đen thanh lịch, chất vải dày dặn không nhăn. Size S. Chỉ mặc 1 lần. Tình trạng 97%.',
   97.00,650000.0000,12000.0000,'SELLING',NULL,NOW(6),NOW(6),false),

  ('face0003-0000-0000-0000-000000000001','fade0003-0000-0000-0000-000000000001','b00b0004-0000-0000-0000-000000000001',
   'SKU-RTN-HOD-001','Routine Hoodie Unisex Oversize Màu Đen',
   'Hoodie unisex oversize của Routine, chất nỉ bông dày, in thêu logo ngực trái. Màu đen cơ bản dễ phối. Size L (mặc như M oversize). Tình trạng 90%.',
   90.00,450000.0000,18000.0000,'SELLING',NULL,NOW(6),NOW(6),false),

  ('face0004-0000-0000-0000-000000000001','fade0004-0000-0000-0000-000000000001','b00b0005-0000-0000-0000-000000000001',
   'SKU-GMC-VMH-001','Gumac Váy midi hoa nhí tay phồng',
   'Váy midi vải voan hoa nhí của Gumac, tay phồng nhẹ vintage. Màu nền kem điểm hoa nhỏ xanh. Size M. Tình trạng 93% — li nhỏ ở vạt gần không thấy.',
   93.00,380000.0000,9000.0000,'SELLING',NULL,NOW(6),NOW(6),false),

  ('face0005-0000-0000-0000-000000000001','fade0005-0000-0000-0000-000000000001','b00b0006-0000-0000-0000-000000000001',
   'SKU-YME-GTE-001','Yame Graphic Tee In Logo Vintage',
   'Áo thun unisex của Yame, in logo vintage tông nâu-cam trên nền trắng. Chất cotton 100% thoáng mát. Size M. Mặc vài lần, không xù, không bay màu. Tình trạng 92%.',
   92.00,250000.0000,7000.0000,'SELLING',NULL,NOW(6),NOW(6),false),

  ('face0006-0000-0000-0000-000000000001','fade0006-0000-0000-0000-000000000001','b00b0008-0000-0000-0000-000000000001',
   'SKU-IVY-TXD-001','Ivy Moda Túi xách tay da PU đen size nhỏ',
   'Túi xách tay của Ivy Moda, da PU đen mềm, quai xách cứng cáp. Kích thước nhỏ nhắn phù hợp đi chơi, đi làm. Còn kèm dustbag gốc. Tình trạng 96%.',
   96.00,550000.0000,14000.0000,'SELLING',NULL,NOW(6),NOW(6),false),

  ('face0007-0000-0000-0000-000000000001','fade0007-0000-0000-0000-000000000001','b00b0009-0000-0000-0000-000000000001',
   'SKU-CNF-ALC-001','Canifa Áo len cổ tròn màu caramel size M',
   'Áo len cổ tròn của Canifa, chất len mềm mại ấm áp, màu caramel dễ phối đồ. Size M vừa dáng. Mặc 3 lần, không xù lông, không giãn. Tình trạng 94%.',
   94.00,420000.0000,11000.0000,'SELLING',NULL,NOW(6),NOW(6),false),

  ('face0008-0000-0000-0000-000000000001','fade0008-0000-0000-0000-000000000001','b00b000a-0000-0000-0000-000000000001',
   'SKU-OWN-QTX-001','Owen Quần tây slim-fit xám nhạt size 32',
   'Quần tây nam slim-fit của Owen, màu xám nhạt sang trọng phù hợp công sở. Vải polyester-rayon không nhăn. Size 32. Tình trạng 95% — còn phẳng, chưa sửa.',
   95.00,680000.0000,16000.0000,'READY_TO_LIST',NULL,NOW(6),NOW(6),false);

-- 5.5 PRODUCT CATEGORIES (babe = b,a,b,e — tất cả hex hợp lệ)
-- primary: last segment 0001 | secondary/parent: last segment 0002
INSERT IGNORE INTO product_categories (id, product_id, category_id, is_primary, created_at, updated_at, is_deleted)
VALUES
  -- face0001: Nữ > Quần áo (primary) + Nữ
  ('babe0001-0000-0000-0000-000000000001','face0001-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',true, NOW(6),NOW(6),false),
  ('babe0001-0000-0000-0000-000000000002','face0001-0000-0000-0000-000000000001','c1000000-0000-0000-0000-000000000000',false,NOW(6),NOW(6),false),
  -- face0002: Nữ > Quần áo
  ('babe0002-0000-0000-0000-000000000001','face0002-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',true, NOW(6),NOW(6),false),
  ('babe0002-0000-0000-0000-000000000002','face0002-0000-0000-0000-000000000001','c1000000-0000-0000-0000-000000000000',false,NOW(6),NOW(6),false),
  -- face0003: Nữ > Quần áo + Nam > Quần áo (unisex)
  ('babe0003-0000-0000-0000-000000000001','face0003-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',true, NOW(6),NOW(6),false),
  ('babe0003-0000-0000-0000-000000000002','face0003-0000-0000-0000-000000000001','c2000003-0003-0000-0000-000000000000',false,NOW(6),NOW(6),false),
  -- face0004: Nữ > Quần áo
  ('babe0004-0000-0000-0000-000000000001','face0004-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',true, NOW(6),NOW(6),false),
  ('babe0004-0000-0000-0000-000000000002','face0004-0000-0000-0000-000000000001','c1000000-0000-0000-0000-000000000000',false,NOW(6),NOW(6),false),
  -- face0005: Nữ > Quần áo + Nam > Quần áo
  ('babe0005-0000-0000-0000-000000000001','face0005-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',true, NOW(6),NOW(6),false),
  ('babe0005-0000-0000-0000-000000000002','face0005-0000-0000-0000-000000000001','c2000003-0003-0000-0000-000000000000',false,NOW(6),NOW(6),false),
  -- face0006: Nữ > Túi xách
  ('babe0006-0000-0000-0000-000000000001','face0006-0000-0000-0000-000000000001','c1000001-0001-0000-0000-000000000000',true, NOW(6),NOW(6),false),
  ('babe0006-0000-0000-0000-000000000002','face0006-0000-0000-0000-000000000001','c1000000-0000-0000-0000-000000000000',false,NOW(6),NOW(6),false),
  -- face0007: Nữ > Quần áo
  ('babe0007-0000-0000-0000-000000000001','face0007-0000-0000-0000-000000000001','c1000003-0003-0000-0000-000000000000',true, NOW(6),NOW(6),false),
  ('babe0007-0000-0000-0000-000000000002','face0007-0000-0000-0000-000000000001','c1000000-0000-0000-0000-000000000000',false,NOW(6),NOW(6),false),
  -- face0008: Nam > Quần áo
  ('babe0008-0000-0000-0000-000000000001','face0008-0000-0000-0000-000000000001','c2000003-0003-0000-0000-000000000000',true, NOW(6),NOW(6),false),
  ('babe0008-0000-0000-0000-000000000002','face0008-0000-0000-0000-000000000001','c2000000-0000-0000-0000-000000000000',false,NOW(6),NOW(6),false);

-- =============================================================================
-- PHẦN 6: VOUCHER TEST (feed = f,e,e,d — tất cả hex hợp lệ)
-- =============================================================================
INSERT IGNORE INTO vouchers (id, code, discount_type, discount_value, min_order_value, max_discount,
  start_date, end_date, usage_limit, used_count, status, created_at, updated_at, is_deleted)
VALUES
  ('feed0001-0000-0000-0000-000000000001','WELCOME10','PERCENT',10.0000,10000.0000,2000.0000,
   NOW(6),DATE_ADD(NOW(6),INTERVAL 90 DAY),100,0,'ACTIVE',NOW(6),NOW(6),false),
  ('feed0002-0000-0000-0000-000000000001','FLAT2K','FIXED_AMOUNT',2000.0000,15000.0000,NULL,
   NOW(6),DATE_ADD(NOW(6),INTERVAL 30 DAY),50,0,'ACTIVE',NOW(6),NOW(6),false);

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- VERIFY
-- =============================================================================
-- SELECT p.name, p.sku, p.sale_price, p.status, b.name brand
-- FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.is_deleted = false;
--
-- SELECT username, email FROM users
-- WHERE id IN ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','cccccccc-cccc-cccc-cccc-cccccccccccc','dddddddd-dddd-dddd-dddd-dddddddddddd');


