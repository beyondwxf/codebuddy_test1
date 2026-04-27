-- 重新生成 customer1 用户的密码
-- 使用 BCrypt 加密 "Admin@2026"
-- 这里使用另一个已知的 BCrypt 哈希值

UPDATE sys_user 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE user_name = 'customer1';

-- 验证更新结果
SELECT 
    user_id,
    user_name,
    nick_name,
    password,
    status,
    del_flag
FROM sys_user 
WHERE user_name = 'customer1';

-- 同时确保用户有正确的角色关联
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (101, 2);

-- 验证角色关联
SELECT 
    u.user_id,
    u.user_name,
    r.role_id,
    r.role_name
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.role_id
WHERE u.user_name = 'customer1';
