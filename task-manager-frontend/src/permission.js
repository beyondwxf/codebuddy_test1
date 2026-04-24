import { getToken, removeToken } from '@/utils/auth'
import router from './router'
import { useUserStore } from '@/store/modules/useUserStore'
import { usePermissionStore } from '@/store/modules/usePermissionStore'
import { ElMessage } from 'element-plus'

// 白名单路由（无需登录）
const whiteList = ['/login']

router.beforeEach(async (to, from, next) => {
  window.NProgress?.start()
  document.title = to.meta.title ? `${to.meta.title} - RuoYi Admin` : 'RuoYi Admin'

  const token = getToken()
  if (token) {
    if (to.path === '/login') {
      // 已登录访问登录页，重定向到首页
      next({ path: '/' })
    } else {
      const userStore = useUserStore()
      const permissionStore = usePermissionStore()
      // 检查是否已生成路由
      if (permissionStore.routesGenerated) {
        next()
      } else {
        try {
          // 获取用户信息
          await userStore.getInfoAction()
          // 生成动态路由
          await permissionStore.generateRoutes()
          next({ ...to, replace: true })
        } catch (error) {
          removeToken()
          userStore.$reset()
          permissionStore.resetState()
          ElMessage.error('登录已过期，请重新登录')
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
  window.NProgress?.done()
})
