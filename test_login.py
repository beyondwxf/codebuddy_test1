import requests
import json

# 测试 customer1 登录
url = 'http://localhost:8080/api/auth/login'
data = {
    'userName': 'customer1',
    'password': 'Admin@2026'
}

print('测试登录...')
print(f'URL: {url}')
print(f'用户名: customer1')
print(f'密码: Admin@2026')
print('-' * 50)

try:
    response = requests.post(url, json=data)
    print(f'状态码: {response.status_code}')
    print(f'响应内容:')
    print(json.dumps(response.json(), indent=2, ensure_ascii=False))
except Exception as e:
    print(f'请求失败: {e}')
