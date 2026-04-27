<template>
  <div class="order-detail-page">
    <!-- 面包屑 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/order/list' }">我的订单</el-breadcrumb-item>
      <el-breadcrumb-item>订单详情</el-breadcrumb-item>
    </el-breadcrumb>

    <div v-loading="loading" class="detail-content">
      <template v-if="!loading && order">
        <!-- 订单信息卡片 -->
        <div class="card">
          <div class="card-title">订单信息</div>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">订单号</span>
              <span class="info-value">{{ order.orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">订单状态</span>
              <el-tag :type="statusTagType(order.status)" size="small">
                {{ statusText(order.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ order.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">收货人</span>
              <span class="info-value">{{ order.receiverName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">联系电话</span>
              <span class="info-value">{{ order.receiverPhone }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">收货地址</span>
              <span class="info-value">{{ order.receiverAddress }}</span>
            </div>
            <div v-if="order.remark" class="info-item full-width">
              <span class="info-label">备注</span>
              <span class="info-value">{{ order.remark }}</span>
            </div>
          </div>
        </div>

        <!-- 订单商品列表 -->
        <div class="card">
          <div class="card-title">订单商品</div>
          <el-table :data="order.orderItems || []" style="width: 100%">
            <el-table-column prop="productName" label="商品名称" min-width="200" />
            <el-table-column label="单价" width="120" align="center">
              <template #default="{ row }">
                <span>¥{{ formatPrice(row.salePrice) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" align="center" />
            <el-table-column label="小计" width="140" align="center">
              <template #default="{ row }">
                <span class="price">¥{{ formatPrice(row.subtotal) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <div class="total-row">
            <span class="total-label">订单合计：</span>
            <span class="total-amount">¥{{ formatPrice(order.totalAmount) }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-area">
          <el-popconfirm
            v-if="order.status === 0"
            title="确定取消该订单吗？"
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="handleCancel"
          >
            <template #reference>
              <el-button type="danger">取消订单</el-button>
            </template>
          </el-popconfirm>
          <el-button @click="router.push('/order/list')">返回订单列表</el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrder, cancelOrder } from '@/api/order'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const order = ref(null)

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

const fetchOrder = async () => {
  loading.value = true
  try {
    const orderId = route.params.id
    const res = await getOrder(orderId)
    order.value = res.data
  } catch (e) {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = async () => {
  try {
    await cancelOrder(order.value.orderId)
    ElMessage.success('订单已取消')
    await fetchOrder()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '取消订单失败')
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style lang="scss" scoped>
.order-detail-page {
  min-height: 400px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 24px;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px 32px;
}

.info-item {
  display: flex;
  align-items: baseline;
  gap: 8px;

  &.full-width {
    grid-column: 1 / -1;
  }
}

.info-label {
  font-size: 14px;
  color: #999;
  white-space: nowrap;
  flex-shrink: 0;
  min-width: 70px;
}

.info-value {
  font-size: 14px;
  color: #333;
  word-break: break-all;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.total-row {
  display: flex;
  justify-content: flex-end;
  align-items: baseline;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}

.total-label {
  font-size: 15px;
  color: #333;
}

.total-amount {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 8px;
}

.action-area {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 8px 0 24px;
}
</style>
