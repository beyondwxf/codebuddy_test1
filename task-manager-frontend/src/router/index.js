import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

// 公共路由（无需动态加载）
const routes = [
  {
    path: '/login',
    component: () => import('@/views/login.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: '/dashboard',
    meta: { title: '首页' },
    children: []
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
