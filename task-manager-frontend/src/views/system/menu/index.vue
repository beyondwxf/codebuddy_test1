<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-bar">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable style="width: 130px">
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
    </el-row>

    <!-- 树形表格 -->
    <el-table v-loading="loading" :data="menuList" row-key="menuId" :default-expand-all="true" :tree-props="{ children: 'children' }" stripe border>
      <el-table-column label="菜单名称" prop="menuName" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">
          <el-icon v-if="row.icon && row.icon !== '#'" style="margin-right: 6px"><component :is="row.icon" /></el-icon>
          {{ row.menuName }}
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="orderNum" width="70" align="center" />
      <el-table-column label="权限标识" prop="perms" min-width="160" show-overflow-tooltip />
      <el-table-column label="路由地址" prop="path" min-width="130" show-overflow-tooltip />
      <el-table-column label="类型" prop="menuType" width="70" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.menuType === 'M'" type="primary" size="small">目录</el-tag>
          <el-tag v-else-if="row.menuType === 'C'" type="success" size="small">菜单</el-tag>
          <el-tag v-else-if="row.menuType === 'F'" type="warning" size="small">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="70" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button v-if="row.menuType !== 'F'" link type="primary" icon="Plus" @click="handleAdd(row)">新增</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/修改弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="680px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
                check-strictly
                placeholder="选择上级菜单"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="M">目录</el-radio>
                <el-radio value="C">菜单</el-radio>
                <el-radio value="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.menuType !== 'F'">
          <el-col :span="12">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="如 system/user/index" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="权限标识">
              <el-input v-model="form.perms" placeholder="如 system:user:list" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'F'">
            <el-form-item label="权限标识">
              <el-input v-model="form.perms" placeholder="如 system:user:add" />
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
        <el-form-item label="图标" v-if="form.menuType !== 'F'">
          <el-input v-model="form.icon" placeholder="如 User、Setting" />
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
import { listMenu, addMenu, updateMenu, delMenu } from '@/api/system/menu'

const loading = ref(false)
const menuList = ref([])
const menuOptions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const queryRef = ref(null)

const queryParams = reactive({ menuName: '', status: '' })
const form = ref({})
const rules = { menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }] }

const getList = async () => {
  loading.value = true
  try {
    const res = await listMenu(queryParams)
    menuList.value = buildTree(res.data || [])
    menuOptions.value = [{ menuId: 0, menuName: '主目录', children: menuList.value }]
  } finally { loading.value = false }
}

const buildTree = (data, parentId = 0) => {
  return data.filter(item => item.parentId === parentId).map(item => ({
    ...item,
    children: buildTree(data, item.menuId)
  }))
}

const handleQuery = () => getList()
const resetQuery = () => { queryRef.value?.resetFields(); getList() }

const reset = () => {
  form.value = { menuId: null, parentId: 0, menuType: 'M', menuName: '', path: '', component: '', perms: '', icon: '', orderNum: 0, status: '0', visible: '0' }
  formRef.value?.resetFields()
}

const handleAdd = (row) => {
  reset()
  if (row) form.value.parentId = row.menuId
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
}

const handleUpdate = (row) => {
  reset()
  Object.assign(form.value, row)
  dialogTitle.value = '修改菜单'
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.value.menuId) { await updateMenu(form.value); ElMessage.success('修改成功') }
    else { await addMenu(form.value); ElMessage.success('新增成功') }
    dialogVisible.value = false; getList()
  })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`是否确认删除菜单"${row.menuName}"？`, '提示', { type: 'warning' })
  await delMenu(row.menuId); ElMessage.success('删除成功'); getList()
}

onMounted(() => getList())
</script>
