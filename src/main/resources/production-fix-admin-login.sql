-- Sửa đăng nhập admin trên production khi POST /api/v1/auth/login trả 500 hoặc Invalid credentials
-- Mật khẩu sau script: password123
--
-- Lỗi collation MySQL 1267: dùng UUID (@admin_id), không so sánh username = 'admin'

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = 'utf8mb4_unicode_ci';

SET @admin_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa';
SET @admin_role_id = '11111111-1111-1111-1111-111111111111';
SET @admin_email = 'admin@rewear.studio';
SET @pwd_hash = '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqvoLSuYDM6W61qqSNo62C';

-- 1. User admin
INSERT INTO users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at, is_deleted)
SELECT @admin_id, 'admin', @pwd_hash, @admin_email, 'System Administrator', NULL, 'ACTIVE', NOW(6), NOW(6), false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = @admin_id);

UPDATE users
SET password_hash = @pwd_hash,
    email = @admin_email,
    username = 'admin',
    status = 'ACTIVE',
    is_deleted = false,
    updated_at = NOW(6)
WHERE id = @admin_id;

-- 2. Auth identity LOCAL (login đọc password từ bảng này)
INSERT INTO auth_identities (id, user_id, provider, provider_email, provider_user_id, password_hash, email_verified, is_primary, created_at, updated_at)
SELECT 'ai000001-0001-0001-0001-000000000001',
       @admin_id,
       'LOCAL',
       @admin_email,
       @admin_email,
       @pwd_hash,
       true,
       true,
       NOW(6),
       NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM auth_identities ai
  WHERE ai.user_id = @admin_id AND BINARY ai.provider = 'LOCAL'
);

UPDATE auth_identities
SET password_hash = @pwd_hash,
    provider_email = @admin_email,
    provider_user_id = @admin_email,
    email_verified = true,
    is_primary = true,
    updated_at = NOW(6)
WHERE user_id = @admin_id AND BINARY provider = 'LOCAL';

-- 3. Role ADMIN
INSERT INTO user_roles (id, user_id, role_id, created_at, updated_at, is_deleted)
SELECT 'ur000001-0001-0001-0001-000000000001', @admin_id, @admin_role_id, NOW(6), NOW(6), false
WHERE NOT EXISTS (
  SELECT 1 FROM user_roles ur
  WHERE ur.user_id = @admin_id AND ur.role_id = @admin_role_id AND ur.is_deleted = false
);

-- 4. Wallet
INSERT INTO wallets (id, user_id, balance, available_balance, bank_name, bank_account_name, bank_account_number, created_at, updated_at, is_deleted)
SELECT 'wa000001-0001-0001-0001-000000000001', @admin_id, 0.00, 0.00, NULL, NULL, NULL, NOW(6), NOW(6), false
WHERE NOT EXISTS (
  SELECT 1 FROM wallets w WHERE w.user_id = @admin_id AND w.is_deleted = false
);

-- Kiểm tra:
-- SELECT u.id, u.username, u.status, ai.provider, ur.role_id
-- FROM users u
-- LEFT JOIN auth_identities ai ON ai.user_id = u.id AND BINARY ai.provider = 'LOCAL'
-- LEFT JOIN user_roles ur ON ur.user_id = u.id AND ur.is_deleted = false
-- WHERE u.id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa';
