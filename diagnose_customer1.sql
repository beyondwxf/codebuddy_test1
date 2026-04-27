-- ========================================
-- 完整诊断 customer1 用户的登录问题
-- ========================================

-- 1. 查看 customer1 的完整用户信息
SELECT 
    user_id,
    user_name,
    nick_name,
    password,
    status,
    del_flag,
    dept_id,
    create_time
FROM sys_user 
WHERE user_name = 'customer1';

-- 2. 查看 customer1 的角色关联
SELECT 
    ur.user_id,
    ur.role_id,
    r.role_name,
    r.role_key,
    r.status as role_status,
    r.del_flag as role_del_flag
FROM sys_user_role ur
LEFT JOIN sys_role r ON ur.role_id = r.role_id
WHERE ur.user_id = 101;

-- 3. 查看所有正常用户的密码哈希（对比用）
SELECT 
    user_id,
    user_name,
    LEFT(password, 40) as password_hash_preview,
    status,
    del_flag
FROM sys_user 
WHERE del_flag = '0' 
  AND status = '0'
  AND user_name IN ('admin', 'customer1', 'zhangsan')
ORDER BY user_id;

-- 4. 如果 customer1 不存在，插入完整数据
INSERT IGNORE INTO `sys_user` VALUES (
    101, 100, 'customer1', '张小明', '00', 
    'customer1@test.com', '13800001001', '0', '', 
    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8OS9Z1yyUxDe', 
    '0', '0', '', NULL, 1, 
    NOW(), NULL, NULL, '电商测试客户1'
);

-- 5. 确保角色关联存在
INSERT IGNORE INTO `sys_user_role` (user_id, role_id) VALUES (101, 2);

-- 6. 强制更新密码为正确的 BCrypt 哈希（Admin@2026）
UPDATE sys_user 
SET password = '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8OS9Z1yyUxDe',
    status = '0',
    del_flag = '0'
WHERE user_name = 'customer1';

-- 7. 最终验证
SELECT 
    user_id,
    user_name,
    nick_name,
    password,
    status,
    del_flag,
    CASE 
        WHEN status = '0' AND del_flag = '0' THEN '✓ 正常'
        ELSE '✗ 异常'
    END as user_status
FROM sys_user 
WHERE user_name = 'customer1';

-- 8. 验证角色
SELECT 
    u.user_name,
    r.role_name,
    r.role_key
FROM sys_user u
INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
INNER JOIN sys_role r ON ur.role_id = r.role_id
WHERE u.user_name = 'customer1';
