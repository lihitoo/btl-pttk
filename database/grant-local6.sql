-- Chỉ dùng tài khoản mẫu này trên máy cá nhân. Đổi mật khẩu khi dùng thật.
-- Chạy bằng root SAU schema + seed. Ứng dụng chỉ cần quyền đọc.
CREATE USER IF NOT EXISTS 'restman6'@'localhost' IDENTIFIED BY 'RestmanDemo6!';
GRANT SELECT ON restman6.* TO 'restman6'@'localhost';
