<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-bar">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input v-model="queryParams.roleKey" placeholder="请输入权限字符" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="角色状态" clearable style="width: 130px">
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
    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="角色ID" prop="roleId" width="80" align="center" />
      <el-table-column label="角色名称" prop="roleName" min-width="120" show-overflow-tooltip />
      <el-table-column label="权限字符" prop="roleKey" min-width="120" show-overflow-tooltip />
      <el-table-column label="排序" prop="roleSort" width="80" align="center" />
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" min-width="160" align="center" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="角色排序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
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
import { listRole, addRole, updateRole, delRole } from '@/api/system/role'

const loading = ref(false)
const roleList = ref([])
const total = ref(0)
const multiple = ref(true)
const selectedIds = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const queryRef = ref(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, roleName: '', roleKey: '', status: '' })
const form = ref({})
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入权限字符', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await listRole(queryParams)
    roleList.value = res.data.rows
    total.value = res.data.total
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryRef.value?.resetFields(); handleQuery() }
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(i => i.roleId)
  multiple.value = !selection.length
}

const reset = () => { form.value = { roleId: null, roleName: '', roleKey: '', roleSort: 0, status: '0', remark: '' }; formRef.value?.resetFields() }

const handleAdd = () => { reset(); dialogTitle.value = '新增角色'; dialogVisible.value = true }
const handleUpdate = (row) => { reset(); Object.assign(form.value, row); dialogTitle.value = '修改角色'; dialogVisible.value = true }

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.value.roleId) { await updateRole(form.value); ElMessage.success('修改成功') }
    else { await addRole(form.value); ElMessage.success('新增成功') }
    dialogVisible.value = false; getList()
  })
}

const handleDelete = async (row) => {
  const ids = row.roleId || selectedIds.value.join(',')
  await ElMessageBox.confirm('是否确认删除角色编号为"' + ids + '"的数据？', '提示', { type: 'warning' })
  await delRole(ids); ElMessage.success('删除成功'); getList()
}

onMounted(() => getList())
</script>
