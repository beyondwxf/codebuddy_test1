import request from './request'

// 登录
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// 注册
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/api/auth/captcha',
    method: 'get'
  })
}

// 登出
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 获取用户信息
export function getInfo() {
  return request({
    url: '/api/auth/getInfo',
    method: 'get'
  })
}
