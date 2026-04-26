<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-bar">
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="SKU编码" prop="skuCode">
        <el-input v-model="queryParams.skuCode" placeholder="请输入SKU编码" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="商品状态" clearable style="width: 130px">
          <el-option
            v-for="item in productStatusOptions"
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
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="预览图" prop="previewImage" width="80" align="center">
        <template #default="{ row }">
          <el-image
            v-if="row.previewImage"
            :src="row.previewImage"
            :preview-src-list="[row.previewImage]"
            fit="cover"
            style="width: 50px; height: 50px; border-radius: 4px"
            preview-teleported
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" prop="productName" min-width="160" show-overflow-tooltip />
      <el-table-column label="SKU编码" prop="skuCode" width="140" show-overflow-tooltip />
      <el-table-column label="售价" prop="salePrice" width="100" align="right">
        <template #default="{ row }">
          <span style="color: #e6a23c; font-weight: 600">¥{{ row.salePrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="进货价" prop="purchasePrice" width="100" align="right">
        <template #default="{ row }">
          <span>¥{{ row.purchasePrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="单位" prop="unit" width="60" align="center" />
      <el-table-column label="简介" prop="description" min-width="180" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
            {{ productStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="900px" append-to-body destroy-on-close top="5vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-tabs v-model="activeTab">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="商品名称" prop="productName">
                  <el-input v-model="form.productName" placeholder="请输入商品名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="SKU编码" prop="skuCode">
                  <el-input v-model="form.skuCode" placeholder="请输入SKU编码" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="商品简介" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品简介" />
            </el-form-item>
            <el-form-item label="预览图" prop="previewImage">
              <el-input v-model="form.previewImage" placeholder="请输入预览图URL" />
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="售价" prop="salePrice">
                  <el-input-number v-model="form.salePrice" :precision="2" :min="0" :step="1" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="进货价" prop="purchasePrice">
                  <el-input-number v-model="form.purchasePrice" :precision="2" :min="0" :step="1" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="单位" prop="unit">
                  <el-input v-model="form.unit" placeholder="单位" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-radio-group v-model="form.status">
                    <el-radio v-for="item in productStatusOptions" :key="item.dictValue" :value="item.dictValue">
                      {{ item.dictLabel }}
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-tab-pane>

          <!-- 宣传页内容 -->
          <el-tab-pane label="移动端宣传页" name="content">
            <div class="editor-container">
              <div style="border: 1px solid #ccc; border-radius: 4px">
                <!-- 工具栏 -->
                <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" style="border-bottom: 1px solid #ccc" />
                <!-- 编辑器 -->
                <Editor
                  v-model="form.mobileContent"
                  :defaultConfig="editorConfig"
                  style="height: 400px; overflow-y: hidden"
                  @onCreated="handleEditorCreated"
                />
              </div>
            </div>
          </el-tab-pane>

          <!-- 供应商信息 -->
          <el-tab-pane label="供应商" name="supplier">
            <el-row :gutter="10" class="mb-8">
              <el-col :span="24">
                <el-button type="primary" plain icon="Plus" size="small" @click="addSupplierRow">添加供应商</el-button>
              </el-col>
            </el-row>
            <el-table :data="form.supplierList" border size="small" style="width: 100%">
              <el-table-column label="供应商" min-width="180">
                <template #default="{ row, $index }">
                  <el-select v-model="row.supplierId" placeholder="请选择供应商" filterable style="width: 100%">
                    <el-option
                      v-for="item in supplierOptions"
                      :key="item.supplierId"
                      :label="item.companyName"
                      :value="item.supplierId"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="供应商商品编码" width="160">
                <template #default="{ row }">
                  <el-input v-model="row.supplierSkuCode" placeholder="供应商商品编码" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="供应商报价" width="130">
                <template #default="{ row }">
                  <el-input-number v-model="row.supplierPrice" :precision="2" :min="0" size="small" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="交货周期(天)" width="120">
                <template #default="{ row }">
                  <el-input-number v-model="row.leadTime" :min="0" size="small" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="默认" width="70" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.isDefault" active-value="1" inactive-value="0" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="备注" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.remark" placeholder="备注" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="70" align="center" fixed="right">
                <template #default="{ $index }">
                  <el-button link type="danger" icon="Delete" size="small" @click="removeSupplierRow($index)" />
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 库存信息 -->
          <el-tab-pane label="库存管理" name="inventory">
            <el-row :gutter="10" class="mb-8">
              <el-col :span="24">
                <el-button type="primary" plain icon="Plus" size="small" @click="addInventoryRow">添加仓库</el-button>
              </el-col>
            </el-row>
            <el-table :data="form.inventoryList" border size="small" style="width: 100%">
              <el-table-column label="仓库" min-width="200">
                <template #default="{ row }">
                  <el-select v-model="row.warehouseId" placeholder="请选择仓库" filterable style="width: 100%">
                    <el-option
                      v-for="item in warehouseOptions"
                      :key="item.warehouseId"
                      :label="`${item.warehouseName}(${item.warehouseCode})`"
                      :value="item.warehouseId"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="库存数量" width="140">
                <template #default="{ row }">
                  <el-input-number v-model="row.stockQuantity" :min="0" size="small" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="预警数量" width="140">
                <template #default="{ row }">
                  <el-input-number v-model="row.warningQuantity" :min="0" size="small" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="备注" min-width="150">
                <template #default="{ row }">
                  <el-input v-model="row.remark" placeholder="备注" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="70" align="center" fixed="right">
                <template #default="{ $index }">
                  <el-button link type="danger" icon="Delete" size="small" @click="removeInventoryRow($index)" />
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, shallowRef, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listProduct, getProduct, addProduct, updateProduct, delProduct, exportProduct } from '@/api/wms/product'
import { listAllWarehouse } from '@/api/wms/warehouse'
import { listSupplier } from '@/api/system/supplier'
import { getDicts } from '@/api/system/dict'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

// ========== 字典数据 ==========
const productStatusOptions = ref([])

// ========== 下拉选项 ==========
const supplierOptions = ref([])
const warehouseOptions = ref([])

// ========== 富文本编辑器 ==========
const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入移动端宣传页内容...' }

const handleEditorCreated = (editor) => {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  if (editorRef.value) {
    editorRef.value.destroy()
  }
})

// ========== 列表状态 ==========
const loading = ref(false)
const productList = ref([])
const total = ref(0)
const multiple = ref(true)
const selectedIds = ref([])

// ========== 搜索参数 ==========
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productName: '',
  skuCode: '',
  status: ''
})
const queryRef = ref(null)

// ========== 弹窗状态 ==========
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const activeTab = ref('basic')
const form = ref({})

// ========== 表单校验规则 ==========
const rules = {
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  skuCode: [{ required: true, message: '请输入SKU编码', trigger: 'blur' }],
  salePrice: [{ required: true, message: '请输入售价', trigger: 'blur' }]
}

// ========== 辅助方法 ==========
const productStatusLabel = (val) => {
  const item = productStatusOptions.value.find(d => d.dictValue === val)
  return item ? item.dictLabel : val
}

// ========== 加载字典和下拉数据 ==========
const loadDictData = async () => {
  try {
    const statusRes = await getDicts('wms_product_status')
    productStatusOptions.value = statusRes.data || []
  } catch {
    // 字典加载失败不影响主流程
  }
}

const loadDropdownData = async () => {
  try {
    const [supplierRes, warehouseRes] = await Promise.all([
      listSupplier({ pageNum: 1, pageSize: 10000 }),
      listAllWarehouse()
    ])
    supplierOptions.value = supplierRes.data?.rows || []
    warehouseOptions.value = warehouseRes.data || []
  } catch {
    // 下拉数据加载失败不影响主流程
  }
}

// ========== 查询列表 ==========
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      productName: queryParams.productName || undefined,
      skuCode: queryParams.skuCode || undefined,
      status: queryParams.status || undefined
    }
    const res = await listProduct(params)
    productList.value = res.data.rows
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryRef.value?.resetFields(); handleQuery() }
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.productId)
  multiple.value = !selection.length
}

// ========== 重置表单 ==========
const reset = () => {
  form.value = {
    productId: null, productName: '', skuCode: '', description: '',
    previewImage: '', mobileContent: '',
    salePrice: 0, purchasePrice: 0, unit: '个', status: '0',
    remark: '',
    supplierList: [],
    inventoryList: []
  }
  activeTab.value = 'basic'
  formRef.value?.resetFields()
}

// ========== 新增 ==========
const handleAdd = () => {
  reset()
  dialogTitle.value = '新增商品'
  dialogVisible.value = true
}

// ========== 修改 ==========
const handleUpdate = async (row) => {
  reset()
  const res = await getProduct(row.productId)
  const data = res.data
  // 确保子列表有值
  data.supplierList = data.supplierList || []
  data.inventoryList = data.inventoryList || []
  Object.assign(form.value, data)
  dialogTitle.value = '修改商品'
  dialogVisible.value = true
}

// ========== 提交表单 ==========
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.value.productId) {
      await updateProduct(form.value)
      ElMessage.success('修改成功')
    } else {
      await addProduct(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  })
}

// ========== 删除 ==========
const handleDelete = async (row) => {
  const ids = row.productId ? row.productId : selectedIds.value.join(',')
  await ElMessageBox.confirm('是否确认删除选中的商品数据？', '提示', { type: 'warning' })
  await delProduct(ids)
  ElMessage.success('删除成功')
  getList()
}

// ========== 导出 ==========
const handleExport = async () => {
  try {
    await ElMessageBox.confirm('是否确认导出商品数据？', '提示', { type: 'warning' })
    const params = {
      productName: queryParams.productName || undefined,
      skuCode: queryParams.skuCode || undefined,
      status: queryParams.status || undefined
    }
    const res = await exportProduct(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '商品数据.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功')
  } catch {
    // 用户取消
  }
}

// ========== 供应商动态行 ==========
const addSupplierRow = () => {
  form.value.supplierList.push({
    supplierId: null, supplierSkuCode: '', supplierPrice: 0,
    leadTime: null, isDefault: form.value.supplierList.length === 0 ? '1' : '0',
    remark: ''
  })
}

const removeSupplierRow = (index) => {
  form.value.supplierList.splice(index, 1)
}

// ========== 库存动态行 ==========
const addInventoryRow = () => {
  form.value.inventoryList.push({
    warehouseId: null, stockQuantity: 0, warningQuantity: 0, remark: ''
  })
}

const removeInventoryRow = (index) => {
  form.value.inventoryList.splice(index, 1)
}

// ========== 初始化 ==========
onMounted(() => {
  loadDictData()
  loadDropdownData()
  getList()
})
</script>

<style scoped>
.editor-container {
  width: 100%;
}
.mb-8 {
  margin-bottom: 8px;
}
</style>
