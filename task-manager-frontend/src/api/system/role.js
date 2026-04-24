import request from '@/api/request'

export function listRole(query) {
  return request({ url: '/api/system/role/list', method: 'get', params: query })
}

export function getRole(roleId) {
  return request({ url: `/api/system/role/${roleId}`, method: 'get' })
}

export function addRole(data) {
  return request({ url: '/api/system/role', method: 'post', data })
}

export function updateRole(data) {
  return request({ url: '/api/system/role', method: 'put', data })
}

export function delRole(roleIds) {
  return request({ url: `/api/system/role/${roleIds}`, method: 'delete' })
}
