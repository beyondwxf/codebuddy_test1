<template>
  <el-card class="product-card" :body-style="{ padding: '0' }" shadow="hover" @click="handleClick">
    <div class="product-image">
      <img v-if="product.previewImage" :src="product.previewImage" :alt="product.productName" />
      <div v-else class="image-placeholder">
        <el-icon :size="48"><Picture /></el-icon>
        <span>暂无图片</span>
      </div>
    </div>
    <div class="product-info">
      <h3 class="product-name" :title="product.productName">{{ product.productName }}</h3>
      <p class="product-desc" :title="product.description">{{ product.description }}</p>
      <div class="product-bottom">
        <span class="product-price">¥{{ formatPrice(product.salePrice) }}</span>
        <el-button size="small" type="primary" @click.stop="handleAddCart">
          <el-icon><ShoppingCart /></el-icon>
          加入购物车
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const handleClick = () => {
  router.push(`/product/${props.product.productId}`)
}

const handleAddCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await cartStore.addItem(props.product.productId)
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error('加入购物车失败')
  }
}
</script>

<style lang="scss" scoped>
.product-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  overflow: hidden;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  }
}

.product-image {
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: #f5f5f5;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
  }

  &:hover img {
    transform: scale(1.05);
  }
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  gap: 8px;
  font-size: 14px;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin: 0 0 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-desc {
  font-size: 13px;
  color: #999;
  margin: 0 0 12px;
  line-height: 1.4;
  height: 36.4px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style>
