<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-bar">
      <el-form-item label="仓库名称" prop="warehouseName">
        <el-input v-model="queryParams.warehouseName" placeholder="请输入仓库名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库编码" prop="warehouseCode">
        <el-input v-model="queryParams.warehouseCode" placeholder="请输入仓库编码" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="省份" prop="province">
        <el-select
          v-model="queryParams.provinceList"
          multiple
          filterable
          collapse-tags
          collapse-tags-tooltip
          placeholder="请选择省份"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in provinceOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="仓库类型" prop="warehouseType">
        <el-select v-model="queryParams.warehouseType" placeholder="仓库类型" clearable style="width: 130px">
          <el-option
            v-for="item in warehouseTypeOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="仓库状态" clearable style="width: 130px">
          <el-option
            v-for="item in statusOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
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
      <el-col :span="1.5">
        <el-button type="info" plain icon="Upload" @click="handleImport">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="warehouseList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="仓库名称" prop="warehouseName" min-width="140" show-overflow-tooltip />
      <el-table-column label="仓库编码" prop="warehouseCode" width="130" show-overflow-tooltip />
      <el-table-column label="省份" prop="province" width="90" align="center" />
      <el-table-column label="详细地址" prop="address" min-width="180" show-overflow-tooltip />
      <el-table-column label="仓库类型" prop="warehouseType" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.warehouseType === '0' ? 'primary' : 'success'" size="small">
            {{ warehouseTypeLabel(row.warehouseType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="140" show-overflow-tooltip />
      <el-table-column label="操作" width="150" align="center" fixed="right">
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="warehouseName">
              <el-input v-model="form.warehouseName" placeholder="请输入仓库名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库编码" prop="warehouseCode">
              <el-input v-model="form.warehouseCode" placeholder="请输入仓库编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="省份" prop="province">
              <el-select v-model="form.province" placeholder="请选择省份" filterable clearable style="width: 100%">
                <el-option
                  v-for="item in provinceOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库类型" prop="warehouseType">
              <el-select v-model="form.warehouseType" placeholder="请选择仓库类型" style="width: 100%">
                <el-option
                  v-for="item in warehouseTypeOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" type="textarea" :rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入弹窗 -->
    <el-dialog title="仓库数据导入" v-model="importVisible" width="400px" append-to-body destroy-on-close>
      <el-upload
        ref="uploadRef"
        :action="importUrl"
        :headers="uploadHeaders"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        drag
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">
            仅允许导入xlsx、xls格式文件，
            <el-link type="primary" :underline="false" @click="handleDownloadTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="submitImport">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { listWarehouse, getWarehouse, addWarehouse, updateWarehouse, delWarehouse, exportWarehouse, importWarehouse, downloadWarehouseTemplate } from '@/api/wms/warehouse'
import { getDicts } from '@/api/system/dict'
import { getToken } from '@/utils/auth'

// 字典数据
const provinceOptions = ref([])
const warehouseTypeOptions = ref([])
const statusOptions = ref([
  { dictLabel: '正常', dictValue: '0' },
  { dictLabel: '停用', dictValue: '1' }
])

// 列表状态
const loading = ref(false)
const warehouseList = ref([])
const total = ref(0)
const multiple = ref(true)
const selectedIds = ref([])

// 搜索参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  warehouseName: '',
  warehouseCode: '',
  provinceList: [],
  warehouseType: '',
  status: ''
})
const queryRef = ref(null)

// 弹窗状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({})

// 导入状态
const importVisible = ref(false)
const uploadRef = ref(null)
const importUrl = '/dev-api/api/wms/warehouse/import'
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + getToken() }))

// 表单校验规则
const rules = {
  warehouseName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
  warehouseCode: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }]
}

// 仓库类型标签
const warehouseTypeLabel = (val) => {
  const item = warehouseTypeOptions.value.find(d => d.dictValue === val)
  return item ? item.dictLabel : val
}

// 加载字典数据
const loadDictData = async () => {
  try {
    const [provinceRes, typeRes] = await Promise.all([
      getDicts('supplier_province'),
      getDicts('wms_warehouse_type')
    ])
    provinceOptions.value = provinceRes.data || []
    warehouseTypeOptions.value = typeRes.data || []
  } catch {
    // 字典加载失败不影响主流程
  }
}

// 查询列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      warehouseName: queryParams.warehouseName || undefined,
      warehouseCode: queryParams.warehouseCode || undefined,
      province: queryParams.provinceList.length > 0 ? queryParams.provinceList.join(',') : undefined,
      warehouseType: queryParams.warehouseType || undefined,
      status: queryParams.status || undefined
    }
    const res = await listWarehouse(params)
    warehouseList.value = res.data.rows
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryRef.value?.resetFields(); handleQuery() }
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.warehouseId)
  multiple.value = !selection.length
}

// 重置表单
const reset = () => {
  form.value = {
    warehouseId: null, warehouseName: '', warehouseCode: '', province: '',
    address: '', warehouseType: '0', status: '0', remark: ''
  }
  formRef.value?.resetFields()
}

// 新增
const handleAdd = () => {
  reset()
  dialogTitle.value = '新增仓库'
  dialogVisible.value = true
}

// 修改
const handleUpdate = async (row) => {
  reset()
  const res = await getWarehouse(row.warehouseId)
  Object.assign(form.value, res.data)
  dialogTitle.value = '修改仓库'
  dialogVisible.value = true
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.value.warehouseId) {
      await updateWarehouse(form.value)
      ElMessage.success('修改成功')
    } else {
      await addWarehouse(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  })
}

// 删除
const handleDelete = async (row) => {
  const ids = row.warehouseId ? row.warehouseId : selectedIds.value.join(',')
  await ElMessageBox.confirm('是否确认删除选中的仓库数据？', '提示', { type: 'warning' })
  await delWarehouse(ids)
  ElMessage.success('删除成功')
  getList()
}

// 导出
const handleExport = async () => {
  try {
    await ElMessageBox.confirm('是否确认导出仓库数据？', '提示', { type: 'warning' })
    const params = {
      warehouseName: queryParams.warehouseName || undefined,
      warehouseCode: queryParams.warehouseCode || undefined,
      province: queryParams.provinceList.length > 0 ? queryParams.provinceList.join(',') : undefined,
      warehouseType: queryParams.warehouseType || undefined,
      status: queryParams.status || undefined
    }
    const res = await exportWarehouse(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '仓库数据.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功')
  } catch {
    // 用户取消
  }
}

// 导入
const handleImport = () => { importVisible.value = true }

const beforeUpload = (file) => {
  const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
  if (!isExcel) {
    ElMessage.error('仅允许导入xlsx、xls格式文件')
    return false
  }
  return true
}

const submitImport = () => { uploadRef.value?.submit() }

const handleImportSuccess = (response) => {
  const msg = response.data || response.message || '导入成功'
  ElMessage.success(msg)
  importVisible.value = false
  getList()
}

const handleImportError = () => { ElMessage.error('导入失败，请检查文件格式') }

// 下载导入模板
const handleDownloadTemplate = async () => {
  try {
    const res = await downloadWarehouseTemplate()
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '仓库导入模板.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
  } catch {
    ElMessage.error('模板下载失败')
  }
}

onMounted(() => {
  loadDictData()
  getList()
})
</script>
