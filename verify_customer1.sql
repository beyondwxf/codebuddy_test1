-- 验证 customer1 用户的详细信息
SELECT 
    user_id,
    user_name,
    nick_name,
    password,
    status,
    del_flag,
    dept_id
FROM sys_user 
WHERE user_name = 'customer1';

-- 验证用户的角色
SELECT 
    u.user_id,
    u.user_name,
    r.role_id,
    r.role_name,
    r.role_key,
    r.status as role_status
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.role_id
WHERE u.user_name = 'customer1';

-- 如果密码哈希有问题，重新设置密码为 Admin@2026
-- BCrypt 加密的 "Admin@2026"
UPDATE sys_user 
SET password = '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2'
WHERE user_name = 'customer1';

-- 确认用户状态正常
UPDATE sys_user 
SET status = '0', del_flag = '0'
WHERE user_name = 'customer1';

-- 最终验证
SELECT 
    user_id,
    user_name,
    nick_name,
    LEFT(password, 30) as password_preview,
    status,
    del_flag
FROM sys_user 
WHERE user_name = 'customer1';
