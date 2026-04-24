import request from '@/api/request'

// 查询供应商列表
export function listSupplier(query) {
  return request({ url: '/api/system/supplier/list', method: 'get', params: query })
}

// 查询供应商详情
export function getSupplier(supplierId) {
  return request({ url: `/api/system/supplier/${supplierId}`, method: 'get' })
}

// 新增供应商
export function addSupplier(data) {
  return request({ url: '/api/system/supplier', method: 'post', data })
}

// 修改供应商
export function updateSupplier(data) {
  return request({ url: '/api/system/supplier', method: 'put', data })
}

// 删除供应商
export function delSupplier(supplierIds) {
  return request({ url: `/api/system/supplier/${supplierIds}`, method: 'delete' })
}

// 导出供应商
export function exportSupplier(params) {
  return request({ url: '/api/system/supplier/export', method: 'post', params, responseType: 'blob' })
}

// 导入供应商
export function importSupplier(data) {
  return request({
    url: '/api/system/supplier/import',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 下载导入模板
export function downloadTemplate() {
  return request({ url: '/api/system/supplier/template', method: 'post', responseType: 'blob' })
}
