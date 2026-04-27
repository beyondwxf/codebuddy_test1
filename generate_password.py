import bcrypt

# 生成 Admin@2026 的 BCrypt 哈希
password = "Admin@2026".encode('utf-8')
hashed = bcrypt.hashpw(password, bcrypt.gensalt(rounds=10))

print("原始密码: Admin@2026")
print("BCrypt 哈希:", hashed.decode('utf-8'))
print()
print("SQL 更新语句:")
print(f"UPDATE sys_user SET password = '{hashed.decode('utf-8')}' WHERE user_name = 'customer1';")
