-- Comprehensive Sample Data for Fashion Consignment System
-- This script covers ALL tables with valid 36-character hexadecimal UUIDs.
-- Note: Passwords are BCrypt hashes for 'password123'

SET FOREIGN_KEY_CHECKS = 0;

-- Truncate existing data
TRUNCATE TABLE activity_logs;
TRUNCATE TABLE chat_messages;
TRUNCATE TABLE conversations;
TRUNCATE TABLE user_notifications;
TRUNCATE TABLE notifications;
TRUNCATE TABLE product_reviews;
TRUNCATE TABLE return_status_history;
TRUNCATE TABLE return_requests;
TRUNCATE TABLE withdrawal_status_history;
TRUNCATE TABLE withdrawal_requests;
TRUNCATE TABLE wallet_transactions;
TRUNCATE TABLE wallets;
TRUNCATE TABLE order_status_history;
TRUNCATE TABLE voucher_usages;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE vouchers;
TRUNCATE TABLE product_categories;
TRUNCATE TABLE warehouse_logs;
TRUNCATE TABLE media_assets;
TRUNCATE TABLE products;
TRUNCATE TABLE product_status_history;
TRUNCATE TABLE consignment_contracts;
TRUNCATE TABLE consignment_status_history;
TRUNCATE TABLE consignment_items;
TRUNCATE TABLE consignment_requests;
TRUNCATE TABLE categories;
TRUNCATE TABLE brands;
TRUNCATE TABLE system_settings;
TRUNCATE TABLE user_addresses;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE role_permissions;
TRUNCATE TABLE roles;
TRUNCATE TABLE permissions;
TRUNCATE TABLE users;
TRUNCATE TABLE wishlist_items;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE carts;

-- -----------------------------------------------------
-- 1. IAM - Permissions, Roles, Users
-- -----------------------------------------------------
INSERT INTO permissions (id, code, name, module, created_at, updated_at, is_deleted) VALUES
('11111111-1111-1111-1111-000000000001', 'IAM_USER_VIEW', 'View Users', 'IAM', NOW(), NOW(), false),
('11111111-1111-1111-1111-000000000002', 'IAM_USER_EDIT', 'Edit Users', 'IAM', NOW(), NOW(), false),
('11111111-1111-1111-1111-000000000003', 'CATALOG_MANAGE', 'Manage Catalog', 'CATALOG', NOW(), NOW(), false),
('11111111-1111-1111-1111-000000000004', 'ORDER_VIEW', 'View Orders', 'ORDER', NOW(), NOW(), false),
('11111111-1111-1111-1111-000000000005', 'CONSIGNMENT_APPROVE', 'Approve Consignment', 'CONSIGNMENT', NOW(), NOW(), false);

INSERT INTO roles (id, name, description, created_at, updated_at, is_deleted) VALUES
('11111111-1111-1111-1111-111111111111', 'ADMIN', 'Full system access', NOW(), NOW(), false),
('22222222-2222-2222-2222-222222222222', 'STAFF', 'Shop operations', NOW(), NOW(), false),
('33333333-3333-3333-3333-333333333333', 'CONSIGNOR', 'Sell items', NOW(), NOW(), false),
('44444444-4444-4444-4444-444444444444', 'BUYER', 'Buy items', NOW(), NOW(), false);

-- Assign Permissions to Roles
INSERT INTO role_permissions (id, role_id, permission_id, created_at, updated_at, is_deleted) VALUES
(UUID(), '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000001', NOW(), NOW(), false),
(UUID(), '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-000000000002', NOW(), NOW(), false),
(UUID(), '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000003', NOW(), NOW(), false),
(UUID(), '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-000000000004', NOW(), NOW(), false);

INSERT INTO users (id, username, password_hash, email, phone, status, created_at, updated_at, is_deleted) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', 'admin@fcs.com', '0901000001', 'ACTIVE', NOW(), NOW(), false),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'staff_ken', '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', 'ken@fcs.com', '0901000002', 'ACTIVE', NOW(), NOW(), false),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'consignor_jane', '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', 'jane@gmail.com', '0901000003', 'ACTIVE', NOW(), NOW(), false),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'buyer_bob', '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', 'bob@gmail.com', '0901000004', 'ACTIVE', NOW(), NOW(), false),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'buyer_alice', '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C', 'alice@gmail.com', '0901000005', 'ACTIVE', NOW(), NOW(), false);

INSERT INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted) VALUES
(UUID(), 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', NOW(), NOW(), false),
(UUID(), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', NOW(), NOW(), false),
(UUID(), 'cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', NOW(), NOW(), false),
(UUID(), 'dddddddd-dddd-dddd-dddd-dddddddddddd', '44444444-4444-4444-4444-444444444444', NOW(), NOW(), false),
(UUID(), 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '44444444-4444-4444-4444-444444444444', NOW(), NOW(), false);

INSERT INTO user_addresses (id, user_id, full_name, phone, street, ward, district, city, is_default, type, created_at, updated_at, is_deleted) VALUES
('ad000001-0001-0001-0001-000000000001', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Jane Consignor', '0901000003', '123 Fashion Way', 'Ward 10', 'District 1', 'HCM City', true, 'HOME', NOW(), NOW(), false),
('ad000002-0002-0002-0002-000000000002', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Bob Buyer', '0901000004', '456 Market St', 'Ward 5', 'District 3', 'HCM City', true, 'OFFICE', NOW(), NOW(), false);

-- -----------------------------------------------------
-- 2. Catalog - Settings, Brands, Categories
-- -----------------------------------------------------
INSERT INTO system_settings (id, setting_key, setting_value, description, created_at, updated_at, is_deleted) VALUES
(UUID(), 'COMMISSION_RATE', '0.10', 'Default commission for shop (10%)', NOW(), NOW(), false),
(UUID(), 'SHIPPING_FEE_FLAT', '30000', 'Flat shipping fee', NOW(), NOW(), false);

INSERT INTO brands (id, name, logo_url, description, is_active, created_at, updated_at, is_deleted) VALUES
('baaaaaaa-1111-1111-1111-111111111111', 'Gucci', 'https://fcs-assets.com/gucci.png', 'Iconic luxury brand', true, NOW(), NOW(), false),
('baaaaaaa-2222-2222-2222-222222222222', 'Louis Vuitton', 'https://fcs-assets.com/lv.png', 'Classic luxury brand', true, NOW(), NOW(), false),
('baaaaaaa-3333-3333-3333-333333333333', 'Dior', 'https://fcs-assets.com/dior.png', 'Elegant fashion brand', true, NOW(), NOW(), false);

INSERT INTO categories (id, parent_id, name, slug, is_active, created_at, updated_at, is_deleted) VALUES
('c1000000-0000-0000-0000-000000000000', NULL, 'Women', 'women', true, NOW(), NOW(), false),
('c1000001-0001-0000-0000-000000000000', 'c1000000-0000-0000-0000-000000000000', 'Handbags', 'women-handbags', true, NOW(), NOW(), false),
('c1000002-0002-0000-0000-000000000000', 'c1000000-0000-0000-0000-000000000000', 'Shoes', 'women-shoes', true, NOW(), NOW(), false),
('c2000000-0000-0000-0000-000000000000', NULL, 'Men', 'men', true, NOW(), NOW(), false);

-- -----------------------------------------------------
-- 3. Consignment - Requests, Items, Contracts
-- -----------------------------------------------------
INSERT INTO consignment_requests (id, consignor_id, code, status, note, created_at, updated_at, is_deleted) VALUES
('c0000001-0001-0001-0001-000000000001', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CR-2024-001', 'APPROVED', 'Sending 1 luxury bag', NOW(), NOW(), false),
('c0000002-0002-0002-0002-000000000002', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'CR-2024-002', 'PENDING', 'Check my Dior wallet', NOW(), NOW(), false);

INSERT INTO consignment_items (id, request_id, suggested_name, suggested_price, condition_note, status, created_at, updated_at, is_deleted) VALUES
('caaaaaaa-1111-1111-1111-000000000001', 'c0000001-0001-0001-0001-000000000001', 'Gucci Marmont Small Shoulder Bag', 25000000.0000, 'Like new, full box', 'READY_FOR_SALE', NOW(), NOW(), false);

INSERT INTO consignment_contracts (id, request_id, commission_rate, agreed_price, status, signed_at, valid_until, created_at, updated_at, is_deleted) VALUES
(UUID(), 'c0000001-0001-0001-0001-000000000001', 0.1000, 25000000.0000, 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 6 MONTH), NOW(), NOW(), false);

INSERT INTO consignment_status_history (id, entity_type, entity_id, from_status, to_status, changed_by, reason, created_at, updated_at) VALUES
(UUID(), 'CONSIGNMENT_REQUEST', 'c0000001-0001-0001-0001-000000000001', 'PENDING', 'APPROVED', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Item authentic and good condition', NOW(), NOW());

-- -----------------------------------------------------
-- 4. Product - Products, Media, Warehouse
-- -----------------------------------------------------
INSERT INTO products (id, consignment_item_id, brand_id, sku, name, description, condition_percent, original_price, sale_price, status, created_at, updated_at, is_deleted) VALUES
('00000001-0001-0001-0001-000000000001', 'caaaaaaa-1111-1111-1111-000000000001', 'baaaaaaa-1111-1111-1111-111111111111', 'SKU-GUC-MAR-001', 'Gucci Marmont Shoulder Bag', 'Leather shoulder bag with GG logo', 98.00, 45000000.0000, 25000000.0000, 'AVAILABLE', NOW(), NOW(), false);

INSERT INTO product_categories (id, product_id, category_id, is_primary, created_at, updated_at, is_deleted) VALUES
(UUID(), '00000001-0001-0001-0001-000000000001', 'c1000001-0001-0000-0000-000000000000', true, NOW(), NOW(), false);

INSERT INTO media_assets (id, owner_type, owner_id, media_type, url, display_order, is_primary, created_at, updated_at, is_deleted) VALUES
(UUID(), 'PRODUCT', '00000001-0001-0001-0001-000000000001', 'IMAGE', 'https://fcs-assets.com/products/gucci-1.jpg', 1, true, NOW(), NOW(), false);

INSERT INTO warehouse_logs (id, product_id, location, action_type, note, created_at, updated_at) VALUES
(UUID(), '00000001-0001-0001-0001-000000000001', 'Shelf-A1', 'INBOUND', 'Received from consignor', NOW(), NOW());

INSERT INTO product_status_history (id, product_id, from_status, to_status, changed_by, reason, created_at, updated_at) VALUES
(UUID(), '00000001-0001-0001-0001-000000000001', NULL, 'AVAILABLE', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Ready for sale', NOW(), NOW());

-- -----------------------------------------------------
-- 5. Orders - Vouchers, Orders, Carts, Wishlist
-- -----------------------------------------------------
INSERT INTO vouchers (id, code, discount_type, discount_value, min_order_value, max_discount, start_date, end_date, usage_limit, status, created_at, updated_at, is_deleted) VALUES
('baaaaaaa-ffff-1111-1111-111111111111', 'WELCOME10', 'PERCENTAGE', 10.0000, 1000000.0000, 500000.0000, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 100, 'ACTIVE', NOW(), NOW(), false);

INSERT INTO orders (id, buyer_id, order_code, sub_total, shipping_fee, discount_amount, total_amount, payment_method, shipping_address_id, status, created_at, updated_at, is_deleted) VALUES
('00000001-0001-0001-0001-000000000001', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'FCS-ORD-001', 25000000.0000, 30000.0000, 500000.0000, 24530000.0000, 'CREDIT_CARD', 'ad000002-0002-0002-0002-000000000002', 'DELIVERED', NOW(), NOW(), false);

INSERT INTO order_items (id, order_id, product_id, sku_snapshot, product_name_snapshot, price_at_purchase, created_at, updated_at, is_deleted) VALUES
(UUID(), '00000001-0001-0001-0001-000000000001', '00000001-0001-0001-0001-000000000001', 'SKU-GUC-MAR-001', 'Gucci Marmont Shoulder Bag', 25000000.0000, NOW(), NOW(), false);

INSERT INTO order_status_history (id, order_id, from_status, to_status, changed_by, reason, created_at, updated_at) VALUES
(UUID(), '00000001-0001-0001-0001-000000000001', 'PENDING', 'DELIVERED', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'System updated', NOW(), NOW());

INSERT INTO voucher_usages (id, voucher_id, user_id, order_id, created_at, updated_at, is_deleted) VALUES
(UUID(), 'baaaaaaa-ffff-1111-1111-111111111111', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '00000001-0001-0001-0001-000000000001', NOW(), NOW(), false);

INSERT INTO carts (id, user_id, session_id, created_at, updated_at, is_deleted) VALUES
('ca000000-0000-0000-0000-000000000001', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', NULL, NOW(), NOW(), false);

INSERT INTO wishlist_items (id, user_id, product_id, created_at, updated_at) VALUES
(UUID(), 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '00000001-0001-0001-0001-000000000001', NOW(), NOW());

-- -----------------------------------------------------
-- 6. Returns & Reviews
-- -----------------------------------------------------
INSERT INTO product_reviews (id, product_id, buyer_id, rating, comment, verified_purchase, created_at, updated_at, is_deleted) VALUES
(UUID(), '00000001-0001-0001-0001-000000000001', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 5, 'Perfect quality!', true, NOW(), NOW(), false);

-- -----------------------------------------------------
-- 7. Financial - Wallets, Transactions, Withdrawals
-- -----------------------------------------------------
INSERT INTO wallets (id, user_id, balance, available_balance, bank_name, bank_account_name, bank_account_number, created_at, updated_at, is_deleted) VALUES
('baaaaaaa-cccc-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 0.00, 0.00, NULL, NULL, NULL, NOW(), NOW(), false),
('baaaaaaa-cccc-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 22500000.00, 22500000.00, 'Vietcombank', 'JANE CONSIGNOR', '1234567890', NOW(), NOW(), false);

INSERT INTO wallet_transactions (id, wallet_id, order_id, amount, type, status, reference_type, description, created_at, updated_at) VALUES
(UUID(), 'baaaaaaa-cccc-2222-2222-222222222222', '00000001-0001-0001-0001-000000000001', 22500000.00, 'PAYOUT', 'COMPLETED', 'ORDER', 'Payout for Gucci Bag (Order FCS-ORD-001)', NOW(), NOW());

INSERT INTO withdrawal_requests (id, request_code, wallet_id, amount, status, reviewed_by, reviewed_at, created_at, updated_at, is_deleted) VALUES
('00000001-ffff-0001-0001-000000000001', 'WDR-001', 'baaaaaaa-cccc-2222-2222-222222222222', 10000000.00, 'APPROVED', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW(), NOW(), false);

INSERT INTO withdrawal_status_history (id, withdrawal_request_id, from_status, to_status, changed_by, reason, created_at, updated_at) VALUES
(UUID(), '00000001-ffff-0001-0001-000000000001', 'PENDING', 'APPROVED', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Approved after review', NOW(), NOW());

-- -----------------------------------------------------
-- 8. Communications - Notifications & Chat
-- -----------------------------------------------------
INSERT INTO notifications (id, title, content, type, created_by, created_at, updated_at, is_deleted) VALUES
('baaaaaaa-eeee-1111-1111-111111111111', 'Welcome to FCS', 'Thanks for joining our fashion consignment system!', 'SYSTEM', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW(), false);

INSERT INTO user_notifications (id, user_id, notification_id, is_read, created_at, updated_at, is_deleted) VALUES
(UUID(), 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'baaaaaaa-eeee-1111-1111-111111111111', false, NOW(), NOW(), false);

INSERT INTO conversations (id, participant1_id, participant2_id, last_message_at, last_message_preview, created_at, updated_at, is_deleted) VALUES
('ca000000-3333-3333-3333-000000000001', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW(), 'Hello, is your bag still available?', NOW(), NOW(), false);

INSERT INTO chat_messages (id, conversation_id, sender_id, content, created_at, updated_at) VALUES
(UUID(), 'ca000000-3333-3333-3333-000000000001', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Hello, is your bag still available?', NOW(), NOW());

-- -----------------------------------------------------
-- 9. Audit - Activity Logs
-- -----------------------------------------------------
INSERT INTO activity_logs (id, user_id, action, entity_name, entity_id, ip_address, created_at, updated_at) VALUES
(UUID(), 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'LOGIN', 'User', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '127.0.0.1', NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
