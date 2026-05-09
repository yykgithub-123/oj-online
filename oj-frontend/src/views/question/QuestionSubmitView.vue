<template>
  <div id="questionSubmitView">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <div class="title-icon-wrapper">
            <icon-code />
          </div>
          <div class="title-text">
            <h1 class="page-title">提交记录</h1>
            <p class="page-subtitle">查看和管理所有题目提交记录</p>
          </div>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="refreshData" class="refresh-btn">
            <template #icon>
              <icon-refresh />
            </template>
            刷新数据
          </a-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon total">
          <icon-file />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">总提交数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon success">
          <icon-check-circle />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ successCount }}</div>
          <div class="stat-label">通过数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon error">
          <icon-close-circle />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ failCount }}</div>
          <div class="stat-label">失败数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rate">
          <icon-trophy />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ successRate }}%</div>
          <div class="stat-label">通过率</div>
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
            <label class="filter-label">题号</label>
            <a-input
              v-model="searchParams.questionId"
              placeholder="输入题号"
              allow-clear
              class="filter-input"
            />
          </div>
          <div class="filter-item">
            <label class="filter-label">编程语言</label>
            <a-select
              v-model="searchParams.language"
              placeholder="全部语言"
              allow-clear
              class="filter-input"
            >
              <a-option value="java">Java</a-option>
              <a-option value="C++">C++</a-option>
              <a-option value="python">Python</a-option>
              <a-option value="go">Go</a-option>
              <a-option value="javascript">JavaScript</a-option>
            </a-select>
          </div>
          <div class="filter-item">
            <label class="filter-label">判题状态</label>
            <a-select
              v-model="searchParams.status"
              placeholder="全部状态"
              allow-clear
              class="filter-input"
            >
              <a-option value="0">
                <span class="status-dot pending"></span>等待中
              </a-option>
              <a-option value="1">
                <span class="status-dot running"></span>判题中
              </a-option>
              <a-option value="2">
                <span class="status-dot success"></span>成功
              </a-option>
              <a-option value="3">
                <span class="status-dot error"></span>失败
              </a-option>
            </a-select>
          </div>
          <div class="filter-actions">
            <a-button type="primary" @click="doSubmit" class="search-btn">
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
      <a-table
        :ref="tableRef"
        :columns="columns"
        :data="dataList"
        :loading="loading"
        :scroll="{ x: 1000 }"
        :pagination="{
          showTotal: true,
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          total: pageTotal,
          showJumper: true,
        }"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
        class="data-table"
        :bordered="false"
      >
        <template #questionTitle="{ record }">
          <div class="question-cell">
            <div v-if="record.questionVO && record.questionVO.title" class="question-title">
              {{ record.questionVO.title }}
            </div>
            <div v-else class="question-id">
              #{{ record.questionId }}
            </div>
          </div>
        </template>

        <template #submitter="{ record }">
          <div class="submitter-cell">
            <a-avatar :size="28" class="submitter-avatar">
              <img
                v-if="record.userVO?.userAvatar"
                :src="record.userVO.userAvatar"
                @error="handleAvatarError"
              />
              <span v-else>{{ (record.userVO?.userName || 'U').charAt(0) }}</span>
            </a-avatar>
            <span class="submitter-name">
              {{ record.userVO?.userName || `用户${record.userId}` }}
            </span>
          </div>
        </template>

        <template #language="{ record }">
          <span class="language-tag" :class="getLanguageClass(record.language)">
            {{ record.language }}
          </span>
        </template>

        <template #judgeInfo="{ record }">
          <div v-if="record.judgeInfo" class="judge-info">
            <div class="info-item" v-if="record.judgeInfo.time">
              <icon-clock-circle />
              <span>{{ record.judgeInfo.time }}ms</span>
            </div>
            <div class="info-item" v-if="record.judgeInfo.memory">
              <icon-storage />
              <span>{{ record.judgeInfo.memory }}KB</span>
            </div>
          </div>
          <span v-else class="no-info">-</span>
        </template>

        <template #status="{ record }">
          <div class="status-cell">
            <span class="status-badge" :class="getStatusClass(record.status)">
              <span class="status-indicator"></span>
              {{ formatStatus(record.status) }}
            </span>
          </div>
        </template>

        <template #createTime="{ record }">
          <div class="time-cell">
            <icon-clock-circle class="time-icon" />
            {{ moment(record.createTime).format("MM-DD HH:mm") }}
          </div>
        </template>

        <template #action="{ record }">
          <div class="action-cell">
            <a-button type="text" size="small" @click="viewDetail(record)" class="action-btn view">
              <icon-eye />
              查看
            </a-button>
            <a-button
              v-if="record.code"
              type="text"
              size="small"
              @click="viewCode(record)"
              class="action-btn code"
            >
              <icon-code />
              代码
            </a-button>
          </div>
        </template>
      </a-table>
    </div>

    <!-- 代码查看模态框 -->
    <a-modal
      v-model:visible="codeModalVisible"
      :footer="false"
      :mask-closable="true"
      width="800px"
      class="code-modal"
    >
      <template #title>
        <div class="modal-title">
          <icon-code />
          <span>代码详情</span>
        </div>
      </template>
      <div v-if="currentCodeRecord" class="code-modal-content">
        <div class="code-meta">
          <div class="meta-item">
            <span class="meta-label">提交ID</span>
            <span class="meta-value">{{ currentCodeRecord.id }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">编程语言</span>
            <span class="meta-value language" :class="getLanguageClass(currentCodeRecord.language)">
              {{ currentCodeRecord.language }}
            </span>
          </div>
          <div class="meta-item">
            <span class="meta-label">判题状态</span>
            <span class="meta-value">
              <span class="status-badge small" :class="getStatusClass(currentCodeRecord.status)">
                {{ formatStatus(currentCodeRecord.status) }}
              </span>
            </span>
          </div>
          <div class="meta-item">
            <span class="meta-label">提交时间</span>
            <span class="meta-value">{{ moment(currentCodeRecord.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
          </div>
        </div>
        <div class="code-section">
          <div class="code-header">
            <icon-code />
            <span>源代码</span>
          </div>
          <pre class="code-block"><code>{{ currentCodeRecord.code }}</code></pre>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import { Question, QuestionSubmitQueryRequest } from "../../../generated";
import { listQuestionSubmitByPage, getSubmitStats } from "@/api/questionSubmitController";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";
import {
  IconCode,
  IconRefresh,
  IconFilter,
  IconSearch,
  IconFile,
  IconCheckCircle,
  IconCloseCircle,
  IconTrophy,
  IconClockCircle,
  IconStorage,
  IconEye,
} from '@arco-design/web-vue/es/icon';

const tableRef = ref();
const loading = ref(false);

const dataList = ref([]);
const total = ref(0);
const pageTotal = ref(0);

// 代码查看模态框
const codeModalVisible = ref(false);
const currentCodeRecord = ref(null);
const searchParams = ref<QuestionSubmitQueryRequest>({
  questionId: undefined,
  language: undefined,
  status: undefined,
  pageSize: 10,
  current: 1,
});

// 统计数据（从后端获取，非当前页计算）
const successCount = ref(0);
const failCount = ref(0);
const successRate = ref(0);

const loadStats = async () => {
  try {
    const res = await getSubmitStats();
    if (res.data.code === 0) {
      const stats = res.data.data;
      total.value = stats.total;
      successCount.value = stats.successCount;
      failCount.value = stats.failCount;
      successRate.value = stats.successRate;
    }
  } catch (e) {
    console.error("加载统计数据失败", e);
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    };

    if (params.questionId && typeof params.questionId === 'string') {
      params.questionId = parseInt(params.questionId);
    }

    const res = await listQuestionSubmitByPage(params);
    if (res.data.code === 0) {
      dataList.value = res.data.data.records;
      pageTotal.value = res.data.data.total;
    } else {
      message.error("加载失败，" + res.data.message);
    }
  } catch (error) {
    console.error('加载数据错误:', error);
    message.error("网络错误，请稍后重试");
  } finally {
    loading.value = false;
  }
};

const refreshData = () => {
  loadData();
  loadStats();
  message.success("数据已刷新");
};

const viewDetail = (record: any) => {
  if (record.questionId) {
    router.push({
      path: `/view/question/${record.questionId}`,
    });
  } else {
    message.warning("无法获取题目信息");
  }
};

const viewCode = (record: any) => {
  if (record.code) {
    showCodeModal(record);
  } else {
    message.warning("暂无代码信息");
  }
};

const showCodeModal = (record: any) => {
  currentCodeRecord.value = record;
  codeModalVisible.value = true;
};

watchEffect(() => {
  loadData();
});

onMounted(() => {
  const originalError = console.error;
  console.error = (...args) => {
    if (args[0] && typeof args[0] === 'string' && args[0].includes('ResizeObserver')) {
      return;
    }
    originalError.apply(console, args);
  };
  loadData();
  loadStats();
});

const columns = [
  {
    title: "题目",
    slotName: "questionTitle",
    width: 200,
    ellipsis: true,
    tooltip: true,
  },
  {
    title: "提交者",
    slotName: "submitter",
    width: 140,
    align: "left",
  },
  {
    title: "语言",
    slotName: "language",
    width: 100,
    align: "center",
  },
  {
    title: "判题信息",
    slotName: "judgeInfo",
    width: 180,
  },
  {
    title: "状态",
    slotName: "status",
    width: 100,
    align: "center",
  },
  {
    title: "提交时间",
    slotName: "createTime",
    width: 130,
    align: "center",
  },
  {
    title: "操作",
    slotName: "action",
    width: 140,
    align: "center",
    fixed: "right",
  },
];

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const onPageSizeChange = (pageSize: number) => {
  searchParams.value = {
    ...searchParams.value,
    pageSize,
    current: 1,
  };
};

const router = useRouter();

const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

const doSubmit = () => {
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
};

const resetSearch = () => {
  searchParams.value = {
    questionId: undefined,
    language: undefined,
    status: undefined,
    pageSize: 10,
    current: 1,
  };
};

const formatStatus = (status: any) => {
  const statusNum = parseInt(status);
  switch (statusNum) {
    case 0: return "等待中";
    case 1: return "判题中";
    case 2: return "通过";
    case 3: return "失败";
    default: return "未知";
  }
};

const getStatusClass = (status: any) => {
  const statusNum = parseInt(status);
  switch (statusNum) {
    case 0: return "pending";
    case 1: return "running";
    case 2: return "success";
    case 3: return "error";
    default: return "unknown";
  }
};

const getLanguageClass = (language: string) => {
  const lang = (language || "").toLowerCase();
  switch (lang) {
    case 'java': return 'lang-java';
    case 'c++': case 'cpp': return 'lang-cpp';
    case 'python': return 'lang-python';
    case 'go': return 'lang-go';
    case 'javascript': case 'js': return 'lang-js';
    default: return 'lang-other';
  }
};

const handleAvatarError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  target.style.display = "none";
};
</script>

<style scoped>
#questionSubmitView {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-6);
  background-color: var(--bg-page);
  min-height: 100vh;
  font-family: var(--font-body);
  animation: fadeIn 0.5s var(--ease-out);
  position: relative;
}

/* 背景纹理 */
#questionSubmitView::before {
  content: '';
  position: fixed;
  inset: 0;
  background:
    radial-gradient(at 30% 20%, rgba(59, 130, 246, 0.04) 0px, transparent 50%),
    radial-gradient(at 70% 60%, rgba(6, 182, 212, 0.03) 0px, transparent 50%);
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
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-accent-500) 100%);
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

.refresh-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
  transition: all var(--duration-fast);
}

.refresh-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.refresh-btn:active {
  transform: translateY(0) scale(0.98);
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
  margin-bottom: var(--space-6);
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

.stat-card:nth-child(1) { animation-delay: calc(var(--stagger-delay) * 2); }
.stat-card:nth-child(2) { animation-delay: calc(var(--stagger-delay) * 3); }
.stat-card:nth-child(3) { animation-delay: calc(var(--stagger-delay) * 4); }
.stat-card:nth-child(4) { animation-delay: calc(var(--stagger-delay) * 5); }

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

.stat-icon.success {
  background: var(--color-success-bg);
  color: var(--color-success);
}

.stat-icon.error {
  background: var(--color-error-bg);
  color: var(--color-error);
}

.stat-icon.rate {
  background: var(--color-warning-bg);
  color: var(--color-warning);
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
  animation: fadeInUp 0.6s var(--ease-out) calc(var(--stagger-delay) * 6) backwards;
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
  min-width: 180px;
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
  animation: fadeInUp 0.6s var(--ease-out) calc(var(--stagger-delay) * 7) backwards;
}

.table-section :deep(.arco-table-container) {
  overflow-x: auto;
}

.data-table {
  border-radius: var(--radius-xl);
}

/* 表格样式 */
:deep(.arco-table-th) {
  background: var(--bg-subtle) !important;
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--text-sm);
}

:deep(.arco-table-td) {
  vertical-align: middle;
  padding: var(--space-3) var(--space-4);
}

:deep(.arco-table-tr:hover .arco-table-td) {
  background: var(--color-primary-50) !important;
}

/* 题目单元格 */
.question-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.question-title {
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.question-id {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* 提交者单元格 */
.submitter-cell {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.submitter-avatar {
  background: var(--color-primary-500);
  color: white;
  font-size: var(--text-xs);
  font-weight: 600;
  flex-shrink: 0;
}

.submitter-name {
  font-weight: 500;
  color: var(--text-primary);
  font-size: var(--text-sm);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 语言标签 */
.language-tag {
  display: inline-block;
  padding: 4px var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: 600;
}

.language-tag.lang-java { background: #fef3c7; color: #92400e; }
.language-tag.lang-cpp { background: #dbeafe; color: #1e40af; }
.language-tag.lang-python { background: #d1fae5; color: #065f46; }
.language-tag.lang-go { background: #e0f2fe; color: #0369a1; }
.language-tag.lang-js { background: #fef9c3; color: #854d0e; }
.language-tag.lang-other { background: var(--bg-subtle); color: var(--text-secondary); }

/* 判题信息 */
.judge-info {
  display: flex;
  gap: var(--space-4);
}

.info-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.no-info {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* 状态单元格 */
.status-cell {
  display: flex;
  justify-content: center;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: 4px var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: 600;
}

.status-indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-badge.pending {
  background: var(--color-warning-bg);
  color: var(--color-warning-text);
}

.status-badge.pending .status-indicator {
  background: var(--color-warning);
}

.status-badge.running {
  background: var(--color-info-bg);
  color: var(--color-info-text);
}

.status-badge.running .status-indicator {
  background: var(--color-info);
  animation: pulse 1.5s infinite;
}

.status-badge.success {
  background: var(--color-success-bg);
  color: var(--color-success-text);
}

.status-badge.success .status-indicator {
  background: var(--color-success);
}

.status-badge.error {
  background: var(--color-error-bg);
  color: var(--color-error-text);
}

.status-badge.error .status-indicator {
  background: var(--color-error);
}

.status-badge.unknown {
  background: var(--bg-subtle);
  color: var(--text-secondary);
}

.status-badge.unknown .status-indicator {
  background: var(--text-tertiary);
}

.status-badge.small {
  padding: 2px var(--space-2);
  font-size: 10px;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 时间单元格 */
.time-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.time-icon {
  font-size: var(--text-xs);
}

/* 操作单元格 */
.action-cell {
  display: flex;
  justify-content: center;
  gap: var(--space-1);
}

.action-btn {
  border-radius: var(--radius-md) !important;
  font-weight: 500 !important;
  font-size: var(--text-xs) !important;
  padding: 4px var(--space-2) !important;
}

.action-btn.view {
  color: var(--color-primary-500);
}

.action-btn.view:hover {
  background: var(--color-primary-50);
}

.action-btn.code {
  color: var(--color-accent-500);
}

.action-btn.code:hover {
  background: #ecfeff;
}

/* 分页 */
:deep(.arco-pagination) {
  padding: var(--space-4);
  justify-content: center;
  border-top: 1px solid var(--border-default);
}

/* 代码模态框 */
.code-modal-content {
  padding: var(--space-4);
}

.code-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-4);
  margin-bottom: var(--space-5);
  padding: var(--space-4);
  background: var(--bg-subtle);
  border-radius: var(--radius-lg);
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.meta-label {
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.meta-value {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.meta-value.language {
  display: inline-block;
  padding: 2px var(--space-2);
  border-radius: var(--radius-sm);
}

.code-section {
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.code-header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  background: #1e293b;
  color: #94a3b8;
  font-weight: 600;
  font-size: var(--text-sm);
}

.code-block {
  margin: 0;
  padding: var(--space-4);
  background: #0f172a;
  color: #e2e8f0;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: var(--text-sm);
  line-height: 1.6;
  overflow-x: auto;
  white-space: pre;
  max-height: 400px;
  overflow-y: auto;
}

/* 下拉菜单中的状态点 */
.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: var(--space-2);
}

.status-dot.pending { background: var(--color-warning); }
.status-dot.running { background: var(--color-info); }
.status-dot.success { background: var(--color-success); }
.status-dot.error { background: var(--color-error); }

/* 响应式设计 */
@media (max-width: 1000px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-content {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-item {
    min-width: 100%;
  }

  .filter-actions {
    margin-left: 0;
    justify-content: flex-end;
  }
}

@media (max-width: 600px) {
  #questionSubmitView {
    padding: var(--space-4);
  }

  .header-content {
    flex-direction: column;
    gap: var(--space-4);
    text-align: center;
  }

  .title-section {
    flex-direction: column;
  }

  .stats-row {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: var(--space-4);
  }

  .code-meta {
    grid-template-columns: 1fr;
  }
}
</style>
