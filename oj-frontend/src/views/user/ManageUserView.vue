<template>
  <div id="manageUserView">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <div class="title-icon-wrapper">
            <icon-user-group />
          </div>
          <div class="title-text">
            <h1 class="page-title">用户管理</h1>
            <p class="page-subtitle">管理和维护所有用户信息</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon total">
          <icon-user />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon admin">
          <icon-safe />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ adminCount }}</div>
          <div class="stat-label">管理员</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon user">
          <icon-user />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ userCount }}</div>
          <div class="stat-label">普通用户</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon banned">
          <icon-stop />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ banCount }}</div>
          <div class="stat-label">已封禁</div>
        </div>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="filter-section">
      <div class="filter-card">
        <div class="filter-header">
          <icon-filter />
          <span>筛选条件</span>
        </div>
        <div class="filter-content">
          <div class="filter-item">
            <label class="filter-label">用户名</label>
            <a-input
              v-model="searchParams.userName"
              placeholder="搜索用户名..."
              allow-clear
              class="filter-input"
            >
              <template #prefix>
                <icon-search />
              </template>
            </a-input>
          </div>
          <div class="filter-item">
            <label class="filter-label">角色</label>
            <a-select
              v-model="searchParams.userRole"
              placeholder="选择角色"
              allow-clear
              class="filter-input"
            >
              <a-option value="user">
                <span class="role-dot normal"></span>普通用户
              </a-option>
              <a-option value="admin">
                <span class="role-dot admin"></span>管理员
              </a-option>
              <a-option value="ban">
                <span class="role-dot banned"></span>已封禁
              </a-option>
            </a-select>
          </div>
          <div class="filter-actions">
            <a-button type="primary" @click="doSearch" class="search-btn">
              <icon-search />
              搜索
            </a-button>
            <a-button @click="resetSearch" class="reset-btn">
              <icon-refresh />
              重置
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-header-bar">
        <div class="table-title">
          <icon-list />
          <span>用户列表</span>
          <span class="total-badge">共 {{ total }} 条</span>
        </div>
        <div class="table-actions">
          <a-button @click="refreshData" class="refresh-btn">
            <icon-refresh />
            刷新
          </a-button>
        </div>
      </div>

      <a-table
        :columns="columns"
        :data="dataList"
        :loading="loading"
        :pagination="{
          showTotal: true,
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          total,
          showJumper: true,
        }"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
        class="data-table"
        row-key="id"
        :bordered="false"
      >
        <!-- 用户信息 -->
        <template #userInfo="{ record }">
          <div class="user-info-cell">
            <a-avatar :size="36" :style="{ backgroundColor: getAvatarColor(record.userRole) }">
              <img v-if="record.userAvatar" :src="record.userAvatar" />
              <span v-else>{{ (record.userName || record.userAccount || '?')[0] }}</span>
            </a-avatar>
            <div class="user-detail">
              <span class="user-name">{{ record.userName || '未设置昵称' }}</span>
              <span class="user-account">{{ record.userAccount }}</span>
            </div>
          </div>
        </template>

        <!-- 角色 -->
        <template #userRole="{ record }">
          <a-tag :color="getRoleColor(record.userRole)" size="small">
            {{ getRoleLabel(record.userRole) }}
          </a-tag>
        </template>

        <!-- 简介 -->
        <template #userProfile="{ record }">
          <span class="profile-text">{{ record.userProfile || '-' }}</span>
        </template>

        <!-- 创建时间 -->
        <template #createTime="{ record }">
          <div class="time-cell">
            <icon-clock-circle class="time-icon" />
            {{ formatTime(record.createTime) }}
          </div>
        </template>

        <!-- 操作 -->
        <template #action="{ record }">
          <div class="action-cell">
            <a-tooltip content="编辑角色">
              <a-button type="primary" size="small" @click="showEditModal(record)" class="edit-btn">
                <icon-edit />
              </a-button>
            </a-tooltip>
            <a-tooltip :content="record.userRole === 'ban' ? '解封' : '封禁'">
              <a-popconfirm
                :content="record.userRole === 'ban' ? '确定要解封该用户吗？' : '确定要封禁该用户吗？'"
                @ok="toggleBan(record)"
              >
                <a-button
                  :status="record.userRole === 'ban' ? 'success' : 'warning'"
                  size="small"
                  class="ban-btn"
                >
                  <icon-check-circle v-if="record.userRole === 'ban'" />
                  <icon-stop v-else />
                </a-button>
              </a-popconfirm>
            </a-tooltip>
            <a-popconfirm
              content="确定要删除该用户吗？此操作不可恢复！"
              @ok="doDelete(record)"
            >
              <a-tooltip content="删除">
                <a-button status="danger" size="small" class="delete-btn">
                  <icon-delete />
                </a-button>
              </a-tooltip>
            </a-popconfirm>
          </div>
        </template>
      </a-table>
    </div>

    <!-- 编辑用户模态框 -->
    <a-modal v-model:visible="editVisible" title="编辑用户" @ok="handleEditOk" :ok-loading="editLoading">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model="editForm.userName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model="editForm.userProfile" placeholder="请输入简介" :rows="3" />
        </a-form-item>
        <a-form-item label="角色">
          <a-select v-model="editForm.userRole">
            <a-option value="user">普通用户</a-option>
            <a-option value="admin">管理员</a-option>
            <a-option value="ban">封禁</a-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import axios from "axios";
import message from "@arco-design/web-vue/es/message";
import {
  IconUserGroup,
  IconUser,
  IconSafe,
  IconStop,
  IconFilter,
  IconSearch,
  IconRefresh,
  IconList,
  IconEdit,
  IconDelete,
  IconClockCircle,
  IconCheckCircle,
} from "@arco-design/web-vue/es/icon";

const loading = ref(false);
const dataList = ref<any[]>([]);
const total = ref(0);

const searchParams = ref({
  pageSize: 10,
  current: 1,
  userName: "",
  userRole: undefined as string | undefined,
});

// 统计（从后端获取各角色总数）
const adminCount = ref(0);
const userCount = ref(0);
const banCount = ref(0);

const loadStats = async () => {
  try {
    const [adminRes, userRes, banRes] = await Promise.all([
      axios.post("/api/user/list/page", { current: 1, pageSize: 1, userRole: "admin" }),
      axios.post("/api/user/list/page", { current: 1, pageSize: 1, userRole: "user" }),
      axios.post("/api/user/list/page", { current: 1, pageSize: 1, userRole: "ban" }),
    ]);
    if (adminRes.data.code === 0) adminCount.value = adminRes.data.data.total;
    if (userRes.data.code === 0) userCount.value = userRes.data.data.total;
    if (banRes.data.code === 0) banCount.value = banRes.data.data.total;
  } catch (e) {
    // ignore
  }
};

// 编辑相关
const editVisible = ref(false);
const editLoading = ref(false);
const editForm = ref({
  id: null as number | null,
  userName: "",
  userProfile: "",
  userRole: "user",
});

const columns = [
  {
    title: "用户信息",
    slotName: "userInfo",
    width: 250,
  },
  {
    title: "角色",
    slotName: "userRole",
    width: 120,
    align: "center",
  },
  {
    title: "简介",
    slotName: "userProfile",
    ellipsis: true,
  },
  {
    title: "注册时间",
    slotName: "createTime",
    width: 160,
  },
  {
    title: "操作",
    slotName: "action",
    width: 160,
    align: "center",
  },
];

const loadData = async () => {
  loading.value = true;
  try {
    const params: any = {
      current: searchParams.value.current,
      pageSize: searchParams.value.pageSize,
    };
    if (searchParams.value.userName) params.userName = searchParams.value.userName;
    if (searchParams.value.userRole) params.userRole = searchParams.value.userRole;

    const res = await axios.post("/api/user/list/page", params);
    if (res.data.code === 0) {
      dataList.value = res.data.data.records;
      total.value = res.data.data.total;
    } else {
      message.error("加载失败：" + res.data.message);
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
  loadStats();
});

const onPageChange = (page: number) => {
  searchParams.value.current = page;
  loadData();
};

const onPageSizeChange = (pageSize: number) => {
  searchParams.value.pageSize = pageSize;
  searchParams.value.current = 1;
  loadData();
};

const doSearch = () => {
  searchParams.value.current = 1;
  loadData();
};

const resetSearch = () => {
  searchParams.value = {
    pageSize: 10,
    current: 1,
    userName: "",
    userRole: undefined,
  };
  loadData();
};

const refreshData = () => {
  loadData();
  loadStats();
  message.success("数据已刷新");
};

const showEditModal = (record: any) => {
  editForm.value = {
    id: record.id,
    userName: record.userName || "",
    userProfile: record.userProfile || "",
    userRole: record.userRole || "user",
  };
  editVisible.value = true;
};

const handleEditOk = async () => {
  editLoading.value = true;
  try {
    const res = await axios.post("/api/user/update", editForm.value);
    if (res.data.code === 0) {
      message.success("用户信息更新成功");
      editVisible.value = false;
      loadData();
      loadStats();
    } else {
      message.error(res.data.message || "更新失败");
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  } finally {
    editLoading.value = false;
  }
};

const toggleBan = async (record: any) => {
  const newRole = record.userRole === "ban" ? "user" : "ban";
  try {
    const res = await axios.post("/api/user/update", {
      id: record.id,
      userRole: newRole,
    });
    if (res.data.code === 0) {
      message.success(newRole === "ban" ? "已封禁该用户" : "已解封该用户");
      loadData();
      loadStats();
    } else {
      message.error(res.data.message || "操作失败");
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  }
};

const doDelete = async (record: any) => {
  try {
    const res = await axios.post("/api/user/delete", { id: record.id });
    if (res.data.code === 0) {
      message.success("用户已删除");
      loadData();
      loadStats();
    } else {
      message.error(res.data.message || "删除失败");
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  }
};

const getRoleColor = (role: string) => {
  switch (role) {
    case "admin":
      return "arcoblue";
    case "ban":
      return "red";
    default:
      return "green";
  }
};

const getRoleLabel = (role: string) => {
  switch (role) {
    case "admin":
      return "管理员";
    case "ban":
      return "已封禁";
    default:
      return "普通用户";
  }
};

const getAvatarColor = (role: string) => {
  switch (role) {
    case "admin":
      return "#3b82f6";
    case "ban":
      return "#ef4444";
    default:
      return "#22c55e";
  }
};

const formatTime = (time: string) => {
  if (!time) return "-";
  const d = new Date(time);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
};
</script>

<style scoped>
#manageUserView {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-6);
  background-color: var(--bg-page);
  min-height: 100vh;
  font-family: var(--font-body);
  animation: fadeIn 0.5s var(--ease-out);
  position: relative;
}

#manageUserView::before {
  content: "";
  position: fixed;
  inset: 0;
  background: radial-gradient(
      at 25% 15%,
      rgba(59, 130, 246, 0.04) 0px,
      transparent 50%
    ),
    radial-gradient(at 75% 50%, rgba(6, 182, 212, 0.03) 0px, transparent 50%),
    radial-gradient(
      at 50% 85%,
      rgba(139, 92, 246, 0.03) 0px,
      transparent 50%
    );
  pointer-events: none;
  z-index: 0;
}

/* 页面标题区域 */
.page-header {
  margin-bottom: var(--space-6);
  animation: fadeInUp 0.6s var(--ease-out) backwards;
  position: relative;
  z-index: 1;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-card);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
}

.title-section {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.title-icon-wrapper {
  width: 48px;
  height: 48px;
  background: linear-gradient(
    135deg,
    var(--color-primary-500) 0%,
    var(--color-accent-500) 100%
  );
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.title-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.page-title {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.page-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
  margin-bottom: var(--space-6);
  position: relative;
  z-index: 1;
}

.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  transition: all var(--duration-fast);
  animation: scaleIn 0.5s var(--ease-out) backwards;
}

.stat-card:nth-child(1) {
  animation-delay: 0.1s;
}
.stat-card:nth-child(2) {
  animation-delay: 0.15s;
}
.stat-card:nth-child(3) {
  animation-delay: 0.2s;
}
.stat-card:nth-child(4) {
  animation-delay: 0.25s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-icon.total {
  background: var(--color-primary-50);
  color: var(--color-primary-500);
}
.stat-icon.admin {
  background: #dbeafe;
  color: #3b82f6;
}
.stat-icon.user {
  background: #dcfce7;
  color: #22c55e;
}
.stat-icon.banned {
  background: #fee2e2;
  color: #ef4444;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-top: var(--space-1);
}

/* 筛选区域 */
.filter-section {
  margin-bottom: var(--space-6);
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.6s var(--ease-out) 0.3s backwards;
}

.filter-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.filter-header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-5);
  background: var(--bg-subtle);
  border-bottom: 1px solid var(--border-default);
  color: var(--text-primary);
  font-weight: 600;
  font-size: var(--text-sm);
}

.filter-content {
  display: flex;
  align-items: flex-end;
  gap: var(--space-4);
  padding: var(--space-5);
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  min-width: 200px;
}

.filter-label {
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-secondary);
}

.filter-input {
  width: 100%;
}

.filter-input :deep(.arco-input),
.filter-input :deep(.arco-select-view) {
  border-radius: var(--radius-md);
}

.role-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: var(--space-2);
}

.role-dot.normal {
  background: #22c55e;
}
.role-dot.admin {
  background: #3b82f6;
}
.role-dot.banned {
  background: #ef4444;
}

.filter-actions {
  display: flex;
  gap: var(--space-3);
  margin-left: auto;
}

.search-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
}

.reset-btn {
  border-radius: var(--radius-md);
  font-weight: 500;
}

/* 表格区域 */
.table-section {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  overflow: visible;
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.6s var(--ease-out) 0.35s backwards;
}

.table-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-default);
}

.table-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-weight: 600;
  color: var(--text-primary);
}

.total-badge {
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 600;
  margin-left: var(--space-2);
}

.table-actions {
  display: flex;
  gap: var(--space-2);
}

.refresh-btn {
  border-radius: var(--radius-md);
}

.data-table {
  padding: 0 var(--space-2);
}

.data-table :deep(.arco-table-th) {
  background: var(--bg-subtle) !important;
  font-weight: 600;
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.data-table :deep(.arco-table-td) {
  border-bottom: 1px solid var(--border-default);
}

.data-table :deep(.arco-table-tr:hover .arco-table-td) {
  background: var(--bg-subtle) !important;
}

/* 用户信息单元格 */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.user-account {
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.profile-text {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.time-cell {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.time-icon {
  font-size: 14px;
  opacity: 0.6;
}

/* 操作按钮 */
.action-cell {
  display: flex;
  gap: var(--space-2);
  justify-content: center;
}

.edit-btn,
.ban-btn,
.delete-btn {
  border-radius: var(--radius-md);
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 768px) {
  #manageUserView {
    padding: var(--space-4);
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-content {
    flex-direction: column;
  }

  .filter-item {
    min-width: 100%;
  }

  .filter-actions {
    margin-left: 0;
    width: 100%;
  }
}
</style>
