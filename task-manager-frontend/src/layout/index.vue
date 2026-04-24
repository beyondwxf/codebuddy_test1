<template>
  <div class="app-wrapper" :class="{ 'sidebar-collapsed': !sidebar.opened }">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :style="{ width: sidebar.opened ? '210px' : '64px' }">
      <Sidebar />
    </div>
    <!-- 主内容区 -->
    <div class="main-container" :style="{ marginLeft: sidebar.opened ? '210px' : '64px' }">
      <Navbar />
      <AppMain />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/store/modules/useAppStore'
import Sidebar from './Sidebar/index.vue'
import Navbar from './Navbar.vue'
import AppMain from './AppMain.vue'

const sidebar = computed(() => useAppStore().sidebar)
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.sidebar-container {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1001;
  background: #304156;
  transition: width 0.28s ease;
  overflow: hidden;
}

.main-container {
  min-height: 100%;
  transition: margin-left 0.28s ease;
  position: relative;
}
</style>
