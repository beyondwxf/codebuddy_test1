<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-bar">
      <el-form-item label="部门名称" prop="deptName">
        <el-input v-model="queryParams.deptName" placeholder="请输入部门名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="部门状态" clearable style="width: 130px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd()">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button @click="toggleExpandAll">{{ isExpandAll ? '折叠全部' : '展开全部' }}</el-button>
      </el-col>
    </el-row>

    <!-- 树形表格 -->
    <el-table
      v-loading="loading"
      :data="deptList"
      row-key="deptId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children' }"
      stripe
      border
    >
      <el-table-column label="部门名称" prop="deptName" min-width="200" show-overflow-tooltip />
      <el-table-column label="排序" prop="orderNum" width="70" align="center" />
      <el-table-column label="负责人" prop="leader" min-width="100" show-overflow-tooltip />
      <el-table-column label="联系电话" prop="phone" min-width="120" show-overflow-tooltip />
      <el-table-column label="邮箱" prop="email" min-width="160" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="70" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" min-width="160" align="center" />
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button link type="primary" icon="Plus" @click="handleAdd(row)">新增</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/修改弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="deptOptions"
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
            check-strictly
            placeholder="选择上级部门"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
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
import { listDept, addDept, updateDept, delDept } from '@/api/system/dept'

const loading = ref(false)
const deptList = ref([])
const deptOptions = ref([])
const isExpandAll = ref(true)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const queryRef = ref(null)

const queryParams = reactive({ deptName: '', status: '' })
const form = ref({})
const rules = { deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }] }

const getList = async () => {
  loading.value = true
  try {
    const res = await listDept(queryParams)
    deptList.value = buildTree(res.data || [])
    deptOptions.value = [{ deptId: 0, deptName: '主部门', children: deptList.value }]
  } finally { loading.value = false }
}

const buildTree = (data, parentId = 0) => {
  return data.filter(item => item.parentId === parentId).map(item => ({
    ...item,
    children: buildTree(data, item.deptId)
  }))
}

const handleQuery = () => getList()
const resetQuery = () => { queryRef.value?.resetFields(); getList() }
const toggleExpandAll = () => { isExpandAll.value = !isExpandAll.value }

const reset = () => {
  form.value = { deptId: null, parentId: 0, deptName: '', orderNum: 0, leader: '', phone: '', email: '', status: '0' }
  formRef.value?.resetFields()
}

const handleAdd = (row) => {
  reset()
  if (row) form.value.parentId = row.deptId
  dialogTitle.value = '新增部门'
  dialogVisible.value = true
}

const handleUpdate = (row) => {
  reset()
  Object.assign(form.value, row)
  dialogTitle.value = '修改部门'
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.value.deptId) { await updateDept(form.value); ElMessage.success('修改成功') }
    else { await addDept(form.value); ElMessage.success('新增成功') }
    dialogVisible.value = false; getList()
  })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`是否确认删除部门"${row.deptName}"？`, '提示', { type: 'warning' })
  await delDept(row.deptId); ElMessage.success('删除成功'); getList()
}

onMounted(() => getList())
</script>
