<template>
  <div id="manageQuestionView">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <a-icon type="setting" class="title-icon" />
        题目管理
      </h2>
      <p class="page-description">管理和维护所有题目信息</p>
    </div>

    <!-- 搜索和操作栏 -->
    <a-card class="action-card" :bordered="false">
      <div class="action-header">
        <div class="search-section">
          <a-form :model="searchParams" layout="inline" class="search-form">
            <a-form-item field="title" label="题目标题">
          <a-input 
                v-model="searchParams.title" 
            placeholder="搜索题目标题..." 
            allow-clear
            class="search-input"
          >
            <template #prefix>
              <a-icon type="search" />
            </template>
          </a-input>
            </a-form-item>
            <a-form-item field="difficulty" label="难度">
              <a-select 
                v-model="searchParams.difficulty" 
                placeholder="选择难度" 
                allow-clear
                class="search-input"
              >
                <a-option value="简单">简单</a-option>
                <a-option value="中等">中等</a-option>
                <a-option value="困难">困难</a-option>
              </a-select>
            </a-form-item>
            <a-form-item field="tags" label="标签">
              <a-select 
                v-model="searchParams.tags" 
                placeholder="选择标签" 
                multiple
                allow-clear
                class="search-input"
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
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="doSearch" class="search-btn">
                  <template #icon>
                    <a-icon type="search" />
                  </template>
                  搜索
                </a-button>
                <a-button @click="resetSearch" class="reset-btn">
                  <template #icon>
                    <a-icon type="reload" />
                  </template>
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </div>
        <div class="action-buttons">
          <a-space>
            <a-button type="primary" @click="createQuestion">
              <template #icon>
                <a-icon type="plus" />
              </template>
              创建题目
            </a-button>
            <a-button @click="refreshData">
              <template #icon>
                <a-icon type="reload" />
              </template>
              刷新
            </a-button>
            <a-button @click="exportData">
              <template #icon>
                <a-icon type="download" />
              </template>
              导出
            </a-button>
          </a-space>
        </div>
      </div>
    </a-card>

    <!-- 统计信息 -->
    <a-row :gutter="16" class="stats-row">
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <a-icon type="file-text" />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ total }}</div>
              <div class="stat-label">总题目数</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <a-icon type="check-circle" />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalSubmissions }}</div>
              <div class="stat-label">总提交数</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <a-icon type="trophy" />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalAccepted }}</div>
              <div class="stat-label">总通过数</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <a-icon type="user" />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 题目列表 -->
    <a-card class="table-card" :bordered="false">
      <div class="table-header">
        <div class="table-title">
          <a-icon type="table" class="table-icon" />
          题目列表
        </div>
        <div class="table-actions">
          <a-select v-model="searchParams.pageSize" size="small" @change="onPageSizeChange">
            <a-option :value="10">10条/页</a-option>
            <a-option :value="20">20条/页</a-option>
            <a-option :value="50">50条/页</a-option>
          </a-select>
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
          showSizeChanger: true,
        }" 
        @page-change="onPageChange"
        class="data-table"
        row-key="id"
      >
        <!-- 自定义内容列 -->
        <template #content="{ record }">
          <div class="content-cell">
            <div class="content-preview">{{ getContentPreview(record.content) }}</div>
            <a-button type="text" size="small" @click="viewContent(record)">
              查看详情
            </a-button>
          </div>
        </template>
        
        <!-- 自定义答案列 -->
        <template #answer="{ record }">
          <div class="answer-cell">
            <div class="answer-preview">{{ getAnswerPreview(record.answer) }}</div>
            <a-button type="text" size="small" @click="viewAnswer(record)">
              查看答案
            </a-button>
          </div>
        </template>
        
        <!-- 自定义标签列 -->
        <template #tags="{ record }">
          <div class="tags-cell">
            <span 
              v-for="tag in parseTags(record.tags)" 
              :key="tag" 
              class="tag"
              :class="getTagClass(tag)"
            >
              {{ tag }}
            </span>
          </div>
        </template>
        
        <template #difficulty="{ record }">
          <a-tag :color="getDifficultyColor(record.difficulty)">
            {{ record.difficulty || '未知' }}
          </a-tag>
        </template>
        
        <template #createTime="{ record }">
          <div class="time-cell">
            {{ moment(record.createTime).format("YYYY-MM-DD HH:mm") }}
          </div>
        </template>
        
        <!-- 自定义判题配置列 -->
        <template #judgeConfig="{ record }">
          <div class="judge-config">
            <div v-for="(value, key) in formatJudgeConfig(record.judgeConfig)" :key="key" class="config-item">
              <span class="config-label">{{ key }}:</span>
              <span class="config-value">{{ value }}</span>
            </div>
          </div>
        </template>
        
        <!-- 自定义判题用例列 -->
        <template #judgeCase="{ record }">
          <div class="test-case">
            <div class="case-count">
              {{ parseTestCases(record.judgeCase).length }} 个用例
            </div>
            <a-button type="text" size="small" @click="viewTestCases(record)">
              查看用例
            </a-button>
          </div>
        </template>
        
        <template #action="{ record }">
          <a-space>
            <a-button type="primary" size="small" @click="doUpdate(record)">
              <template #icon>
                <a-icon type="edit" />
              </template>
              编辑
            </a-button>
            <a-button type="text" size="small" @click="viewDetail(record)">
              <template #icon>
                <a-icon type="eye" />
              </template>
              查看
            </a-button>
            <a-popconfirm
              content="确定要删除这个题目吗？"
              @ok="doDelete(record)"
            >
              <a-button status="danger" size="small">
                <template #icon>
                  <a-icon type="delete" />
                </template>
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-card>

    <!-- 详情模态框 -->
    <a-modal v-model:visible="detailVisible" title="题目详情" width="800px" :footer="false">
      <div v-if="currentRecord">
        <a-descriptions :data="detailData" layout="vertical" bordered />
      </div>
    </a-modal>

    <!-- 测试用例模态框 -->
    <a-modal v-model:visible="testCaseVisible" title="测试用例" width="600px" :footer="false">
      <div v-if="currentRecord">
        <div v-for="(testCase, index) in parseTestCases(currentRecord.judgeCase)" :key="index" class="test-case-modal">
          <h4>测试用例 {{ index + 1 }}</h4>
          <div class="case-content">
            <div class="case-input">
              <strong>输入:</strong>
              <pre>{{ testCase.input }}</pre>
            </div>
            <div class="case-output">
              <strong>期望输出:</strong>
              <pre>{{ testCase.output }}</pre>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect, computed } from "vue";
import {
  Page_Question_,
  Question,
  QuestionControllerService,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import * as querystring from "querystring";
import { useRouter } from "vue-router";
import moment from "moment";

const tableRef = ref();
const loading = ref(false);
const searchKeyword = ref('');
const pageSize = ref(10);

// 模态框状态
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

// 统计数据
const totalSubmissions = ref(0);
const totalAccepted = ref(0);
const activeUsers = ref(0);

const loadData = async () => {
  loading.value = true;
  try {
    // 处理搜索参数，过滤空值
    const params = {
      ...searchParams.value,
    };
    
    // 过滤空字符串
    if (params.title === '') {
      params.title = undefined;
    }
    if (params.difficulty === '') {
      params.difficulty = undefined;
    }
    if (params.tags.length === 0) {
      params.tags = undefined;
    }
    
    const res = await QuestionControllerService.listQuestionVoByPageUsingPost(params);
    if (res.code === 0) {
      dataList.value = res.data.records;
      total.value = res.data.total;
      

      
      // 计算统计数据
      calculateStats();
    } else {
      message.error("加载失败，" + res.message);
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  } finally {
    loading.value = false;
  }
};

/**
 * 计算统计数据
 */
const calculateStats = () => {
  totalSubmissions.value = dataList.value.reduce((sum, item) => sum + (item.submitNum || 0), 0);
  totalAccepted.value = dataList.value.reduce((sum, item) => sum + (item.acceptedNum || 0), 0);
  activeUsers.value = Math.floor(Math.random() * 50) + 20; // 模拟数据
};

/**
 * 监听 searchParams 变量，改变时触发页面的重新加载
 */
watchEffect(() => {
  // 只有当searchParams有实际变化时才重新加载
  if (searchParams.value.current || searchParams.value.pageSize || searchParams.value.title || searchParams.value.difficulty || searchParams.value.tags) {
  loadData();
  }
});

/**
 * 页面加载时，请求数据
 */
onMounted(() => {
  // 添加ResizeObserver错误处理
  const originalError = console.error;
  console.error = (...args) => {
    if (args[0] && typeof args[0] === 'string' && args[0].includes('ResizeObserver')) {
      return; // 忽略ResizeObserver错误
    }
    originalError.apply(console, args);
  };
  
  loadData();
});

// 解析标签
const parseTags = (tags: any) => {
  if (!tags) return [];
  
  // 如果已经是数组，直接返回
  if (Array.isArray(tags)) {
    return tags;
  }
  
  // 如果是字符串，尝试解析
  if (typeof tags === 'string') {
    try {
      // 尝试解析JSON格式
      const parsed = JSON.parse(tags);
      if (Array.isArray(parsed)) {
        return parsed;
      }
      // 如果不是数组，尝试按逗号分割
      return tags.split(',').map(tag => tag.trim()).filter(tag => tag);
    } catch (e) {
      // JSON解析失败，尝试按逗号分割
      try {
        return tags.split(',').map(tag => tag.trim()).filter(tag => tag);
      } catch (e2) {
        console.error('标签解析失败:', tags, e2);
        return [];
      }
    }
  }
  
  // 其他类型，返回空数组
  return [];
};

// 获取难度颜色
const getDifficultyColor = (difficulty: string) => {
  switch (difficulty) {
    case '简单':
      return 'green';
    case '中等':
      return 'orange';
    case '困难':
      return 'red';
    default:
      return 'gray';
  }
};

// 获取内容预览
const getContentPreview = (content: string) => {
  if (!content) return '暂无内容';
  return content.length > 50 ? content.substring(0, 50) + '...' : content;
};

// 获取答案预览
const getAnswerPreview = (answer: string) => {
  if (!answer) return '暂无答案';
  return answer.length > 50 ? answer.substring(0, 50) + '...' : answer;
};

const columns = [
  {
    title: "题目标题",
    dataIndex: "title",
    width: 200,
    ellipsis: true,
    tooltip: true,
  },
  {
    title: "难度",
    slotName: "difficulty",
    width: 100,
    align: "center",
  },
  {
    title: "标签",
    slotName: "tags",
    width: 150,
  },
  {
    title: "内容",
    slotName: "content",
    width: 200,
  },
  {
    title: "答案",
    slotName: "answer",
    width: 200,
  },
  {
    title: "提交数",
    dataIndex: "submitNum",
    width: 100,
    align: "center",
  },
  {
    title: "通过数",
    dataIndex: "acceptedNum",
    width: 100,
    align: "center",
  },
  {
    title: "判题配置",
    slotName: "judgeConfig",
    width: 150,
  },
  {
    title: "测试用例",
    slotName: "judgeCase",
    width: 120,
    align: "center",
  },
  {
    title: "创建时间",
    slotName: "createTime",
    width: 150,
    align: "center",
  },
  {
    title: "操作",
    slotName: "action",
    width: 200,
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

const onPageSizeChange = (size: number) => {
  searchParams.value = {
    ...searchParams.value,
    pageSize: size,
    current: 1,
  };
};

const doSearch = () => {
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
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
    const res = await QuestionControllerService.deleteQuestionUsingPost({
      id: question.id,
    });
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
  router.push({
    path: "/update/question",
    query: {
      id: question.id,
    },
  });
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

const viewContent = (record: any) => {
  currentRecord.value = record;
  detailVisible.value = true;
};

const viewAnswer = (record: any) => {
  currentRecord.value = record;
  detailVisible.value = true;
};

const viewTestCases = (record: any) => {
  currentRecord.value = record;
  testCaseVisible.value = true;
};

// 详情数据
const detailData = computed(() => {
  if (!currentRecord.value) return [];
  
  return [
    {
      label: '题目标题',
      value: currentRecord.value.title,
    },
    {
      label: '难度等级',
      value: currentRecord.value.difficulty || '未知',
    },
    {
      label: '题目内容',
      value: currentRecord.value.content || '暂无内容',
    },
    {
      label: '题目答案',
      value: currentRecord.value.answer || '暂无答案',
    },
    {
      label: '提交数',
      value: currentRecord.value.submitNum || 0,
    },
    {
      label: '通过数',
      value: currentRecord.value.acceptedNum || 0,
    },
    {
      label: '创建时间',
      value: moment(currentRecord.value.createTime).format("YYYY-MM-DD HH:mm:ss"),
    },
  ];
});

// 将 judgeConfig 字符串解析为对象并格式化为特定的显示格式
const formatJudgeConfig = (judgeConfig: string) => {
  if (!judgeConfig) return {};
  try {
    const config = JSON.parse(judgeConfig);
    return {
      "时间限制": `${config.timeLimit} ms`,
      "内存限制": `${config.memoryLimit} KB`,
      "堆栈限制": `${config.stackLimit} KB`,
    };
  } catch (e) {
    return {};
  }
};

// 解析判题用例的 JSON 字符串
const parseTestCases = (judgeCase: string) => {
  if (!judgeCase) return [];
  try {
    return JSON.parse(judgeCase);
  } catch (e) {
    return [];
  }
};

// 获取标签样式类
const getTagClass = (tag: string) => {
  const tagClasses = ['algorithm', 'data-structure', 'math', 'string'];
  return tagClasses[Math.floor(Math.random() * tagClasses.length)];
};
</script>

<style scoped>
#manageQuestionView {
  max-width: 1280px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 页面标题样式 */
.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-icon {
  margin-right: 12px;
  color: #165dff;
}

.page-description {
  color: #86909c;
  font-size: 14px;
  margin: 0;
}

/* 操作卡片样式 */
.action-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.action-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-input {
  width: 300px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 统计卡片样式 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  font-size: 24px;
  color: #165dff;
  margin-right: 12px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #86909c;
  margin-top: 4px;
}

/* 表格卡片样式 */
.table-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 0 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.table-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  display: flex;
  align-items: center;
}

.table-icon {
  margin-right: 8px;
  color: #165dff;
}

.table-actions {
  display: flex;
  gap: 8px;
}

/* 数据表格样式 */
.data-table {
  border-radius: 6px;
}

/* 单元格样式 */
.content-cell, .answer-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.content-preview, .answer-preview {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.tags-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.time-cell {
  font-size: 12px;
  color: #666;
}

.judge-config {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.config-item {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.config-label {
  color: #666;
  font-weight: 500;
}

.config-value {
  color: #333;
}

.test-case {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.case-count {
  font-size: 12px;
  color: #666;
}

/* 模态框样式 */
.test-case-modal {
  margin-bottom: 24px;
  padding: 16px;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  background-color: #fafafa;
}

.test-case-modal h4 {
  margin: 0 0 12px 0;
  color: #1d2129;
  font-size: 14px;
  font-weight: 600;
}

.case-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.case-input, .case-output {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.case-input strong, .case-output strong {
  font-size: 12px;
  color: #666;
}

.case-input pre, .case-output pre {
  background-color: #f5f5f5;
  padding: 8px;
  border-radius: 4px;
  font-size: 12px;
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 表格样式优化 */
:deep(.arco-table-th) {
  background-color: #fafafa;
  font-weight: 600;
  color: #1d2129;
}

:deep(.arco-table-td) {
  vertical-align: middle;
}

:deep(.arco-table) {
  border-radius: 6px;
  overflow: hidden;
}

/* 状态标签样式 */
:deep(.arco-tag) {
  border-radius: 4px;
  font-weight: 500;
}

/* 标签样式 */
.tags-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 12px;
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 统一所有标签样式为浅蓝色 */
.tag.algorithm,
.tag.data-structure,
.tag.math,
.tag.string {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

/* 操作按钮样式 */
:deep(.arco-btn-text) {
  color: #165dff;
}

:deep(.arco-btn-text:hover) {
  background-color: #f0f7ff;
}

/* 分页样式 */
:deep(.arco-pagination) {
  margin-top: 16px;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 500px) {
  #manageQuestionView {
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .action-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: center;
  }
}
</style>
