-- Bulk test data for local Fashion Consignment System MySQL database.
-- Safe to run repeatedly: generated rows use fixed ID prefixes and INSERT IGNORE.

SET FOREIGN_KEY_CHECKS = 0;

INSERT IGNORE INTO brands (id, name, logo_url, description, is_active, created_at, updated_at, is_deleted) VALUES
('bd000000-0000-0000-0000-000000000001', 'Chanel', 'https://picsum.photos/seed/brand-chanel/240/120', 'Thuong hieu thoi trang xa xi Phap', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000002', 'Prada', 'https://picsum.photos/seed/brand-prada/240/120', 'Thoi trang cao cap Y', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000003', 'Burberry', 'https://picsum.photos/seed/brand-burberry/240/120', 'Thuong hieu Anh voi hoa tiet ke bieu tuong', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000004', 'Hermes', 'https://picsum.photos/seed/brand-hermes/240/120', 'Do da va phu kien cao cap', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000005', 'Balenciaga', 'https://picsum.photos/seed/brand-balenciaga/240/120', 'Thoi trang duong dai cao cap', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000006', 'Saint Laurent', 'https://picsum.photos/seed/brand-ysl/240/120', 'Phong cach Paris hien dai', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000007', 'Celine', 'https://picsum.photos/seed/brand-celine/240/120', 'Toi gian va thanh lich', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000008', 'Fendi', 'https://picsum.photos/seed/brand-fendi/240/120', 'Tui xach va phu kien Y', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000009', 'Bottega Veneta', 'https://picsum.photos/seed/brand-bottega/240/120', 'Ky thuat dan da Intrecciato', true, NOW(6), NOW(6), false),
('bd000000-0000-0000-0000-000000000010', 'Versace', 'https://picsum.photos/seed/brand-versace/240/120', 'Phong cach Y noi bat', true, NOW(6), NOW(6), false);

INSERT IGNORE INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted) VALUES
('ca000000-0000-0000-0000-000000000001', NULL, 'Phụ kiện', 'accessories', true, NOW(6), NOW(6), false),
('ca000000-0000-0000-0000-000000000002', 'ca000000-0000-0000-0000-000000000001', 'Ví', 'wallets', true, NOW(6), NOW(6), false),
('ca000000-0000-0000-0000-000000000003', 'ca000000-0000-0000-0000-000000000001', 'Đồng hồ', 'watches', true, NOW(6), NOW(6), false),
('ca000000-0000-0000-0000-000000000004', 'ca000000-0000-0000-0000-000000000001', 'Thắt lưng', 'belts', true, NOW(6), NOW(6), false),
('ca000000-0000-0000-0000-000000000005', NULL, 'Quần áo', 'clothing', true, NOW(6), NOW(6), false),
('ca000000-0000-0000-0000-000000000006', 'ca000000-0000-0000-0000-000000000005', 'Áo khoác', 'outerwear', true, NOW(6), NOW(6), false),
('ca000000-0000-0000-0000-000000000007', 'ca000000-0000-0000-0000-000000000005', 'Đầm', 'dresses', true, NOW(6), NOW(6), false),
('ca000000-0000-0000-0000-000000000008', 'ca000000-0000-0000-0000-000000000005', 'Sneaker', 'sneakers', true, NOW(6), NOW(6), false);

INSERT IGNORE INTO vouchers (id, code, discount_type, discount_value, min_order_value, max_discount, start_date, end_date, usage_limit, used_count, status, created_at, updated_at, is_deleted) VALUES
('vc000000-0000-0000-0000-000000000001', 'TEST50K', 'FIXED_AMOUNT', 50000.0000, 500000.0000, 50000.0000, DATE_SUB(NOW(6), INTERVAL 1 DAY), DATE_ADD(NOW(6), INTERVAL 1 YEAR), 1000, 0, 'ACTIVE', NOW(6), NOW(6), false),
('vc000000-0000-0000-0000-000000000002', 'VIP15', 'PERCENT', 15.0000, 3000000.0000, 1500000.0000, DATE_SUB(NOW(6), INTERVAL 1 DAY), DATE_ADD(NOW(6), INTERVAL 1 YEAR), 500, 0, 'ACTIVE', NOW(6), NOW(6), false),
('vc000000-0000-0000-0000-000000000003', 'FREESHIP', 'FIXED_AMOUNT', 30000.0000, 0.0000, 30000.0000, DATE_SUB(NOW(6), INTERVAL 1 DAY), DATE_ADD(NOW(6), INTERVAL 6 MONTH), 2000, 0, 'ACTIVE', NOW(6), NOW(6), false);

UPDATE categories SET name = CASE id
  WHEN 'ca000000-0000-0000-0000-000000000001' THEN 'Phụ kiện'
  WHEN 'ca000000-0000-0000-0000-000000000002' THEN 'Ví'
  WHEN 'ca000000-0000-0000-0000-000000000003' THEN 'Đồng hồ'
  WHEN 'ca000000-0000-0000-0000-000000000004' THEN 'Thắt lưng'
  WHEN 'ca000000-0000-0000-0000-000000000005' THEN 'Quần áo'
  WHEN 'ca000000-0000-0000-0000-000000000006' THEN 'Áo khoác'
  WHEN 'ca000000-0000-0000-0000-000000000007' THEN 'Đầm'
  WHEN 'ca000000-0000-0000-0000-000000000008' THEN 'Sneaker'
  ELSE name
END
WHERE id LIKE 'ca000000-0000-0000-0000-%';

UPDATE brands SET description = CASE name
  WHEN 'Chanel' THEN 'Thuong hieu thoi trang xa xi Phap'
  WHEN 'Prada' THEN 'Thoi trang cao cap Y'
  WHEN 'Burberry' THEN 'Thuong hieu Anh voi hoa tiet ke bieu tuong'
  WHEN 'Hermes' THEN 'Do da va phu kien cao cap'
  WHEN 'Balenciaga' THEN 'Thoi trang duong dai cao cap'
  WHEN 'Saint Laurent' THEN 'Phong cach Paris hien dai'
  WHEN 'Celine' THEN 'Toi gian va thanh lich'
  WHEN 'Fendi' THEN 'Tui xach va phu kien Y'
  WHEN 'Bottega Veneta' THEN 'Ky thuat dan da Intrecciato'
  WHEN 'Versace' THEN 'Phong cach Y noi bat'
  ELSE description
END
WHERE id LIKE 'bd000000-0000-0000-0000-%';

DROP PROCEDURE IF EXISTS seed_bulk_test_data;
DELIMITER $$
CREATE PROCEDURE seed_bulk_test_data()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE n CHAR(4);
  DECLARE suffix CHAR(12);
  DECLARE buyer_id CHAR(36);
  DECLARE seller_id CHAR(36);
  DECLARE address_id CHAR(36);
  DECLARE cart_id CHAR(36);
  DECLARE request_id CHAR(36);
  DECLARE item_id CHAR(36);
  DECLARE product_id CHAR(36);
  DECLARE order_id CHAR(36);
  DECLARE wallet_id CHAR(36);
  DECLARE product_status VARCHAR(30);
  DECLARE request_status VARCHAR(30);
  DECLARE order_status VARCHAR(30);
  DECLARE brand_id CHAR(36);
  DECLARE category_id CHAR(36);
  DECLARE price DECIMAL(19,4);
  DECLARE discount DECIMAL(19,4);

  WHILE i <= 300 DO
    SET n = LPAD(i, 4, '0');
    SET suffix = LPAD(i, 12, '0');
    SET buyer_id = CONCAT('71000000-0000-0000-0000-', suffix);
    SET seller_id = CONCAT('72000000-0000-0000-0000-', suffix);
    SET address_id = CONCAT('73000000-0000-0000-0000-', suffix);
    SET cart_id = CONCAT('74000000-0000-0000-0000-', suffix);
    SET request_id = CONCAT('75000000-0000-0000-0000-', suffix);
    SET item_id = CONCAT('76000000-0000-0000-0000-', suffix);
    SET product_id = CONCAT('77000000-0000-0000-0000-', suffix);
    SET order_id = CONCAT('78000000-0000-0000-0000-', suffix);
    SET wallet_id = CONCAT('79000000-0000-0000-0000-', suffix);
    SET price = 1200000 + (i * 137000);
    SET discount = IF(MOD(i, 5) = 0, 150000, 0);
    SET product_status = CASE
      WHEN FLOOR((i - 1) / 8) < 10 THEN 'SELLING'
      WHEN MOD(i, 8) = 0 THEN 'SELLING'
      WHEN MOD(i, 8) = 1 THEN 'READY_TO_LIST'
      WHEN MOD(i, 8) = 2 THEN 'RESERVED'
      WHEN MOD(i, 8) = 3 THEN 'SOLD'
      WHEN MOD(i, 8) = 4 THEN 'HOLD'
      WHEN MOD(i, 8) = 5 THEN 'DRAFT'
      WHEN MOD(i, 8) = 6 THEN 'RETURNED'
      ELSE 'ARCHIVED'
    END;
    SET request_status = CASE MOD(i, 7)
      WHEN 0 THEN 'SUBMITTED'
      WHEN 1 THEN 'UNDER_REVIEW'
      WHEN 2 THEN 'APPROVED'
      WHEN 3 THEN 'RECEIVED'
      WHEN 4 THEN 'REJECTED'
      WHEN 5 THEN 'DRAFT'
      ELSE 'CANCELLED'
    END;
    SET order_status = CASE MOD(i, 9)
      WHEN 0 THEN 'PENDING_PAYMENT'
      WHEN 1 THEN 'PAID'
      WHEN 2 THEN 'CONFIRMED'
      WHEN 3 THEN 'PACKING'
      WHEN 4 THEN 'SHIPPED'
      WHEN 5 THEN 'DELIVERED'
      WHEN 6 THEN 'COMPLETED'
      WHEN 7 THEN 'CANCELLED'
      ELSE 'REFUNDED'
    END;
    SET brand_id = CONCAT('bd000000-0000-0000-0000-', LPAD(((i - 1) % 10) + 1, 12, '0'));
    SET category_id = CONCAT('ca000000-0000-0000-0000-', LPAD(((i - 1) % 8) + 1, 12, '0'));

    INSERT IGNORE INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
    VALUES
      (buyer_id, CONCAT('test_buyer_', n), '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', CONCAT('test_buyer_', n, '@example.com'), CONCAT('Test Buyer ', n), CONCAT('091', LPAD(i, 7, '0')), 'ACTIVE', NOW(6), NOW(6), false),
      (seller_id, CONCAT('test_seller_', n), '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', CONCAT('test_seller_', n, '@example.com'), CONCAT('Test Seller ', n), CONCAT('092', LPAD(i, 7, '0')), 'ACTIVE', NOW(6), NOW(6), false);

    INSERT IGNORE INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
    VALUES
      (CONCAT('7a000000-0000-0000-0000-', suffix), buyer_id, 'LOCAL', CONCAT('test_buyer_', n, '@example.com'), CONCAT('test_buyer_', n, '@example.com'), '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6)),
      (CONCAT('7b000000-0000-0000-0000-', suffix), seller_id, 'LOCAL', CONCAT('test_seller_', n, '@example.com'), CONCAT('test_seller_', n, '@example.com'), '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', true, true, NOW(6), NOW(6));

    INSERT IGNORE INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
    VALUES
      (CONCAT('7c000000-0000-0000-0000-', suffix), buyer_id, '44444444-4444-4444-4444-444444444444', NOW(6), NOW(6), false),
      (CONCAT('7d000000-0000-0000-0000-', suffix), seller_id, '33333333-3333-3333-3333-333333333333', NOW(6), NOW(6), false);

    INSERT IGNORE INTO user_addresses (id, user_id, full_name, phone, street, ward, district, city, is_default, type, created_at, updated_at, is_deleted)
    VALUES (address_id, buyer_id, CONCAT('Test Customer ', n), CONCAT('091', LPAD(i, 7, '0')), CONCAT(i, ' Nguyen Trai'), CONCAT('Ward ', ((i - 1) % 12) + 1), CONCAT('District ', ((i - 1) % 10) + 1), 'Ho Chi Minh City', true, IF(MOD(i, 2) = 0, 'HOME', 'OFFICE'), NOW(6), NOW(6), false);

    INSERT IGNORE INTO wallets (id, user_id, balance, available_balance, bank_name, bank_account_name, bank_account_number, created_at, updated_at, is_deleted)
    VALUES (wallet_id, seller_id, price * 2, price * 2, 'Vietcombank', CONCAT('TEST SELLER ', n), CONCAT('9704', LPAD(i, 10, '0')), NOW(6), NOW(6), false);

    INSERT IGNORE INTO consignment_requests (id, consignor_id, code, status, note, created_at, updated_at, is_deleted)
    VALUES (request_id, seller_id, CONCAT('CR-TEST-', n), request_status, CONCAT('Bulk test consignment request ', n), DATE_SUB(NOW(6), INTERVAL (i % 90) DAY), NOW(6), false);

    INSERT IGNORE INTO consignment_items (id, request_id, suggested_name, suggested_price, condition_note, status, created_at, updated_at, is_deleted)
    VALUES (item_id, request_id, CONCAT(CASE MOD(i, 6) WHEN 0 THEN 'Handbag' WHEN 1 THEN 'Leather wallet' WHEN 2 THEN 'Sneaker' WHEN 3 THEN 'Jacket' WHEN 4 THEN 'Watch' ELSE 'Belt' END, ' premium test ', n), price, CONCAT('Condition ', 70 + (i % 30), ' percent, bulk test data'), IF(product_status IN ('SELLING','SOLD','RESERVED','HOLD','RETURNED','ARCHIVED'), 'CONVERTED_TO_PRODUCT', 'ACCEPTED'), NOW(6), NOW(6), false);

    INSERT IGNORE INTO products (id, consignment_item_id, brand_id, sku, name, description, condition_percent, original_price, sale_price, status, reserved_until, created_at, updated_at, is_deleted)
    VALUES (product_id, item_id, brand_id, CONCAT('SKU-TEST-', n), CONCAT('Test fashion product ', n), CONCAT('Test product description ', n, ' for listing, search, filter, cart and order screens.'), 70 + (i % 30), price * 1.7, price, product_status, IF(product_status = 'RESERVED', DATE_ADD(NOW(6), INTERVAL 2 DAY), NULL), DATE_SUB(NOW(6), INTERVAL (i % 90) DAY), NOW(6), false);

    INSERT IGNORE INTO product_categories (id, product_id, category_id, is_primary, created_at, updated_at, is_deleted)
    VALUES (CONCAT('7e000000-0000-0000-0000-', suffix), product_id, category_id, true, NOW(6), NOW(6), false);

    INSERT IGNORE INTO media_assets (id, owner_type, owner_id, media_type, url, display_order, is_primary, mime_type, size_bytes, uploaded_by, created_at, updated_at, is_deleted)
    VALUES
      (CONCAT('7f000000-0000-0000-0000-', suffix), 'PRODUCT', product_id, 'IMAGE', CONCAT('https://picsum.photos/seed/fcs-product-', n, '/900/1200'), 1, true, 'image/jpeg', 250000 + i, seller_id, NOW(6), NOW(6), false),
      (CONCAT('8f000000-0000-0000-0000-', suffix), 'PRODUCT', product_id, 'IMAGE', CONCAT('https://picsum.photos/seed/fcs-product-alt-', n, '/900/1200'), 2, false, 'image/jpeg', 260000 + i, seller_id, NOW(6), NOW(6), false);

    INSERT IGNORE INTO carts (id, user_id, session_id, created_at, updated_at, is_deleted)
    VALUES (cart_id, buyer_id, NULL, NOW(6), NOW(6), false);

    IF product_status IN ('SELLING','READY_TO_LIST','RESERVED') THEN
      INSERT IGNORE INTO cart_items (id, cart_id, product_id, created_at, updated_at, is_deleted)
      VALUES (CONCAT('81000000-0000-0000-0000-', suffix), cart_id, product_id, NOW(6), NOW(6), false);

      INSERT IGNORE INTO wishlist_items (id, user_id, product_id, created_at, updated_at)
      VALUES (CONCAT('82000000-0000-0000-0000-', suffix), buyer_id, product_id, NOW(6), NOW(6));
    END IF;

    IF MOD(i, 3) <> 0 THEN
      INSERT IGNORE INTO orders (id, buyer_id, order_code, sub_total, shipping_fee, discount_amount, total_amount, payment_method, shipping_address_id, status, tracking_number, shipping_provider, shipping_snapshot, created_at, updated_at, is_deleted)
      VALUES (order_id, buyer_id, CONCAT('FCS-TEST-', n), price, 30000.0000, discount, price + 30000 - discount, CASE MOD(i, 3) WHEN 0 THEN 'COD' WHEN 1 THEN 'BANK_TRANSFER' ELSE 'CREDIT_CARD' END, address_id, order_status, CONCAT('GHN', LPAD(i, 10, '0')), IF(MOD(i, 2) = 0, 'GHN', 'GHTK'), CONCAT('Test Customer ', n, ', ', i, ' Nguyen Trai, Ho Chi Minh City'), DATE_SUB(NOW(6), INTERVAL (i % 60) DAY), NOW(6), false);

      INSERT IGNORE INTO order_items (id, order_id, product_id, sku_snapshot, product_name_snapshot, price_at_purchase, condition_snapshot, created_at, updated_at, is_deleted)
      VALUES (CONCAT('83000000-0000-0000-0000-', suffix), order_id, product_id, CONCAT('SKU-TEST-', n), CONCAT('Test fashion product ', n), price, CONCAT(70 + (i % 30), '%'), NOW(6), NOW(6), false);

      INSERT IGNORE INTO order_status_history (id, order_id, from_status, to_status, changed_by, reason, created_at, updated_at)
      VALUES (CONCAT('84000000-0000-0000-0000-', suffix), order_id, 'PENDING_PAYMENT', order_status, seller_id, 'Bulk test order status data', NOW(6), NOW(6));

      INSERT IGNORE INTO product_reviews (id, product_id, buyer_id, rating, comment, verified_purchase, created_at, updated_at, is_deleted)
      VALUES (CONCAT('85000000-0000-0000-0000-', suffix), product_id, buyer_id, (i % 5) + 1, CONCAT('Bulk test review for product ', n, ': item matches description and UI displays correctly.'), true, NOW(6), NOW(6), false);

      INSERT IGNORE INTO wallet_transactions (id, wallet_id, order_id, amount, type, status, reference_type, description, created_at, updated_at)
      VALUES (CONCAT('86000000-0000-0000-0000-', suffix), wallet_id, order_id, price * 0.9, 'SALE_REVENUE', 'POSTED', 'ORDER', CONCAT('Bulk test revenue from order FCS-TEST-', n), NOW(6), NOW(6));
    END IF;

    INSERT IGNORE INTO warehouse_logs (id, product_id, location, action_type, note, created_at, updated_at)
    VALUES (CONCAT('87000000-0000-0000-0000-', suffix), product_id, CONCAT('Shelf-', CHAR(65 + (i % 5)), '-', ((i - 1) % 20) + 1), 'IN', 'Bulk test stock-in', NOW(6), NOW(6));

    INSERT IGNORE INTO product_status_history (id, product_id, from_status, to_status, changed_by, reason, created_at, updated_at)
    VALUES (CONCAT('88000000-0000-0000-0000-', suffix), product_id, NULL, product_status, seller_id, 'Bulk test data initialization', NOW(6), NOW(6));

    INSERT IGNORE INTO activity_logs (id, user_id, action, entity_name, entity_id, ip_address, created_at, updated_at)
    VALUES (CONCAT('89000000-0000-0000-0000-', suffix), seller_id, 'CREATE', 'Product', product_id, '127.0.0.1', NOW(6), NOW(6));

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL seed_bulk_test_data();
DROP PROCEDURE seed_bulk_test_data;

SET FOREIGN_KEY_CHECKS = 1;
