import request from '@/api/request'

// 查询用户列表
export function listUser(query) {
  return request({ url: '/api/system/user/list', method: 'get', params: query })
}

// 查询用户详情
export function getUser(userId) {
  return request({ url: `/api/system/user/${userId}`, method: 'get' })
}

// 新增用户
export function addUser(data) {
  return request({ url: '/api/system/user', method: 'post', data })
}

// 修改用户
export function updateUser(data) {
  return request({ url: '/api/system/user', method: 'put', data })
}

// 删除用户
export function delUser(userIds) {
  return request({ url: `/api/system/user/${userIds}`, method: 'delete' })
}

// 重置密码
export function resetUserPwd(data) {
  return request({ url: '/api/system/user/resetPwd', method: 'put', data })
}

// 修改用户状态
export function changeUserStatus(data) {
  return request({ url: '/api/system/user/changeStatus', method: 'put', data })
}
