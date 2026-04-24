<template>
  <div class="navbar" :style="{ width: sidebar.opened ? 'calc(100% - 210px)' : 'calc(100% - 64px)' }">
    <div class="hamburger-container" @click="toggleSidebar">
      <el-icon :size="20">
        <Fold v-if="sidebar.opened" />
        <Expand v-else />
      </el-icon>
    </div>

    <!-- 面包屑 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
        <span v-if="item.redirect === 'noRedirect' || item.children?.length > 0">{{ item.meta?.title }}</span>
        <router-link v-else :to="item.path">{{ item.meta?.title }}</router-link>
      </el-breadcrumb-item>
    </el-breadcrumb>

    <div class="right-menu">
      <el-dropdown trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="30" style="background-color: #409EFF">
            {{ nickName.charAt(0) }}
          </el-avatar>
          <span class="username">{{ nickName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/useAppStore'
import { useUserStore } from '@/store/modules/useUserStore'
import { usePermissionStore } from '@/store/modules/usePermissionStore'
import { ElMessage } from 'element-plus'

const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const sidebar = computed(() => appStore.sidebar)
const nickName = computed(() => userStore.user?.nickName || '管理员')

const toggleSidebar = () => appStore.toggleSidebar()

// 面包屑
const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta && item.meta.title)
})

// 退出登录
const handleLogout = async () => {
  try {
    await userStore.logoutAction()
    permissionStore.resetState()
    ElMessage.success('已退出登录')
  } catch (e) {
    ElMessage.error('退出失败')
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  position: fixed;
  top: 0;
  right: 0;
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  padding: 0 15px;
  z-index: 1000;
  transition: width 0.28s ease;
}

.hamburger-container {
  cursor: pointer;
  line-height: 50px;
  display: flex;
  align-items: center;

  &:hover {
    background: rgba(0, 0, 0, 0.025);
  }
}

.breadcrumb {
  margin-left: 15px;
}

.right-menu {
  margin-left: auto;
  display: flex;
  align-items: center;

  .avatar-wrapper {
    display: flex;
    align-items: center;
    cursor: pointer;
    gap: 8px;

    .username {
      font-size: 14px;
      color: #333;
    }
  }
}
</style>
