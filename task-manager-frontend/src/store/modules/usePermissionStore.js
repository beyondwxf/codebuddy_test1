import { defineStore } from 'pinia'
import { getRouters } from '@/api/auth'
import router from '@/router'

// 静态组件映射（Vite 可静态分析）
const COMPONENT_MAP = {
  // Dashboard
  'dashboard': () => import('@/views/dashboard/index.vue'),
  'dashboard/index': () => import('@/views/dashboard/index.vue'),
  // 系统管理
  'system/user/index': () => import('@/views/system/user/index.vue'),
  'system/role/index': () => import('@/views/system/role/index.vue'),
  'system/menu/index': () => import('@/views/system/menu/index.vue'),
  'system/dept/index': () => import('@/views/system/dept/index.vue'),
  'system/dict/index': () => import('@/views/system/dict/index.vue'),
  // 供应商管理
  'system/supplier/index': () => import('@/views/system/supplier/index.vue'),
  // 系统监控
  'monitor/operlog/index': () => import('@/views/monitor/operlog/index.vue'),
  'monitor/logininfor/index': () => import('@/views/monitor/logininfor/index.vue'),
}

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    menus: [],
    routesGenerated: false
  }),
  getters: {
    sidebarMenus(state) {
      return state.menus.filter(m => !m.meta?.hidden)
    }
  },
  actions: {
    async generateRoutes() {
      const res = await getRouters()
      const menuData = res.data
      this.menus = menuData

      menuData.forEach(menu => {
        const menuPath = menu.path.startsWith('/') ? menu.path : `/${menu.path}`

        if (menu.children && menu.children.length > 0) {
          menu.children.forEach(child => {
            // 子路由路径拼接：子路由 path 通常是相对路径（如 user），需拼接父路径
            const childPath = child.path || ''
            const fullPath = childPath.startsWith('/')
              ? childPath
              : `${menuPath}/${childPath}`.replace(/\/+/g, '/')

            router.addRoute('Layout', {
              path: fullPath,
              name: child.name,
              component: resolveComponent(child.component),
              meta: {
                title: child.meta?.title || child.name,
                icon: child.meta?.icon && child.meta.icon !== '#' ? child.meta.icon : '',
                hidden: child.meta?.hidden || false
              }
            })
          })
        } else {
          router.addRoute('Layout', {
            path: menuPath,
            name: menu.name,
            component: resolveComponent(menu.component),
            meta: {
              title: menu.meta?.title || menu.name,
              icon: menu.meta?.icon && menu.meta.icon !== '#' ? menu.meta.icon : '',
              hidden: menu.meta?.hidden || false
            }
          })
        }
      })

      // Dashboard 首页路由
      router.addRoute('Layout', {
        path: '/dashboard',
        name: 'Dashboard',
        component: COMPONENT_MAP['dashboard'],
        meta: { title: '首页', icon: 'dashboard', hidden: true }
      })

      this.routesGenerated = true
    },

    resetState() {
      this.menus = []
      this.routesGenerated = false
    }
  }
})

/**
 * 解析组件路径为异步组件
 */
function resolveComponent(componentPath) {
  if (!componentPath || componentPath === 'Layout' || componentPath === 'InnerLink') {
    return COMPONENT_MAP['dashboard']
  }
  return COMPONENT_MAP[componentPath] || COMPONENT_MAP['dashboard']
}
