import request from '@/api/request'

export function listMenu(query) {
  return request({ url: '/api/system/menu/list', method: 'get', params: query })
}

export function getMenu(menuId) {
  return request({ url: `/api/system/menu/${menuId}`, method: 'get' })
}

export function treeSelect() {
  return request({ url: '/api/system/menu/treeSelect', method: 'get' })
}

export function addMenu(data) {
  return request({ url: '/api/system/menu', method: 'post', data })
}

export function updateMenu(data) {
  return request({ url: '/api/system/menu', method: 'put', data })
}

export function delMenu(menuId) {
  return request({ url: `/api/system/menu/${menuId}`, method: 'delete' })
}
