<template>
  <div class="dashboard-container">
    <!-- 欢迎语 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h2>{{ greeting }}，{{ userInfo.nickName || '管理员' }}</h2>
        <p class="welcome-desc">{{ currentDate }}</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card" :style="{ borderTop: `3px solid ${item.color}` }">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-title">{{ item.title }}</p>
              <p class="stat-value" :style="{ color: item.color }">{{ item.value }}</p>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: item.color + '15', color: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card shadow="hover" class="quick-card">
      <template #header>
        <span>快捷操作</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="4" v-for="item in quickActions" :key="item.title">
          <div class="quick-item" @click="handleQuick(item.path)">
            <el-icon :size="24" :color="item.color"><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 系统信息 -->
    <el-card shadow="hover" class="info-card">
      <template #header>
        <span>系统信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="系统名称">RuoYi Admin 后台管理系统</el-descriptions-item>
        <el-descriptions-item label="版本号">v1.0.0</el-descriptions-item>
        <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
        <el-descriptions-item label="后端框架">Spring Boot 3</el-descriptions-item>
        <el-descriptions-item label="数据库">MySQL 8.x</el-descriptions-item>
        <el-descriptions-item label="缓存">Redis</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/useUserStore'
import { listUser } from '@/api/system/user'
import { listRole } from '@/api/system/role'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.user || {})

// 问候语
const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 9) return '早上好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 17) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => {
  const d = new Date()
  const weekMap = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekMap[d.getDay()]}`
})

// 统计卡片（初始显示占位符，加载完成后显示真实数据）
const statCards = ref([
  { title: '用户总数', value: '-', icon: 'User', color: '#409EFF' },
  { title: '角色数量', value: '-', icon: 'UserFilled', color: '#67C23A' },
  { title: '菜单数量', value: '-', icon: 'Menu', color: '#E6A23C' },
  { title: '部门数量', value: '-', icon: 'OfficeBuilding', color: '#F56C6C' }
])

/** 从后端加载真实统计数据 */
async function loadStats() {
  try {
    const [userRes, roleRes] = await Promise.all([
      listUser({ pageNum: 1, pageSize: 1 }),
      listRole({ pageNum: 1, pageSize: 1 })
    ])
    statCards.value[0].value = userRes.data.total
    statCards.value[1].value = roleRes.data.total
  } catch (e) {
    console.warn('统计数据加载失败，使用默认值')
  }
}

onMounted(() => {
  loadStats()
})

// 快捷操作
const quickActions = ref([
  { title: '用户管理', icon: 'User', path: '/system/user', color: '#409EFF' },
  { title: '角色管理', icon: 'UserFilled', path: '/system/role', color: '#67C23A' },
  { title: '菜单管理', icon: 'Menu', path: '/system/menu', color: '#E6A23C' },
  { title: '部门管理', icon: 'OfficeBuilding', path: '/system/dept', color: '#F56C6C' },
  { title: '字典管理', icon: 'Collection', path: '/system/dict', color: '#909399' },
  { title: '操作日志', icon: 'Document', path: '/monitor/operlog', color: '#b37feb' }
])

const handleQuick = (path) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  .welcome-section {
    margin-bottom: 20px;

    .welcome-content {
      h2 {
        font-size: 22px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 6px;
      }

      .welcome-desc {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .stat-row {
    margin-bottom: 16px;
  }

  .stat-card {
    border-radius: 8px;

    .stat-content {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .stat-info {
        .stat-title {
          font-size: 13px;
          color: #909399;
          margin-bottom: 8px;
        }

        .stat-value {
          font-size: 28px;
          font-weight: 600;
        }
      }

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .quick-card {
    margin-bottom: 16px;
    border-radius: 8px;

    .quick-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      padding: 12px 0;
      cursor: pointer;
      border-radius: 8px;
      transition: background 0.2s;

      &:hover {
        background: #f5f7fa;
      }

      span {
        font-size: 13px;
        color: #606266;
      }
    }
  }

  .info-card {
    border-radius: 8px;
  }
}
</style>
