-- 查看所有可以正常登录的用户及其密码哈希
SELECT 
    user_id,
    user_name,
    nick_name,
    LEFT(password, 40) as password_hash,
    status,
    del_flag
FROM sys_user 
WHERE del_flag = '0' 
  AND status = '0'
  AND user_name IN ('admin', 'customer1', 'zhangsan', 'lisi')
ORDER BY user_id;

-- 检查 admin 用户的完整信息（如果存在）
SELECT 
    user_id,
    user_name,
    nick_name,
    password,
    dept_id,
    status,
    del_flag
FROM sys_user 
WHERE user_name = 'admin';
