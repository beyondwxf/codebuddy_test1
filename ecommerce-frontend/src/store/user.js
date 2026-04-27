import { defineStore } from 'pinia'
import { login, logout, getInfo, register } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userId: '',
    userName: '',
    nickName: '',
    roles: [],
    permissions: []
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async loginAction(loginForm) {
      const res = await login(loginForm)
      const { token, userId, userName, nickName } = res.data
      this.token = token
      this.userId = userId
      this.userName = userName
      this.nickName = nickName
      setToken(token)
      return res
    },
    async registerAction(registerForm) {
      const res = await register(registerForm)
      return res
    },
    async getInfoAction() {
      const res = await getInfo()
      const { user, roles, permissions } = res.data
      this.userId = user.userId
      this.userName = user.userName
      this.nickName = user.nickName
      this.roles = roles
      this.permissions = permissions
      return res
    },
    async logoutAction() {
      try {
        await logout()
      } finally {
        this.resetState()
      }
    },
    resetState() {
      this.token = ''
      this.userId = ''
      this.userName = ''
      this.nickName = ''
      this.roles = []
      this.permissions = []
      removeToken()
    }
  }
})
