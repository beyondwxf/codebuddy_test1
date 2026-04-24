import request from '@/api/request'

export function listLogininfor(query) {
  return request({ url: '/api/monitor/logininfor/list', method: 'get', params: query })
}

export function delLogininfor(infoIds) {
  return request({ url: `/api/monitor/logininfor/${infoIds}`, method: 'delete' })
}

export function cleanLogininfor() {
  return request({ url: '/api/monitor/logininfor/clean', method: 'delete' })
}

export function unlockLogininfor(userName) {
  return request({ url: `/api/monitor/logininfor/unlock/${userName}`, method: 'get' })
}
