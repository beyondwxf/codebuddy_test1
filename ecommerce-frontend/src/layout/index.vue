<template>
  <div class="shop-layout">
    <header class="shop-header">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <el-icon :size="24"><Shop /></el-icon>
          <span>电商商城</span>
        </div>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
        </nav>
        <div class="header-right">
          <router-link to="/cart" class="cart-link">
            <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </el-badge>
          </router-link>
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <span class="user-info">
                <el-icon><User /></el-icon>
                {{ userStore.nickName || userStore.userName }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/order/list')">我的订单</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="auth-link">登录</router-link>
            <router-link to="/register" class="auth-link">注册</router-link>
          </template>
        </div>
      </div>
    </header>
    <main class="shop-main">
      <router-view />
    </main>
    <footer class="shop-footer">
      <p>&copy; 2026 电商商城 版权所有</p>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const handleLogout = async () => {
  await userStore.logoutAction()
  router.push('/')
}
</script>

<style lang="scss" scoped>
.shop-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.shop-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: bold;
    color: #409eff;
    cursor: pointer;
  }

  .nav-links {
    display: flex;
    gap: 24px;
    a {
      font-size: 15px;
      color: #333;
      &:hover { color: #409eff; }
      &.router-link-exact-active { color: #409eff; font-weight: 500; }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .cart-link {
    color: #333;
    cursor: pointer;
    &:hover { color: #409eff; }
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    color: #333;
    &:hover { color: #409eff; }
  }

  .auth-link {
    font-size: 14px;
    color: #409eff;
    &:hover { text-decoration: underline; }
  }
}

.shop-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 20px auto;
  padding: 0 20px;
}

.shop-footer {
  background: #333;
  color: #999;
  text-align: center;
  padding: 20px;
  font-size: 13px;
}
</style>
