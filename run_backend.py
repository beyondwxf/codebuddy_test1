import subprocess
import os
import time
import signal
import sys

# 切换到后端目录
backend_dir = r"c:\dev\code\ai_code\codebuddy_test1\task-manager-backend"
os.chdir(backend_dir)

# 设置环境变量
env = os.environ.copy()
env['JAVA_TOOL_OPTIONS'] = '-Djansi.strip=true'

# 编译
print("编译中...")
result = subprocess.run(['mvnw.cmd', 'clean', 'package', '-DskipTests'],
                        env=env, capture_output=True, text=True)
if result.returncode != 0:
    print("编译失败:")
    print(result.stderr)
    sys.exit(1)
print("编译成功!")

# 启动
jar_path = os.path.join(backend_dir, 'target', 'task-manager-backend-1.0.0.jar')
print(f"启动后端: {jar_path}")
subprocess.Popen(['java', '-jar', jar_path], env=env)
print("后端已启动!")
