<template>
  <div class="sidebar">
    <!-- Logo -->
    <div class="sidebar-logo" :class="{ collapse: !sidebar.opened }">
      <router-link to="/" class="logo-link">
        <span class="logo-icon">R</span>
        <span v-show="sidebar.opened" class="logo-title">RuoYi Admin</span>
      </router-link>
    </div>
    <!-- 菜单 -->
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="!sidebar.opened"
        :collapse-transition="false"
        :unique-opened="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        @select="handleMenuSelect"
      >
        <SidebarItem
          v-for="menu in menuList"
          :key="menu.path"
          :item="normalizeMenu(menu)"
          :base-path="normalizePath(menu.path)"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/useAppStore'
import { usePermissionStore } from '@/store/modules/usePermissionStore'
import SidebarItem from './SidebarItem.vue'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()
const permissionStore = usePermissionStore()
const sidebar = computed(() => appStore.sidebar)

// 记录上一次路由路径，避免重复导航
let lastNavigatedPath = ref('')

/**
 * el-menu 的 select 事件回调
 * 统一处理菜单项点击，使用 router.push 确保页面切换
 */
function handleMenuSelect(index) {
  if (index && index !== lastNavigatedPath.value) {
    lastNavigatedPath.value = index
    router.push(index)
  }
}

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 从 Pinia store 获取侧边栏菜单
const menuList = computed(() => permissionStore.sidebarMenus)

/**
 * 确保路径以 / 开头
 */
function normalizePath(path) {
  if (!path) return '/'
  return path.startsWith('/') ? path : `/${path}`
}

/**
 * 规范化菜单数据：确保一级菜单 path 是绝对路径
 * 后端可能返回 "system" 或 "/system"，统一转为 "/system"
 */
function normalizeMenu(menu) {
  return {
    ...menu,
    path: normalizePath(menu.path)
  }
}
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .el-scrollbar {
    flex: 1;
  }

  .sidebar-logo {
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #2b2f3a;
    overflow: hidden;

    &.collapse {
      justify-content: center;
    }

    .logo-link {
      display: flex;
      align-items: center;
      gap: 10px;
      text-decoration: none;
    }

    .logo-icon {
      width: 30px;
      height: 30px;
      background: #409EFF;
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-weight: bold;
      font-size: 18px;
      flex-shrink: 0;
    }

    .logo-title {
      color: #fff;
      font-size: 14px;
      font-weight: 600;
      white-space: nowrap;
    }
  }
}
</style>
