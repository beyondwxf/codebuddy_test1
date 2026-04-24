<template>
  <div v-if="!item.meta?.hidden">
    <!-- 有可见子菜单：渲染为子菜单组 -->
    <el-sub-menu v-if="hasVisibleChildren" :index="item.path">
      <template #title>
        <el-icon v-if="isValidIcon(item.meta?.icon)">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <SidebarItem
        v-for="child in visibleChildren"
        :key="child.path"
        :item="child"
        :base-path="item.path"
      />
    </el-sub-menu>

    <!-- 无子菜单：el-menu 触发 select 事件时自动导航 -->
    <el-menu-item v-else :index="fullPath">
      <el-icon v-if="isValidIcon(item.meta?.icon)">
        <component :is="item.meta.icon" />
      </el-icon>
      <template #title>{{ item.meta?.title }}</template>
    </el-menu-item>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Element Plus 图标名称白名单
const VALID_ICONS = new Set([
  'user', 'peoples', 'tree', 'read', 'log', 'setting', 'monitor',
  'dashboard', 'search', 'refresh', 'plus', 'edit', 'delete',
  'download', 'upload', 'document', 'lock', 'key', 'message',
  'phone', 'email', 'star', 'warning', 'info', 'success',
  'close', 'check', 'arrow-left', 'arrow-right', 'arrow-up', 'arrow-down',
  'menu', 'more', 'tools', 'house', 'calendar', 'clock',
  'bell', 'notification', 'chat-dot-round', 'picture', 'folder',
  'files', 'data-line', 'data-board', 'tickets', 'operation',
  'setting', 'component', 'grid', 'list', 'guide'
])

const props = defineProps({
  item: { type: Object, required: true },
  basePath: { type: String, default: '' }
})

// 判断 icon 是否为有效的 Element Plus 图标名
function isValidIcon(icon) {
  return icon && icon !== '#' && VALID_ICONS.has(icon)
}

// 可见子菜单
const visibleChildren = computed(() => {
  if (!props.item.children || props.item.children.length === 0) return []
  return props.item.children.filter(c => !c.meta?.hidden)
})

// 是否有可见子菜单
const hasVisibleChildren = computed(() => visibleChildren.value.length > 0)

// 完整路由路径（作为 el-menu-item 的 index）
const fullPath = computed(() => {
  const itemPath = props.item.path || ''
  if (itemPath.startsWith('/')) return itemPath
  const base = props.basePath || ''
  return base.endsWith('/') ? `${base}${itemPath}` : `${base}/${itemPath}`
})
</script>
