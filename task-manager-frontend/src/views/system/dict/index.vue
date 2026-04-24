<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：字典类型 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>字典类型</span>
              <el-button type="primary" link icon="Plus" @click="handleAddType">新增</el-button>
            </div>
          </template>
          <!-- 搜索 -->
          <el-input v-model="typeQuery" placeholder="搜索字典类型" clearable prefix-icon="Search" class="mb-12" />
          <!-- 类型列表 -->
          <el-table :data="filteredTypes" highlight-current-row @current-change="handleTypeSelect" stripe border size="small" max-height="520">
            <el-table-column prop="dictName" label="字典名称" show-overflow-tooltip />
            <el-table-column prop="dictType" label="字典类型" show-overflow-tooltip />
            <el-table-column width="100" align="center">
              <template #default="{ row }">
                <el-button link type="primary" icon="Edit" @click.stop="handleUpdateType(row)" />
                <el-button link type="danger" icon="Delete" @click.stop="handleDeleteType(row)" />
              </template>
            </el-table-column>
          </el-table>
          <!-- 分页 -->
          <div class="pagination-container" style="padding-top: 10px">
            <el-pagination
              v-model:current-page="typeParams.pageNum"
              :total="typeTotal"
              :page-size="typeParams.pageSize"
              layout="prev, pager, next"
              small
              @current-change="getTypeList"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：字典数据 -->
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>字典数据{{ currentType ? ' [' + currentType.dictType + ']' : '' }}</span>
              <el-button type="primary" link icon="Plus" @click="handleAddData" :disabled="!currentType">新增</el-button>
            </div>
          </template>
          <el-table v-loading="dataLoading" :data="dataList" stripe border>
            <el-table-column label="字典标签" prop="dictLabel" min-width="120" />
            <el-table-column label="字典键值" prop="dictValue" width="100" align="center" />
            <el-table-column label="排序" prop="dictSort" width="70" align="center" />
            <el-table-column label="状态" prop="status" width="70" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
                  {{ row.status === '0' ? '正常' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" min-width="120" show-overflow-tooltip />
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button link type="primary" icon="Edit" @click="handleUpdateData(row)" />
                <el-button link type="danger" icon="Delete" @click="handleDeleteData(row)" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 字典类型弹窗 -->
    <el-dialog :title="typeDialogTitle" v-model="typeDialogVisible" width="500px" append-to-body destroy-on-close>
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="80px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="typeForm.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="typeForm.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="typeForm.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="typeForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTypeForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 字典数据弹窗 -->
    <el-dialog :title="dataDialogTitle" v-model="dataDialogVisible" width="500px" append-to-body destroy-on-close>
      <el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="80px">
        <el-form-item label="字典类型">
          <el-input v-model="dataForm.dictType" disabled />
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="dataForm.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="dataForm.dictValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="排序" prop="dictSort">
          <el-input-number v-model="dataForm.dictSort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dataForm.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dataForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDataForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDictType, addDictType, updateDictType, delDictType, listDictData, addDictData, updateDictData, delDictData } from '@/api/system/dict'

const typeQuery = ref('')
const typeTotal = ref(0)
const typeList = ref([])
const typeParams = reactive({ pageNum: 1, pageSize: 100 })
const currentType = ref(null)
const dataList = ref([])
const dataLoading = ref(false)

// 字典类型弹窗
const typeDialogVisible = ref(false)
const typeDialogTitle = ref('')
const typeFormRef = ref(null)
const typeForm = ref({})
const typeRules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }]
}

// 字典数据弹窗
const dataDialogVisible = ref(false)
const dataDialogTitle = ref('')
const dataFormRef = ref(null)
const dataForm = ref({})
const dataRules = {
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典键值', trigger: 'blur' }]
}

const filteredTypes = computed(() => {
  if (!typeQuery.value) return typeList.value
  const q = typeQuery.value.toLowerCase()
  return typeList.value.filter(i => i.dictName.toLowerCase().includes(q) || i.dictType.toLowerCase().includes(q))
})

const getTypeList = async () => {
  const res = await listDictType(typeParams)
  typeList.value = res.data.rows
  typeTotal.value = res.data.total
}

const handleTypeSelect = (row) => {
  currentType.value = row
  if (row) getDataList(row.dictType)
}

const getDataList = async (dictType) => {
  dataLoading.value = true
  try {
    const res = await listDictData({ dictType, pageNum: 1, pageSize: 100 })
    dataList.value = res.data.rows
  } finally { dataLoading.value = false }
}

const resetTypeForm = () => { typeForm.value = { dictId: null, dictName: '', dictType: '', status: '0', remark: '' }; typeFormRef.value?.resetFields() }
const resetDataForm = () => { dataForm.value = { dictCode: null, dictType: '', dictLabel: '', dictValue: '', dictSort: 0, status: '0', remark: '' }; dataFormRef.value?.resetFields() }

const handleAddType = () => { resetTypeForm(); typeDialogTitle.value = '新增字典类型'; typeDialogVisible.value = true }
const handleUpdateType = (row) => { resetTypeForm(); Object.assign(typeForm.value, row); typeDialogTitle.value = '修改字典类型'; typeDialogVisible.value = true }

const submitTypeForm = () => {
  typeFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (typeForm.value.dictId) { await updateDictType(typeForm.value); ElMessage.success('修改成功') }
    else { await addDictType(typeForm.value); ElMessage.success('新增成功') }
    typeDialogVisible.value = false; getTypeList()
  })
}

const handleDeleteType = async (row) => {
  await ElMessageBox.confirm(`是否确认删除字典类型"${row.dictName}"？`, '提示', { type: 'warning' })
  await delDictType(row.dictId); ElMessage.success('删除成功'); getTypeList()
}

const handleAddData = () => { resetDataForm(); dataForm.value.dictType = currentType.value.dictType; dataDialogTitle.value = '新增字典数据'; dataDialogVisible.value = true }
const handleUpdateData = (row) => { resetDataForm(); Object.assign(dataForm.value, row); dataDialogTitle.value = '修改字典数据'; dataDialogVisible.value = true }

const submitDataForm = () => {
  dataFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (dataForm.value.dictCode) { await updateDictData(dataForm.value); ElMessage.success('修改成功') }
    else { await addDictData(dataForm.value); ElMessage.success('新增成功') }
    dataDialogVisible.value = false; if (currentType.value) getDataList(currentType.value.dictType)
  })
}

const handleDeleteData = async (row) => {
  await ElMessageBox.confirm('是否确认删除该字典数据？', '提示', { type: 'warning' })
  await delDictData(row.dictCode); ElMessage.success('删除成功'); if (currentType.value) getDataList(currentType.value.dictType)
}

onMounted(() => getTypeList())
</script>
