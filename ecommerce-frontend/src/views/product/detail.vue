<template>
  <div class="product-detail-page">
    <!-- 加载中 -->
    <div v-if="loading" v-loading="true" class="loading-area"></div>

    <!-- 商品不存在 -->
    <el-empty v-else-if="!product" description="商品不存在或已下架" />

    <!-- 商品详情 -->
    <template v-else>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>商品详情</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 商品信息区 -->
      <div class="product-info-area">
        <!-- 左侧图片 -->
        <div class="product-gallery">
          <img v-if="product.previewImage" :src="product.previewImage" :alt="product.productName" class="product-image" />
          <div v-else class="image-placeholder">
            <el-icon :size="64"><Picture /></el-icon>
            <span>暂无图片</span>
          </div>
        </div>

        <!-- 右侧信息 -->
        <div class="product-meta">
          <h1 class="product-name">{{ product.productName }}</h1>
          <p class="product-sku">SKU编码：{{ product.skuCode }}</p>
          <div class="product-price-box">
            <span class="price-label">价格</span>
            <span class="price-value">¥{{ formatPrice(product.salePrice) }}</span>
          </div>
          <div class="product-stock">
            <span v-if="product.totalStock > 0" class="stock-in">
              <el-icon><CircleCheck /></el-icon>
              有货（库存：{{ product.totalStock }}{{ product.unit || '件' }}）
            </span>
            <span v-else class="stock-out">
              <el-icon><CircleClose /></el-icon>
              缺货
            </span>
          </div>
          <p v-if="product.description" class="product-desc">{{ product.description }}</p>

          <!-- 数量选择和按钮 -->
          <div class="action-area">
            <div class="quantity-row">
              <span class="quantity-label">数量</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.totalStock || 1"
                :disabled="product.totalStock <= 0"
                size="large"
              />
            </div>
            <div class="button-row">
              <el-button
                type="primary"
                size="large"
                :disabled="product.totalStock <= 0"
                @click="handleAddCart"
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button
                type="danger"
                size="large"
                :disabled="product.totalStock <= 0"
                @click="handleBuyNow"
              >
                立即购买
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品详情区 -->
      <div v-if="product.mobileContent" class="product-content-area">
        <h2 class="content-title">商品详情</h2>
        <div class="product-content" v-html="product.mobileContent"></div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProduct } from '@/api/product'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const product = ref(null)
const quantity = ref(1)

const formatPrice = (price) => {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

const fetchProduct = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getProduct(id)
    product.value = res.data || null
    quantity.value = 1
  } catch (e) {
    product.value = null
  } finally {
    loading.value = false
  }
}

const handleAddCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await cartStore.addItem(product.value.productId, quantity.value)
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error('加入购物车失败')
  }
}

const handleBuyNow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await cartStore.addItem(product.value.productId, quantity.value)
    router.push('/cart')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchProduct()
})
</script>

<style lang="scss" scoped>
.product-detail-page {
  min-height: 400px;
}

.loading-area {
  min-height: 400px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.product-info-area {
  display: flex;
  gap: 40px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  @media (max-width: 768px) {
    flex-direction: column;
    gap: 20px;
  }
}

.product-gallery {
  flex-shrink: 0;
  width: 450px;
  height: 450px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;

  @media (max-width: 768px) {
    width: 100%;
    height: auto;
    aspect-ratio: 1;
  }

  .product-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
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
  gap: 12px;
  font-size: 16px;
}

.product-meta {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px;
  line-height: 1.3;
}

.product-sku {
  font-size: 13px;
  color: #999;
  margin: 0 0 20px;
}

.product-price-box {
  background: #fef0f0;
  padding: 15px 20px;
  border-radius: 6px;
  margin-bottom: 16px;
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price-label {
  font-size: 14px;
  color: #999;
}

.price-value {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
}

.product-stock {
  margin-bottom: 16px;
  font-size: 14px;

  .stock-in {
    color: #67c23a;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .stock-out {
    color: #f56c6c;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.product-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 10px;
}

.action-area {
  margin-top: 30px;
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.quantity-label {
  font-size: 14px;
  color: #666;
}

.button-row {
  display: flex;
  gap: 16px;
}

.product-content-area {
  margin-top: 20px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.content-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.product-content {
  line-height: 1.8;
  color: #333;
  font-size: 14px;

  :deep(img) {
    max-width: 100%;
    height: auto;
  }
}
</style>
