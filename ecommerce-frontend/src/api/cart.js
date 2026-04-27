import request from './request'

// 获取购物车列表
export function listCart() {
  return request({
    url: '/api/shop/cart/list',
    method: 'get'
  })
}

// 添加到购物车
export function addToCart(data) {
  return request({
    url: '/api/shop/cart/add',
    method: 'post',
    data
  })
}

// 修改购物车数量
export function updateCart(data) {
  return request({
    url: '/api/shop/cart/update',
    method: 'put',
    data
  })
}

// 删除购物车项
export function deleteCart(cartIds) {
  return request({
    url: '/api/shop/cart/' + cartIds,
    method: 'delete'
  })
}
