import { useUserStore } from '@/store/modules/useUserStore'

/**
 * v-hasPermi 权限指令
 * 用法：v-hasPermi="['system:user:add']"
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    const permissions = window.__USER_PERMISSIONS__ || []

    if (value && value instanceof Array && value.length > 0) {
      const hasPermission = permissions.some(perm => {
        return value.includes('*:*:*') || value.includes(perm)
      })
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('请设置操作权限标签值，如 v-hasPermi="[\'system:user:add\']"')
    }
  }
}
