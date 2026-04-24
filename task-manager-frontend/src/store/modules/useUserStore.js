import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    user: {},
    roles: [],
    permissions: []
  }),
  actions: {
    /**
     * 登录
     */
    async loginAction(loginForm) {
      const res = await login(loginForm)
      setToken(res.data.token)
      this.token = res.data.token
    },

    /**
     * 获取用户信息
     */
    async getInfoAction() {
      const res = await getInfo()
      const { user, roles, permissions } = res.data
      this.user = user
      this.roles = roles
      this.permissions = permissions
      return res.data
    },

    /**
     * 登出
     */
    async logoutAction() {
      try {
        await logout()
      } finally {
        removeToken()
        this.token = ''
        this.user = {}
        this.roles = []
        this.permissions = []
        router.push('/login')
      }
    }
  }
})
