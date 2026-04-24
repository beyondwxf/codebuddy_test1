import request from '@/api/request'

export function listDept(query) {
  return request({ url: '/api/system/dept/list', method: 'get', params: query })
}

export function getDept(deptId) {
  return request({ url: `/api/system/dept/${deptId}`, method: 'get' })
}

export function addDept(data) {
  return request({ url: '/api/system/dept', method: 'post', data })
}

export function updateDept(data) {
  return request({ url: '/api/system/dept', method: 'put', data })
}

export function delDept(deptId) {
  return request({ url: `/api/system/dept/${deptId}`, method: 'delete' })
}
