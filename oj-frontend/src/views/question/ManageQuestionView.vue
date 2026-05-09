<template>
  <div id="manageQuestionView">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <div class="title-icon-wrapper">
            <icon-settings />
          </div>
          <div class="title-text">
            <h1 class="page-title">题目管理</h1>
            <p class="page-subtitle">管理和维护所有题目信息</p>
          </div>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="createQuestion" class="create-btn">
            <template #icon>
              <icon-plus />
            </template>
            创建题目
          </a-button>
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
            <label class="filter-label">题目标题</label>
            <a-input
              v-model="searchParams.title"
              placeholder="搜索题目标题..."
              allow-clear
              class="filter-input"
            >
              <template #prefix>
                <icon-search />
              </template>
            </a-input>
          </div>
          <div class="filter-item">
            <label class="filter-label">难度</label>
            <a-select
              v-model="searchParams.difficulty"
              placeholder="选择难度"
              allow-clear
              class="filter-input"
            >
              <a-option value="简单">
                <span class="difficulty-dot easy"></span>简单
              </a-option>
              <a-option value="中等">
                <span class="difficulty-dot medium"></span>中等
              </a-option>
              <a-option value="困难">
                <span class="difficulty-dot hard"></span>困难
              </a-option>
            </a-select>
          </div>
          <div class="filter-item">
            <label class="filter-label">标签</label>
            <a-select
              v-model="searchParams.tags"
              placeholder="选择标签"
              multiple
              allow-clear
              class="filter-input"
            >
              <a-option value="数组">数组</a-option>
              <a-option value="字符串">字符串</a-option>
              <a-option value="链表">链表</a-option>
              <a-option value="树">树</a-option>
              <a-option value="图">图</a-option>
              <a-option value="动态规划">动态规划</a-option>
              <a-option value="贪心">贪心</a-option>
              <a-option value="回溯">回溯</a-option>
              <a-option value="二分查找">二分查找</a-option>
              <a-option value="哈希表">哈希表</a-option>
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
          <span>题目列表</span>
          <span class="total-badge">共 {{ total }} 条</span>
        </div>
        <div class="table-actions">
          <a-button @click="refreshData" class="refresh-btn">
            <icon-refresh />
            刷新
          </a-button>
          <a-button @click="exportData" class="export-btn">
            <icon-download />
            导出
          </a-button>
        </div>
      </div>

      <a-table
        :ref="tableRef"
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
        <!-- 题目标题 -->
        <template #title="{ record }">
          <div class="title-cell">
            <span class="question-title">{{ record.title }}</span>
          </div>
        </template>

        <!-- 难度 -->
        <template #difficulty="{ record }">
          <span class="difficulty-badge" :class="getDifficultyClass(record.difficulty)">
            {{ record.difficulty || '未知' }}
          </span>
        </template>

        <!-- 标签 -->
        <template #tags="{ record }">
          <div class="tags-cell">
            <span
              v-for="tag in parseTags(record.tags)"
              :key="tag"
              class="tag-badge"
            >
              {{ tag }}
            </span>
          </div>
        </template>

        <!-- 统计数据 -->
        <template #stats="{ record }">
          <div class="stats-cell">
            <div class="stat-item submit">
              <icon-clock-circle />
              <span>{{ record.submitNum || 0 }}</span>
            </div>
            <div class="stat-item accept">
              <icon-check-circle />
              <span>{{ record.acceptedNum || 0 }}</span>
            </div>
          </div>
        </template>

        <!-- 判题配置 -->
        <template #judgeConfig="{ record }">
          <div class="config-cell">
            <div v-for="(value, key) in formatJudgeConfig(record.judgeConfig)" :key="key" class="config-item">
              <span class="config-key">{{ key }}</span>
              <span class="config-val">{{ value }}</span>
            </div>
          </div>
        </template>

        <!-- 测试用例 -->
        <template #judgeCase="{ record }">
          <div class="testcase-cell">
            <div class="case-count">
              <icon-experiment />
              <span>{{ parseTestCases(record.judgeCase).length }} 个</span>
            </div>
            <a-button type="text" size="small" @click="viewTestCases(record)" class="view-btn">
              查看
            </a-button>
          </div>
        </template>

        <!-- 创建时间 -->
        <template #createTime="{ record }">
          <div class="time-cell">
            <icon-clock-circle class="time-icon" />
            {{ moment(record.createTime).format("MM-DD HH:mm") }}
          </div>
        </template>

        <!-- 操作 -->
        <template #action="{ record }">
          <div class="action-cell">
            <a-tooltip content="编辑">
              <a-button type="primary" size="small" @click="doUpdate(record)" class="edit-btn">
                <icon-edit />
              </a-button>
            </a-tooltip>
            <a-tooltip content="查看">
              <a-button type="outline" size="small" @click="viewDetail(record)" class="view-action-btn">
                <icon-eye />
              </a-button>
            </a-tooltip>
            <a-popconfirm
              content="确定要删除这个题目吗？"
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

    <!-- 详情模态框 -->
    <a-modal v-model:visible="detailVisible" :footer="false" width="800px" class="detail-modal">
      <template #title>
        <div class="modal-title">
          <icon-file />
          <span>题目详情</span>
        </div>
      </template>
      <div v-if="currentRecord" class="detail-content">
        <div class="detail-meta">
          <div class="meta-item">
            <span class="meta-label">题目标题</span>
            <span class="meta-value">{{ currentRecord.title }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">难度等级</span>
            <span class="meta-value">
              <span class="difficulty-badge" :class="getDifficultyClass(currentRecord.difficulty)">
                {{ currentRecord.difficulty || '未知' }}
              </span>
            </span>
          </div>
          <div class="meta-item">
            <span class="meta-label">提交数</span>
            <span class="meta-value">{{ currentRecord.submitNum || 0 }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">通过数</span>
            <span class="meta-value">{{ currentRecord.acceptedNum || 0 }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">创建时间</span>
            <span class="meta-value">{{ moment(currentRecord.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
          </div>
        </div>
        <div class="detail-section">
          <h4 class="section-title">题目内容</h4>
          <div class="section-content">{{ currentRecord.content || '暂无内容' }}</div>
        </div>
        <div class="detail-section">
          <h4 class="section-title">题目答案</h4>
          <div class="section-content">{{ currentRecord.answer || '暂无答案' }}</div>
        </div>
      </div>
    </a-modal>

    <!-- 测试用例模态框 -->
    <a-modal v-model:visible="testCaseVisible" :footer="false" width="600px" class="testcase-modal">
      <template #title>
        <div class="modal-title">
          <icon-experiment />
          <span>测试用例</span>
        </div>
      </template>
      <div v-if="currentRecord" class="testcase-content">
        <div v-for="(testCase, index) in parseTestCases(currentRecord.judgeCase)" :key="index" class="testcase-item">
          <div class="case-header">
            <span class="case-badge">用例 {{ index + 1 }}</span>
          </div>
          <div class="case-body">
            <div class="case-input">
              <label>输入</label>
              <pre>{{ testCase.input }}</pre>
            </div>
            <div class="case-output">
              <label>期望输出</label>
              <pre>{{ testCase.output }}</pre>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Page_Question_,
  Question,
  QuestionControllerService,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";
import {
  IconSettings,
  IconPlus,
  IconClockCircle,
  IconFilter,
  IconSearch,
  IconRefresh,
  IconDownload,
  IconList,
  IconEye,
  IconEdit,
  IconDelete,
  IconExperiment,
} from '@arco-design/web-vue/es/icon';

const tableRef = ref();
const loading = ref(false);

const detailVisible = ref(false);
const testCaseVisible = ref(false);
const currentRecord = ref(null);

const dataList = ref([]);
const total = ref(0);

const searchParams = ref({
  pageSize: 10,
  current: 1,
  title: '',
  difficulty: '',
  tags: [] as string[],
});

const loadData = async () => {
  loading.value = true;
  try {
    const params = { ...searchParams.value };

    if (params.title === '') params.title = undefined;
    if (params.difficulty === '') params.difficulty = undefined;
    if (params.tags.length === 0) params.tags = undefined;

    const res = await QuestionControllerService.listQuestionByPageUsingPost(params);
    if (res.code === 0) {
      dataList.value = res.data.records;
      total.value = Number(res.data.total);
    } else {
      message.error("加载失败，" + res.message);
    }
  } catch (error) {
    console.error("loadData error:", error);
    message.error("网络错误，请稍后重试");
  } finally {
    loading.value = false;
  }
};

watchEffect(() => {
  if (searchParams.value.current || searchParams.value.pageSize || searchParams.value.title || searchParams.value.difficulty || searchParams.value.tags) {
    loadData();
  }
});

onMounted(() => {
  const originalError = console.error;
  console.error = (...args) => {
    if (args[0] && typeof args[0] === 'string' && args[0].includes('ResizeObserver')) return;
    originalError.apply(console, args);
  };
  loadData();
});

const parseTags = (tags: any) => {
  if (!tags) return [];
  if (Array.isArray(tags)) return tags;
  if (typeof tags === 'string') {
    try {
      const parsed = JSON.parse(tags);
      if (Array.isArray(parsed)) return parsed;
      return tags.split(',').map(tag => tag.trim()).filter(tag => tag);
    } catch (e) {
      try {
        return tags.split(',').map(tag => tag.trim()).filter(tag => tag);
      } catch (e2) {
        return [];
      }
    }
  }
  return [];
};

const getDifficultyClass = (difficulty: string) => {
  switch (difficulty) {
    case '简单': return 'easy';
    case '中等': return 'medium';
    case '困难': return 'hard';
    default: return 'unknown';
  }
};

const columns = [
  { title: "题目标题", slotName: "title", ellipsis: true, tooltip: true },
  { title: "难度", slotName: "difficulty", align: "center" },
  { title: "标签", slotName: "tags" },
  { title: "统计", slotName: "stats", align: "center" },
  { title: "判题配置", slotName: "judgeConfig" },
  { title: "测试用例", slotName: "judgeCase", align: "center" },
  { title: "创建时间", slotName: "createTime", align: "center" },
  { title: "操作", slotName: "action", align: "center" },
];

const onPageChange = (page: number) => {
  searchParams.value = { ...searchParams.value, current: page };
};

const onPageSizeChange = (pageSize: number) => {
  searchParams.value = { ...searchParams.value, pageSize, current: 1 };
};

const doSearch = () => {
  searchParams.value = { ...searchParams.value, current: 1 };
};

const resetSearch = () => {
  searchParams.value = {
    ...searchParams.value,
    title: '',
    difficulty: '',
    tags: [],
    current: 1,
  };
};

const doDelete = async (question: Question) => {
  try {
    const res = await QuestionControllerService.deleteQuestionUsingDelete(question.id!);
    if (res.code === 0) {
      message.success("删除成功");
      loadData();
    } else {
      message.error("删除失败，" + res.message);
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  }
};

const router = useRouter();

const doUpdate = (question: Question) => {
  router.push({ path: "/update/question", query: { id: question.id } });
};

const createQuestion = () => {
  router.push("/add/question");
};

const refreshData = () => {
  loadData();
  message.success("数据已刷新");
};

const exportData = () => {
  message.info("导出功能开发中...");
};

const viewDetail = (record: any) => {
  currentRecord.value = record;
  detailVisible.value = true;
};

const viewTestCases = (record: any) => {
  currentRecord.value = record;
  testCaseVisible.value = true;
};

const formatJudgeConfig = (judgeConfig: any) => {
  if (!judgeConfig) return {};
  try {
    const config = typeof judgeConfig === 'string' ? JSON.parse(judgeConfig) : judgeConfig;
    if (!config.timeLimit && !config.memoryLimit) return {};
    return {
      "时间": `${config.timeLimit || 0}ms`,
      "内存": `${config.memoryLimit || 0}KB`,
    };
  } catch (e) {
    return {};
  }
};

const parseTestCases = (judgeCase: any) => {
  if (!judgeCase) return [];
  try {
    if (Array.isArray(judgeCase)) return judgeCase;
    return JSON.parse(judgeCase);
  } catch (e) {
    return [];
  }
};
</script>

<style scoped>
#manageQuestionView {
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
#manageQuestionView::before {
  content: '';
  position: fixed;
  inset: 0;
  background:
    radial-gradient(at 25% 15%, rgba(59, 130, 246, 0.04) 0px, transparent 50%),
    radial-gradient(at 75% 50%, rgba(6, 182, 212, 0.03) 0px, transparent 50%),
    radial-gradient(at 50% 85%, rgba(139, 92, 246, 0.03) 0px, transparent 50%);
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

.create-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.39);
  transition: all var(--duration-fast);
}

.create-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.5);
}

.create-btn:active {
  transform: translateY(0) scale(0.98);
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

.difficulty-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: var(--space-2);
}

.difficulty-dot.easy { background: var(--color-success); }
.difficulty-dot.medium { background: var(--color-warning); }
.difficulty-dot.hard { background: var(--color-error); }

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

.table-section :deep(.arco-table) {
  border-radius: 0;
}

.table-section :deep(.arco-table-container) {
  overflow-x: auto;
}

.table-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-5);
  background: var(--bg-subtle);
  border-bottom: 1px solid var(--border-default);
}

.table-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--text-primary);
}

.total-badge {
  padding: 4px var(--space-3);
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 600;
}

.table-actions {
  display: flex;
  gap: var(--space-2);
}

.refresh-btn,
.export-btn {
  border-radius: var(--radius-md);
  font-weight: 500;
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

/* 单元格样式 */
.title-cell {
  display: flex;
  flex-direction: column;
}

.question-title {
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.difficulty-badge {
  display: inline-block;
  padding: 4px var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: 600;
}

.difficulty-badge.easy { background: var(--color-success-bg); color: var(--color-success-text); }
.difficulty-badge.medium { background: var(--color-warning-bg); color: var(--color-warning-text); }
.difficulty-badge.hard { background: var(--color-error-bg); color: var(--color-error-text); }
.difficulty-badge.unknown { background: var(--bg-subtle); color: var(--text-secondary); }

.tags-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag-badge {
  display: inline-block;
  padding: 2px var(--space-2);
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: 500;
  max-width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.preview-cell {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.preview-text {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  line-height: 1.4;
}

.view-btn {
  color: var(--color-primary-500);
  font-size: var(--text-xs);
  padding: 2px var(--space-2);
}

.view-btn:hover {
  background: var(--color-primary-50);
}

.stats-cell {
  display: flex;
  gap: var(--space-3);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
}

.stat-item.submit { color: var(--text-secondary); }
.stat-item.accept { color: var(--color-success); }

.config-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.config-item {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-xs);
}

.config-key { color: var(--text-tertiary); }
.config-val { color: var(--text-primary); font-weight: 500; }

.testcase-cell {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.case-count {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.time-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
  color: var(--text-secondary);
  white-space: nowrap;
}

.time-icon {
  font-size: var(--text-xs);
}

/* 操作按钮 */
.action-cell {
  display: flex;
  gap: var(--space-2);
  justify-content: center;
  flex-wrap: nowrap;
}

.edit-btn,
.view-action-btn,
.delete-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
  font-size: var(--text-xs);
  min-width: 28px;
  padding: 0 6px;
}

/* 分页 */
:deep(.arco-pagination) {
  padding: var(--space-4);
  justify-content: center;
  border-top: 1px solid var(--border-default);
}

/* 模态框 */
.detail-modal,
.testcase-modal {
  border-radius: var(--radius-xl);
}

.modal-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-weight: 600;
}

.modal-title .arco-icon {
  color: var(--color-primary-500);
}

.detail-content {
  padding: var(--space-4);
}

.detail-meta {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-4);
  padding: var(--space-4);
  background: var(--bg-subtle);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-5);
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

.detail-section {
  margin-bottom: var(--space-5);
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-3);
}

.section-content {
  padding: var(--space-4);
  background: var(--bg-subtle);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.6;
}

.testcase-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.testcase-item {
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.case-header {
  padding: var(--space-3) var(--space-4);
  background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
}

.case-badge {
  font-size: var(--text-sm);
  font-weight: 600;
  color: white;
}

.case-body {
  padding: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.case-input,
.case-output {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.case-input label,
.case-output label {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  font-weight: 600;
}

.case-input pre,
.case-output pre {
  background: #0f172a;
  color: #e2e8f0;
  padding: var(--space-3);
  border-radius: var(--radius-md);
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: var(--text-xs);
  margin: 0;
  white-space: pre-wrap;
}

/* 响应式设计 */
@media (max-width: 1000px) {
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

  .detail-meta {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  #manageQuestionView {
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

  .table-header-bar {
    flex-direction: column;
    gap: var(--space-3);
  }

  .detail-meta {
    grid-template-columns: 1fr;
  }

  .action-cell {
    flex-direction: column;
  }
}
</style>