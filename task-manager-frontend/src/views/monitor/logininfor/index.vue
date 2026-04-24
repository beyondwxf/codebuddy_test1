<template>
  <div class="app-container">
    <el-card shadow="hover">
      <template #header>
        <span>登录日志</span>
      </template>
      <!-- 搜索栏 -->
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="登录账号">
          <el-input v-model="queryParams.userName" placeholder="请输入登录账号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="登录状态">
          <el-select v-model="queryParams.status" placeholder="登录状态" clearable style="width: 130px">
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
        <el-table-column label="日志编号" prop="infoId" width="100" align="center" />
        <el-table-column label="登录账号" prop="userName" width="120" align="center" />
        <el-table-column label="登录IP" prop="ipaddr" width="140" align="center" />
        <el-table-column label="登录地点" prop="loginLocation" min-width="150" />
        <el-table-column label="浏览器" prop="browser" width="120" align="center" />
        <el-table-column label="操作系统" prop="os" width="120" align="center" />
        <el-table-column label="登录状态" prop="status" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提示信息" prop="msg" min-width="150" />
        <el-table-column label="登录日期" prop="loginTime" width="170" align="center" />
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
  userName: '',
  status: undefined
})

const handleQuery = async () => {
  // TODO: 接入后端 API
  logList.value = []
  total.value = 0
}

const resetQuery = () => {
  queryParams.value = { pageNum: 1, pageSize: 10, userName: '', status: undefined }
  handleQuery()
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
