import request from '@/api/request'

export function listOperLog(query) {
  return request({ url: '/api/monitor/operlog/list', method: 'get', params: query })
}

export function getOperLog(operId) {
  return request({ url: `/api/monitor/operlog/${operId}`, method: 'get' })
}

export function delOperLog(operIds) {
  return request({ url: `/api/monitor/operlog/${operIds}`, method: 'delete' })
}

export function cleanOperLog() {
  return request({ url: '/api/monitor/operlog/clean', method: 'delete' })
}
