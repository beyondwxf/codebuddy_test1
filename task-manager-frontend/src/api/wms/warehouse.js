import request from '@/api/request'

// 查询仓库列表
export function listWarehouse(query) {
  return request({ url: '/api/wms/warehouse/list', method: 'get', params: query })
}

// 查询所有仓库（下拉选择用）
export function listAllWarehouse() {
  return request({ url: '/api/wms/warehouse/listAll', method: 'get' })
}

// 查询仓库详情
export function getWarehouse(warehouseId) {
  return request({ url: `/api/wms/warehouse/${warehouseId}`, method: 'get' })
}

// 新增仓库
export function addWarehouse(data) {
  return request({ url: '/api/wms/warehouse', method: 'post', data })
}

// 修改仓库
export function updateWarehouse(data) {
  return request({ url: '/api/wms/warehouse', method: 'put', data })
}

// 删除仓库
export function delWarehouse(warehouseIds) {
  return request({ url: `/api/wms/warehouse/${warehouseIds}`, method: 'delete' })
}

// 导出仓库
export function exportWarehouse(params) {
  return request({ url: '/api/wms/warehouse/export', method: 'post', params, responseType: 'blob' })
}

// 导入仓库
export function importWarehouse(data) {
  return request({
    url: '/api/wms/warehouse/import',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 下载导入模板
export function downloadWarehouseTemplate() {
  return request({ url: '/api/wms/warehouse/template', method: 'post', responseType: 'blob' })
}
