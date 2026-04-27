-- ========================================
-- 修复 customer1 用户密码
-- 使用 admin 用户的密码（密码是 admin）
-- ========================================

-- 方法1：使用 admin 的密码哈希（密码明文是 admin）
UPDATE sys_user 
SET password = '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2'
WHERE user_name = 'customer1';

-- 验证
SELECT 
    user_name, 
    password,
    status,
    del_flag
FROM sys_user 
WHERE user_name IN ('admin', 'customer1');

-- 说明：
-- admin 用户的密码哈希: $2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2
-- 对应的密码明文是: admin
-- 
-- 所以 customer1 的登录凭据应该是：
-- 用户名: customer1
-- 密码: admin
