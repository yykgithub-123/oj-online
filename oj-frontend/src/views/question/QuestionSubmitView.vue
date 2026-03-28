<template>
  <div id="questionSubmitView">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <a-icon type="code" class="title-icon" />
        题目提交管理
      </h2>
      <p class="page-description">查看和管理所有题目提交记录</p>
    </div>

    <!-- 搜索表单 -->
    <a-card class="search-card" :bordered="false">
      <a-form :model="searchParams" layout="inline" class="search-form">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item field="questionId" label="题号">
              <a-input 
                v-model="searchParams.questionId" 
                placeholder="请输入题号" 
                allow-clear
                class="search-input"
              >
                <template #prefix>
                  <a-icon type="number" />
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item field="language" label="编程语言">
              <a-select 
                v-model="searchParams.language" 
                placeholder="选择编程语言" 
                allow-clear
                class="search-input"
              >
                <a-option value="java">Java</a-option>
                <a-option value="C++">C++</a-option>
                <a-option value="python">Python</a-option>
                <a-option value="go">Go</a-option>
                <a-option value="javascript">JavaScript</a-option>
                <a-option value="html">HTML</a-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item field="status" label="判题状态">
              <a-select 
                v-model="searchParams.status" 
                placeholder="选择状态" 
                allow-clear
                class="search-input"
              >
                <a-option value="0">等待中</a-option>
                <a-option value="1">判题中</a-option>
                <a-option value="2">成功</a-option>
                <a-option value="3">失败</a-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="doSubmit" class="search-btn">
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
          </a-col>
        </a-row>
      </a-form>
    </a-card>
    <a-divider size="0" />
    <a-card class="table-card" :bordered="false">
      <div class="table-header">
        <div class="table-title">
          <a-icon type="table" class="table-icon" />
          提交记录列表
        </div>
        <div class="table-actions">
          <a-button type="primary" @click="refreshData" class="refresh-btn">
            <template #icon>
              <a-icon type="reload" />
            </template>
            刷新
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
          showSizeChanger: true,
        }" 
        @page-change="onPageChange"
        class="data-table"
      >
      <template #questionTitle="{ record }">
        <div v-if="record.questionVO && record.questionVO.title">
          {{ record.questionVO.title }}
        </div>
        <div v-else>
          <a-tag color="orange">题目ID: {{ record.questionId }}</a-tag>
        </div>
      </template>
      <template #submitter="{ record }">
        <div v-if="record.userVO && record.userVO.userName">
          {{ record.userVO.userName }}
        </div>
        <div v-else>
          <a-tag color="blue">用户ID: {{ record.userId }}</a-tag>
        </div>
      </template>
      <template #judgeInfo="{ record }">
        <div v-if="record.judgeInfo" class="judge-info">
          <a-descriptions :data="transformJudgeInfo(record.judgeInfo)" layout="inline-horizontal" size="small" />
        </div>
        <div v-else class="no-judge-info">
          <a-tag color="gray">暂无判题信息</a-tag>
        </div>
      </template>
      <!-- 判题状态 -->
      <template #status="{ record }">
        <a-tag :color="getStatusColor(record.status)">
          {{ formatStatus(record.status) }}
        </a-tag>
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD HH:mm") }}
      </template>
      <template #action="{ record }">
        <a-space>
          <a-button type="text" size="small" @click="viewDetail(record)">
            <template #icon>
              <a-icon type="eye" />
            </template>
            查看
          </a-button>
          <a-button type="text" size="small" @click="viewCode(record)" v-if="record.code">
            <template #icon>
              <a-icon type="code" />
            </template>
            代码
          </a-button>
        </a-space>
      </template>
    </a-table>
    </a-card>
    
    <!-- 代码查看模态框 -->
    <a-modal 
      v-model:visible="codeModalVisible" 
      title="代码详情" 
      width="800px" 
      :footer="false"
      :mask-closable="true"
    >
      <div v-if="currentCodeRecord" class="code-modal-content">
        <div class="code-info">
          <a-descriptions :data="codeInfoData" layout="vertical" bordered />
        </div>
        <div class="code-content">
          <h4>代码内容：</h4>
          <pre class="code-block">{{ currentCodeRecord.code }}</pre>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect, computed } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionSubmitQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";

const tableRef = ref();
const loading = ref(false);

const dataList = ref([]);
const total = ref(0);

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

const loadData = async () => {
  loading.value = true;
  try {
    // 处理questionId的类型转换
    const params = {
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    };
    
    // 如果questionId是字符串，转换为数字
    if (params.questionId && typeof params.questionId === 'string') {
      params.questionId = parseInt(params.questionId);
    }
    
    const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(params);
    if (res.code === 0) {
      dataList.value = res.data.records;
      total.value = res.data.total;
    } else {
      message.error("加载失败，" + res.message);
    }
  } catch (error) {
    console.error('加载数据错误:', error);
    message.error("网络错误，请稍后重试");
  } finally {
    loading.value = false;
  }
};

/**
 * 刷新数据
 */
const refreshData = () => {
  loadData();
};

/**
 * 查看详情
 */
const viewDetail = (record: any) => {
  // 跳转到题目详情页面
  if (record.questionId) {
    router.push({
      path: `/view/question/${record.questionId}`,
    });
  } else {
    message.warning("无法获取题目信息");
  }
};

/**
 * 查看代码
 */
const viewCode = (record: any) => {
  if (record.code) {
    // 创建模态框显示代码
    showCodeModal(record);
  } else {
    message.warning("暂无代码信息");
  }
};

/**
 * 显示代码模态框
 */
const showCodeModal = (record: any) => {
  currentCodeRecord.value = record;
  codeModalVisible.value = true;
};

/**
 * 监听 searchParams 变量，改变时触发页面的重新加载
 */
watchEffect(() => {
  loadData();
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

const columns = [
  // {
  //   title: "提交号",
  //   dataIndex: "id",
  // },
  {
    title: "题目标题",
    dataIndex: "questionVO.title",
    width: 200,
    ellipsis: true,
    tooltip: true,
  },
  {
    title: "编程语言",
    dataIndex: "language",
    width: 120,
    align: "center",
  },
  {
    title: "判题信息",
    slotName: "judgeInfo",
    width: 300,
  },
  {
    title: "判题状态",
    slotName: "status",
    width: 120,
    align: "center",
  },
  {
    title: "提交者",
    slotName: "submitter",
    width: 120,
    align: "center",
  },
  {
    title: "创建时间",
    slotName: "createTime",
    width: 120,
    align: "center",
  },
  {
    title: "操作",
    slotName: "action",
    width: 150,
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

const router = useRouter();

/**
 * 跳转到做题页面
 * @param question
 */
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

/**
 * 确认搜索，重新加载数据
 */
const doSubmit = () => {
  // 这里需要重置搜索页号
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
};

/**
 * 重置搜索条件
 */
const resetSearch = () => {
  searchParams.value = {
    questionId: undefined,
    language: undefined,
    status: undefined,
    pageSize: 10,
    current: 1,
  };
};
// 将 judgeInfo 对象转换为 descriptions 需要的数组形式
const transformJudgeInfo = (judgeInfo: Record<string, any>) => {
  if (!judgeInfo) {
    return [];
  }
  
  return Object.keys(judgeInfo).map(key => {
    const value = judgeInfo[key];
    
    if (key === 'time') {
      return {
        label: key,
        value: value ? `${value}ms` : 'nullms'
      };
    }

    if (key === 'memory') {
      return {
        label: key,
        value: value ? `${value}kb` : 'nullkb'
      };
    }

    if (key === 'message') {
      if (!value) {
        return {
          label: key,
          value: 'null'
        };
      }
      // 限制message最多显示8个字符
      const displayValue = value.length > 8 ? value.substring(0, 8) + '...' : value;
      return {
        label: key,
        value: displayValue
      };
    }

    return {
      label: key,
      value: value ? `${value}` : 'null'
    };
  });
};
 


// 根据 status 返回对应的状态字符串
const formatStatus = (status: any) => {
  // 确保status是数字类型
  const statusNum = parseInt(status);
  switch (statusNum) {
    case 0:
      return "等待中";
    case 1:
      return "判题中";
    case 2:
      return "成功";
    case 3:
      return "失败";
    default:
      return "未知状态";
  }
};

// 根据 status 返回对应的颜色
const getStatusColor = (status: any) => {
  // 确保status是数字类型
  const statusNum = parseInt(status);
  switch (statusNum) {
    case 0:
      return "orange"; // 等待中
    case 1:
      return "blue";   // 判题中
    case 2:
      return "green";  // 成功
    case 3:
      return "red";    // 失败
    default:
      return "gray";   // 未知状态
  }
};

// 代码信息数据
const codeInfoData = computed(() => {
  if (!currentCodeRecord.value) return [];
  
  return [
    {
      label: '提交ID',
      value: currentCodeRecord.value.id,
    },
    {
      label: '题目ID',
      value: currentCodeRecord.value.questionId,
    },
    {
      label: '编程语言',
      value: currentCodeRecord.value.language,
    },
    {
      label: '判题状态',
      value: formatStatus(currentCodeRecord.value.status),
    },
    {
      label: '提交时间',
      value: moment(currentCodeRecord.value.createTime).format("YYYY-MM-DD HH:mm:ss"),
    },
  ];
});
</script>

<style scoped>
#questionSubmitView {
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

/* 搜索卡片样式 */
.search-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-form {
  padding: 0;
}

.search-input {
  width: 100%;
}

.search-btn {
  margin-right: 8px;
}

.reset-btn {
  border-color: #d9d9d9;
  color: #666;
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

.refresh-btn {
  border-radius: 6px;
}

/* 数据表格样式 */
.data-table {
  border-radius: 6px;
}

/* 判题信息样式 */
.judge-info {
  max-width: 280px;
}

.no-judge-info {
  text-align: center;
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

/* 判题信息描述列表样式 */
:deep(.arco-descriptions-item-label) {
  font-weight: 500;
  color: #666;
}

:deep(.arco-descriptions-item-value) {
  color: #333;
}

/* 状态标签样式 */
:deep(.arco-tag) {
  border-radius: 4px;
  font-weight: 500;
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

/* 代码模态框样式 */
.code-modal-content {
  max-height: 600px;
  overflow-y: auto;
}

.code-info {
  margin-bottom: 20px;
}

.code-content h4 {
  margin: 0 0 12px 0;
  color: #1d2129;
  font-size: 14px;
  font-weight: 600;
}

.code-block {
  background-color: #f5f5f5;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.5;
  overflow-x: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 400px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 500px) {
  #questionSubmitView {
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .table-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
