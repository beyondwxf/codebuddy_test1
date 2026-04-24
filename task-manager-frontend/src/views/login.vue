<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">R</div>
        <h2 class="login-title">RuoYi Admin</h2>
        <p class="login-subtitle">若依后台管理系统</p>
      </div>

      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
        <el-form-item prop="userName">
          <el-input
            v-model="loginForm.userName"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item prop="code" v-if="captchaEnabled">
          <div class="captcha-row">
            <el-input
              v-model="loginForm.code"
              placeholder="验证码"
              prefix-icon="Key"
              @keyup.enter="handleLogin"
            />
            <img
              :src="captchaUrl"
              class="captcha-img"
              alt="验证码"
              @click="refreshCaptcha"
            />
          </div>
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-tip">
        <span>默认账号: <strong>admin</strong> / <strong>admin</strong></span>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCaptcha, login } from '@/api/auth'
import { setToken } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const loginFormRef = ref(null)
const loading = ref(false)
const captchaEnabled = ref(false)
const captchaUrl = ref('')

const loginForm = ref({
  userName: 'admin',
  password: 'admin',
  rememberMe: true,
  uuid: '',
  code: ''
})

const loginRules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 1, message: '请输入密码', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    const data = res.data
    captchaEnabled.value = true
    loginForm.value.uuid = data.uuid
    // 使用 Base64 验证码图片
    captchaUrl.value = `data:image/png;base64,${data.img}`
  } catch {
    captchaEnabled.value = false
  }
}

// 登录
const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await login(loginForm.value)
      setToken(res.data.token)
      const redirect = route.query.redirect || '/'
      router.push(redirect)
      ElMessage.success('登录成功')
    } catch {
      // 登录失败不再刷新验证码
      // refreshCaptcha()
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  // 验证码已禁用，不再自动获取
  // refreshCaptcha()
})
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2d3a4b 0%, #1a2332 50%, #0d1b2a 100%);
  position: relative;
  overflow: hidden;
}

.login-card {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  z-index: 10;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  .logo-icon {
    width: 50px;
    height: 50px;
    background: linear-gradient(135deg, #409EFF, #66B1FF);
    border-radius: 12px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-weight: bold;
    font-size: 24px;
    margin-bottom: 12px;
  }

  .login-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }

  .login-subtitle {
    font-size: 13px;
    color: #909399;
    margin-top: 6px;
  }
}

.captcha-row {
  display: flex;
  width: 100%;
  gap: 10px;

  .el-input {
    flex: 1;
  }

  .captcha-img {
    height: 40px;
    border-radius: 4px;
    cursor: pointer;
    border: 1px solid #dcdfe6;
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 8px;
}

.login-tip {
  text-align: center;
  margin-top: 10px;
  font-size: 12px;
  color: #909399;

  strong {
    color: #409EFF;
  }
}

// 背景装饰
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.06;
  background: #409EFF;
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -80px;
  left: -80px;
  animation: float 6s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 10%;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-30px); }
}
</style>
