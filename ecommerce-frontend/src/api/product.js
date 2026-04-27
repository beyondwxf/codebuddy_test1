import request from './request'

// 公开-商品列表
export function listProducts(params) {
  return request({
    url: '/api/shop/products',
    method: 'get',
    params
  })
}

// 公开-商品详情
export function getProduct(productId) {
  return request({
    url: '/api/shop/products/' + productId,
    method: 'get'
  })
}
