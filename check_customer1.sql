-- 检查 customer1 用户是否存在
SELECT user_id, user_name, nick_name, status, del_flag 
FROM sys_user 
WHERE user_name = 'customer1';

-- 如果不存在，插入 customer1 用户
-- 密码是 Admin@2026 的 BCrypt 加密值
INSERT IGNORE INTO `sys_user` VALUES (
    101, 100, 'customer1', '张小明', '00', 
    'customer1@test.com', '13800001001', '0', '', 
    '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', 
    '0', '0', '', NULL, 1, 
    NOW(), NULL, NULL, '电商测试客户1'
);

-- 分配普通角色 (roleId=2)
INSERT IGNORE INTO `sys_user_role` VALUES (101, 2);

-- 验证插入结果
SELECT user_id, user_name, nick_name, status, del_flag 
FROM sys_user 
WHERE user_name = 'customer1';
