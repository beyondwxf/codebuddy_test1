import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/dev-api',
  timeout: 30000
})

// 请求拦截器：注入 Token
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一错误处理
service.interceptors.response.use(
  response => {
    // blob 类型响应（导出文件等场景）
    if (response.config.responseType === 'blob') {
      // 后端返回错误时 Content-Type 为 JSON，需解析错误信息
      const contentType = response.headers['content-type'] || ''
      if (contentType.includes('application/json')) {
        return response.data.text().then(text => {
          const errorData = JSON.parse(text)
          ElMessage.error(errorData.message || '导出失败')
          return Promise.reject(new Error(errorData.message || '导出失败'))
        })
      }
      return response.data
    }
    const res = response.data
    // code !== 200 视为业务错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      // 401 Token 过期
      if (res.code === 401) {
        removeToken()
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const msg = error.response?.data?.message || error.message
    ElMessage.error(msg)
    if (error.response?.status === 401) {
      removeToken()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default service
