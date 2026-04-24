<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-bar">
      <el-form-item label="用户名称" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 130px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb-16">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户ID" prop="userId" width="80" align="center" />
      <el-table-column label="用户名" prop="userName" min-width="100" show-overflow-tooltip />
      <el-table-column label="昵称" prop="nickName" min-width="100" show-overflow-tooltip />
      <el-table-column label="部门" prop="deptId" min-width="100" show-overflow-tooltip />
      <el-table-column label="手机号" prop="phone" min-width="120" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="{ row }">
          <el-switch v-model="row.status" active-value="0" inactive-value="1" @change="handleStatusChange(row)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" min-width="160" align="center" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(row)">删除</el-button>
          <el-button link type="primary" icon="Key" @click="handleResetPwd(row)">重置</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </div>

    <!-- 新增/修改弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名" :disabled="form.userId != null" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.userId == null">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.sex">
                <el-radio value="0">男</el-radio>
                <el-radio value="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listUser, addUser, updateUser, delUser, resetUserPwd, changeUserStatus } from '@/api/system/user'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const multiple = ref(true)
const selectedIds = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const queryRef = ref(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, userName: '', phonenumber: '', status: '' })

const form = ref({})
const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 5, message: '密码不能少于5位', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await listUser(queryParams)
    userList.value = res.data.rows
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryRef.value?.resetFields(); handleQuery() }

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.userId)
  multiple.value = !selection.length
}

const reset = () => {
  form.value = { userId: null, userName: '', nickName: '', password: '', phone: '', email: '', sex: '0', status: '0', remark: '' }
  formRef.value?.resetFields()
}

const handleAdd = () => {
  reset()
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
}

const handleUpdate = (row) => {
  reset()
  Object.assign(form.value, row)
  dialogTitle.value = '修改用户'
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.value.userId) {
      await updateUser(form.value)
      ElMessage.success('修改成功')
    } else {
      await addUser(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  })
}

const handleDelete = async (row) => {
  const ids = row.userId || selectedIds.value.join(',')
  await ElMessageBox.confirm('是否确认删除用户编号为"' + ids + '"的数据？', '提示', { type: 'warning' })
  await delUser(ids)
  ElMessage.success('删除成功')
  getList()
}

const handleResetPwd = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入新密码', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: 'admin123'
  })
  await resetUserPwd({ userId: row.userId, password: value })
  ElMessage.success('密码已重置为: ' + value)
}

const handleStatusChange = async (row) => {
  const text = row.status === '0' ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(`确认要${text}用户"${row.userName}"吗？`, '提示', { type: 'warning' })
    await changeUserStatus({ userId: row.userId, status: row.status })
    ElMessage.success(`${text}成功`)
  } catch {
    row.status = row.status === '0' ? '1' : '0'
  }
}

onMounted(() => getList())
</script>
