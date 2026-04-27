<template>
  <div class="order-list-page">
    <div class="list-header">
      <h2>我的订单</h2>
    </div>

    <div v-loading="loading" class="order-content">
      <!-- 订单卡片列表 -->
      <template v-if="!loading && orders.length > 0">
        <div v-for="order in orders" :key="order.orderId" class="order-card">
          <div class="order-card-top">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <el-tag :type="statusTagType(order.status)" size="small">
              {{ statusText(order.status) }}
            </el-tag>
          </div>
          <div class="order-card-mid">
            <span class="order-amount">¥{{ formatPrice(order.totalAmount) }}</span>
            <span class="order-time">{{ order.createTime }}</span>
          </div>
          <div class="order-card-bottom">
            <el-button type="primary" text @click="router.push(`/order/${order.orderId}`)">
              查看详情
            </el-button>
            <el-popconfirm
              v-if="order.status === 0"
              title="确定取消该订单吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleCancel(order.orderId)"
            >
              <template #reference>
                <el-button type="danger" text>取消订单</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-card">
        <el-empty description="暂无订单">
          <el-button type="primary" @click="router.push('/')">去逛逛</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-area">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        background
        @current-change="fetchOrders"
        @size-change="fetchOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listOrders, cancelOrder } from '@/api/order'

const router = useRouter()

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const statusMap = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '已付款', type: 'primary' },
  2: { text: '已发货', type: 'info' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'info' }
}

const statusText = (status) => statusMap[status]?.text || '未知'
const statusTagType = (status) => statusMap[status]?.type || 'info'

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await listOrders({ pageNum: pageNum.value, pageSize: pageSize.value })
    const data = res.data
    orders.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    orders.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleCancel = async (orderId) => {
  try {
    await cancelOrder(orderId)
    ElMessage.success('订单已取消')
    await fetchOrders()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '取消订单失败')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.order-list-page {
  min-height: 400px;
}

.list-header {
  margin-bottom: 20px;

  h2 {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin: 0;
  }
}

.order-content {
  min-height: 200px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 20px 24px;
  margin-bottom: 16px;
}

.order-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-card-mid {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 14px;
}

.order-amount {
  font-size: 22px;
  color: #f56c6c;
  font-weight: bold;
}

.order-time {
  font-size: 13px;
  color: #999;
}

.order-card-bottom {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  border-top: 1px solid #f0f0f0;
  padding-top: 14px;
}

.empty-card {
  background: #fff;
  border-radius: 8px;
  padding: 60px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.pagination-area {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}
</style>
