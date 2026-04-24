import request from '@/api/request'

// 字典类型
export function listDictType(query) {
  return request({ url: '/api/system/dict/type/list', method: 'get', params: query })
}

export function getDictType(dictId) {
  return request({ url: `/api/system/dict/type/${dictId}`, method: 'get' })
}

export function addDictType(data) {
  return request({ url: '/api/system/dict/type', method: 'post', data })
}

export function updateDictType(data) {
  return request({ url: '/api/system/dict/type', method: 'put', data })
}

export function delDictType(dictIds) {
  return request({ url: `/api/system/dict/type/${dictIds}`, method: 'delete' })
}

// 字典数据
export function listDictData(query) {
  return request({ url: '/api/system/dict/data/list', method: 'get', params: query })
}

export function getDictData(dictCode) {
  return request({ url: `/api/system/dict/data/${dictCode}`, method: 'get' })
}

export function getDicts(dictType) {
  return request({ url: `/api/system/dict/data/type/${dictType}`, method: 'get' })
}

export function addDictData(data) {
  return request({ url: '/api/system/dict/data', method: 'post', data })
}

export function updateDictData(data) {
  return request({ url: '/api/system/dict/data', method: 'put', data })
}

export function delDictData(dictCodes) {
  return request({ url: `/api/system/dict/data/${dictCodes}`, method: 'delete' })
}
