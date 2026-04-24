import { useUserStore } from '@/store/modules/useUserStore'

/**
 * v-hasPermi 权限指令
 * 用法：v-hasPermi="['system:user:add']"
 * 从 Pinia store 实时读取权限数据，确保与后端同步
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    // 从 Pinia store 实时读取权限数据，解决 window.__USER_PERMISSIONS__ 过期问题
    const userStore = useUserStore()
    const permissions = userStore.permissions || []

    if (value && value instanceof Array && value.length > 0) {
      const hasPermission = value.some(perm =>
        perm === '*:*:*' || permissions.includes(perm)
      )
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('请设置操作权限标签值，如 v-hasPermi="[\'system:user:add\']"')
    }
  }
}
