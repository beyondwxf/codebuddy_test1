<template>
  <div class="checkout-page">
    <div class="checkout-header">
      <h2>确认订单</h2>
    </div>

    <!-- 选中商品为空 -->
    <div v-if="selectedItems.length === 0" class="empty-card">
      <el-empty description="没有选中的商品">
        <el-button type="primary" @click="router.push('/cart')">返回购物车</el-button>
      </el-empty>
    </div>

    <template v-else>
      <!-- 订单商品列表 -->
      <div class="card">
        <div class="card-title">订单商品</div>
        <el-table :data="selectedItems" style="width: 100%">
          <el-table-column prop="productName" label="商品名称" min-width="200" />
          <el-table-column label="单价" width="120" align="center">
            <template #default="{ row }">
              <span class="price">¥{{ formatPrice(row.salePrice) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" align="center" />
          <el-table-column label="小计" width="140" align="center">
            <template #default="{ row }">
              <span class="price">¥{{ formatPrice(row.salePrice * row.quantity) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="total-row">
          <span class="total-label">合计：</span>
          <span class="total-amount">¥{{ formatPrice(cartStore.totalAmount) }}</span>
        </div>
      </div>

      <!-- 收货信息表单 -->
      <div class="card">
        <div class="card-title">收货信息</div>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          size="large"
        >
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="联系电话" prop="receiverPhone">
            <el-input v-model="form.receiverPhone" placeholder="请输入11位手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="收货地址" prop="receiverAddress">
            <el-input
              v-model="form.receiverAddress"
              type="textarea"
              :rows="3"
              placeholder="请输入详细收货地址"
            />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="form.remark"
              type="textarea"
              :rows="2"
              placeholder="选填，如有特殊要求请备注"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 提交按钮 -->
      <div class="submit-area">
        <el-button
          type="danger"
          size="large"
          :loading="submitting"
          class="submit-btn"
          @click="handleSubmit"
        >
          提交订单
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/store/cart'
import { submitOrder } from '@/api/order'

const router = useRouter()
const cartStore = useCartStore()

const formRef = ref(null)
const submitting = ref(false)

const selectedItems = computed(() => cartStore.selectedItems)

const form = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: ''
})

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入联系电话'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的11位手机号'))
  } else {
    callback()
  }
}

const rules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  receiverAddress: [
    { required: true, message: '请输入收货地址', trigger: 'blur' }
  ]
}

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    await submitOrder({
      cartIds: selectedItems.value.map(item => item.cartId),
      receiverName: form.receiverName,
      receiverPhone: form.receiverPhone,
      receiverAddress: form.receiverAddress,
      remark: form.remark
    })
    ElMessage.success('订单提交成功')
    cartStore.selectedIds = []
    await cartStore.fetchCart()
    router.push('/order/list')
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '订单提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  if (cartStore.cartList.length === 0) {
    await cartStore.fetchCart()
  }
})
</script>

<style lang="scss" scoped>
.checkout-page {
  min-height: 400px;
}

.checkout-header {
  margin-bottom: 20px;

  h2 {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin: 0;
  }
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
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 8px;
}

.empty-card {
  background: #fff;
  border-radius: 8px;
  padding: 60px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.submit-area {
  text-align: center;
  padding: 24px 0;
}

.submit-btn {
  width: 320px;
  font-size: 16px;
}
</style>
