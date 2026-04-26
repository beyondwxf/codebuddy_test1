import request from '@/api/request'

// 查询商品列表
export function listProduct(query) {
  return request({ url: '/api/wms/product/list', method: 'get', params: query })
}

// 查询商品详情（含供应商和库存信息）
export function getProduct(productId) {
  return request({ url: `/api/wms/product/${productId}`, method: 'get' })
}

// 新增商品
export function addProduct(data) {
  return request({ url: '/api/wms/product', method: 'post', data })
}

// 修改商品
export function updateProduct(data) {
  return request({ url: '/api/wms/product', method: 'put', data })
}

// 删除商品
export function delProduct(productIds) {
  return request({ url: `/api/wms/product/${productIds}`, method: 'delete' })
}

// 导出商品
export function exportProduct(params) {
  return request({ url: '/api/wms/product/export', method: 'post', params, responseType: 'blob' })
}

// 导入商品
export function importProduct(data) {
  return request({
    url: '/api/wms/product/import',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 下载导入模板
export function downloadProductTemplate() {
  return request({ url: '/api/wms/product/template', method: 'post', responseType: 'blob' })
}
