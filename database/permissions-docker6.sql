-- MYSQL_USER trong compose cố định là restman6. Mật khẩu lấy từ .env.
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'restman6'@'%';
GRANT SELECT ON restman6.* TO 'restman6'@'%';
