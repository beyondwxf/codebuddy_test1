<template>
  <div class="app-container">
    <el-card shadow="hover">
      <template #header>
        <span>操作日志</span>
      </template>
      <!-- 搜索栏 -->
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="系统模块">
          <el-input v-model="queryParams.title" placeholder="请输入系统模块" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input v-model="queryParams.operName" placeholder="请输入操作人员" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.businessType" placeholder="操作类型" clearable style="width: 130px">
            <el-option label="其它" :value="0" />
            <el-option label="新增" :value="1" />
            <el-option label="修改" :value="2" />
            <el-option label="删除" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="操作状态" clearable style="width: 130px">
            <el-option label="成功" :value="0" />
            <el-option label="失败" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="logList" border stripe style="width: 100%">
        <el-table-column label="日志编号" prop="operId" width="100" align="center" />
        <el-table-column label="系统模块" prop="title" min-width="120" />
        <el-table-column label="操作类型" prop="businessType" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="businessTypeTag(row.businessType)">{{ businessTypeLabel(row.businessType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作人员" prop="operName" width="110" align="center" />
        <el-table-column label="操作地址" prop="operIp" width="130" align="center" />
        <el-table-column label="操作状态" prop="status" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作日期" prop="operTime" width="170" align="center" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const logList = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  title: '',
  operName: '',
  businessType: undefined,
  status: undefined
})

// 加载数据
const handleQuery = async () => {
  // TODO: 接入后端 API
  logList.value = []
  total.value = 0
}

const resetQuery = () => {
  queryParams.value = { pageNum: 1, pageSize: 10, title: '', operName: '', businessType: undefined, status: undefined }
  handleQuery()
}

const businessTypeLabel = (type) => {
  const map = { 0: '其它', 1: '新增', 2: '修改', 3: '删除' }
  return map[type] || '其它'
}

const businessTypeTag = (type) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return map[type] || 'info'
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.app-container {
  padding: 0;
}
.search-form {
  margin-bottom: 10px;
}
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
