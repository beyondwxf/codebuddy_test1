<template>
  <div class="home-page">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-input
        v-model="searchForm.productName"
        placeholder="搜索商品"
        :prefix-icon="Search"
        clearable
        style="width: 240px"
        @keyup.enter="handleSearch"
      />
      <el-input-number
        v-model="searchForm.minPrice"
        :min="0"
        :precision="2"
        :controls="false"
        placeholder="最低价"
        style="width: 120px"
      />
      <span class="price-separator">—</span>
      <el-input-number
        v-model="searchForm.maxPrice"
        :min="0"
        :precision="2"
        :controls="false"
        placeholder="最高价"
        style="width: 120px"
      />
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 商品网格 -->
    <div v-loading="loading" class="product-grid-wrapper">
      <div v-if="!loading && products.length > 0" class="product-grid">
        <ProductCard
          v-for="product in products"
          :key="product.productId"
          :product="product"
        />
      </div>
      <el-empty v-else-if="!loading" description="暂无商品" />
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-area">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        background
        @current-change="fetchProducts"
        @size-change="fetchProducts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { listProducts } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'

const loading = ref(false)
const products = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)

const searchForm = reactive({
  productName: '',
  minPrice: undefined,
  maxPrice: undefined
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchForm.productName) params.productName = searchForm.productName
    if (searchForm.minPrice != null) params.minPrice = searchForm.minPrice
    if (searchForm.maxPrice != null) params.maxPrice = searchForm.maxPrice

    const res = await listProducts(params)
    const data = res.data
    products.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchProducts()
}

const handleReset = () => {
  searchForm.productName = ''
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  pageNum.value = 1
  fetchProducts()
}

onMounted(() => {
  fetchProducts()
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 400px;
}

.search-area {
  background: #fff;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.price-separator {
  color: #999;
  font-size: 14px;
}

.product-grid-wrapper {
  min-height: 200px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (max-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.pagination-area {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}
</style>
