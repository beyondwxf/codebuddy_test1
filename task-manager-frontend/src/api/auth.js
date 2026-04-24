import request from './request'

/**
 * 获取验证码
 */
export function getCaptcha() {
  return request({
    url: '/api/auth/captcha',
    method: 'get'
  })
}

/**
 * 登录
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/**
 * 登出
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getInfo() {
  return request({
    url: '/api/auth/getInfo',
    method: 'get'
  })
}

/**
 * 获取路由信息
 */
export function getRouters() {
  return request({
    url: '/api/auth/getRouters',
    method: 'get'
  })
}
