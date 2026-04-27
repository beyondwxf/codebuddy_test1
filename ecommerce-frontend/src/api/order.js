import request from './request'

// 提交订单
export function submitOrder(data) {
  return request({
    url: '/api/shop/order/submit',
    method: 'post',
    data
  })
}

// 订单列表
export function listOrders(params) {
  return request({
    url: '/api/shop/order/list',
    method: 'get',
    params
  })
}

// 订单详情
export function getOrder(orderId) {
  return request({
    url: '/api/shop/order/' + orderId,
    method: 'get'
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: '/api/shop/order/cancel/' + orderId,
    method: 'put'
  })
}
