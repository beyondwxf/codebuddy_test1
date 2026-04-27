<template>
  <div class="cart-page">
    <!-- 页面标题 -->
    <div class="cart-header">
      <h2>我的购物车</h2>
      <span class="cart-count">共 {{ cartStore.cartCount }} 件商品</span>
    </div>

    <!-- 购物车列表 -->
    <div v-loading="loading" class="cart-content">
      <template v-if="!loading && cartStore.cartList.length > 0">
        <div class="cart-card">
          <!-- 表头 -->
          <div class="cart-table-header">
            <div class="col-check">
              <el-checkbox
                :model-value="isAllSelected"
                @change="cartStore.toggleSelectAll()"
              />
            </div>
            <div class="col-product">商品信息</div>
            <div class="col-price">单价</div>
            <div class="col-quantity">数量</div>
            <div class="col-subtotal">小计</div>
            <div class="col-action">操作</div>
          </div>

          <!-- 商品行 -->
          <div
            v-for="item in cartStore.cartList"
            :key="item.cartId"
            class="cart-item"
          >
            <div class="col-check">
              <el-checkbox
                :model-value="cartStore.selectedIds.includes(item.cartId)"
                @change="cartStore.toggleSelect(item.cartId)"
              />
            </div>
            <div class="col-product">
              <div class="product-info">
                <div class="product-image" @click="router.push(`/product/${item.productId}`)">
                  <img v-if="item.previewImage" :src="item.previewImage" :alt="item.productName" />
                  <div v-else class="image-placeholder">
                    <el-icon :size="28"><Picture /></el-icon>
                  </div>
                </div>
                <router-link :to="`/product/${item.productId}`" class="product-name">
                  {{ item.productName }}
                </router-link>
              </div>
            </div>
            <div class="col-price">
              <span class="price-text">¥{{ formatPrice(item.salePrice) }}</span>
            </div>
            <div class="col-quantity">
              <el-input-number
                :model-value="item.quantity"
                :min="1"
                size="small"
                @change="(val) => handleQuantityChange(item.cartId, val)"
              />
            </div>
            <div class="col-subtotal">
              <span class="subtotal-text">¥{{ formatPrice(item.salePrice * item.quantity) }}</span>
            </div>
            <div class="col-action">
              <el-popconfirm
                title="确定删除该商品吗？"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleRemove(item.cartId)"
              >
                <template #reference>
                  <el-button type="danger" text>删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>

        <!-- 底部结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox
              :model-value="isAllSelected"
              @change="cartStore.toggleSelectAll()"
            >
              全选
            </el-checkbox>
            <el-button
              type="danger"
              text
              :disabled="cartStore.selectedIds.length === 0"
              @click="handleBatchRemove"
            >
              删除选中
            </el-button>
          </div>
          <div class="footer-right">
            <span class="selected-info">已选 <em>{{ cartStore.selectedIds.length }}</em> 件</span>
            <span class="total-label">合计：</span>
            <span class="total-price">¥{{ formatPrice(cartStore.totalAmount) }}</span>
            <el-button type="danger" size="large" @click="handleCheckout">
              去结算
            </el-button>
          </div>
        </div>
      </template>

      <!-- 空购物车 -->
      <div v-else-if="!loading" class="cart-empty">
        <el-empty description="购物车空空如也">
          <el-button type="primary" @click="router.push('/')">去逛逛</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(false)

const isAllSelected = computed(() => {
  return cartStore.cartList.length > 0 && cartStore.selectedIds.length === cartStore.cartList.length
})

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const handleQuantityChange = async (cartId, quantity) => {
  if (!quantity || quantity < 1) return
  try {
    await cartStore.updateItem(cartId, quantity)
  } catch (e) {
    ElMessage.error('更新数量失败')
  }
}

const handleRemove = async (cartId) => {
  try {
    await cartStore.removeItems(cartId)
    ElMessage.success('已删除')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const handleBatchRemove = async () => {
  if (cartStore.selectedIds.length === 0) return
  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${cartStore.selectedIds.length} 件商品吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await cartStore.removeItems(cartStore.selectedIds.join(','))
    ElMessage.success('已删除选中商品')
  } catch (e) {
    // 用户取消
  }
}

const handleCheckout = () => {
  if (cartStore.selectedIds.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push('/order/checkout')
}

onMounted(async () => {
  loading.value = true
  try {
    await cartStore.fetchCart()
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.cart-page {
  min-height: 400px;
}

.cart-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;

  h2 {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin: 0;
  }

  .cart-count {
    font-size: 14px;
    color: #999;
  }
}

.cart-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.cart-table-header {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.col-check {
  width: 50px;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
}

.col-product {
  flex: 1;
  min-width: 0;
}

.col-price {
  width: 100px;
  flex-shrink: 0;
  text-align: center;
}

.col-quantity {
  width: 140px;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
}

.col-subtotal {
  width: 110px;
  flex-shrink: 0;
  text-align: center;
}

.col-action {
  width: 80px;
  flex-shrink: 0;
  text-align: center;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f5f5;
  flex-shrink: 0;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #c0c4cc;
  }
}

.product-name {
  font-size: 14px;
  color: #333;
  text-decoration: none;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;

  &:hover {
    color: #409eff;
  }
}

.price-text {
  font-size: 14px;
  color: #333;
}

.subtotal-text {
  font-size: 15px;
  color: #f56c6c;
  font-weight: 600;
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-top: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  .footer-left {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .footer-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .selected-info {
    font-size: 14px;
    color: #666;

    em {
      font-style: normal;
      color: #f56c6c;
      font-weight: 600;
    }
  }

  .total-label {
    font-size: 14px;
    color: #333;
  }

  .total-price {
    font-size: 24px;
    color: #f56c6c;
    font-weight: bold;
    margin-right: 8px;
  }
}

.cart-empty {
  background: #fff;
  border-radius: 8px;
  padding: 60px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}
</style>
