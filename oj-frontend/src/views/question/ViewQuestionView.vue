<template>
  <div id="viewQuestionView">
    <div class="split-container">
      <!-- 左侧：题目信息和标签页 -->
      <div class="left-panel" :style="{ width: leftWidth + '%' }">
        <div class="question-header">
          <a-breadcrumb>
            <a-breadcrumb-item>
              <icon-home @click="goToQuestions" class="home-icon" />
            </a-breadcrumb-item>
            <a-breadcrumb-item>{{ question?.id || '1' }}. {{ question?.title || '计数问题' }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        
        <a-tabs default-active-key="question" class="question-tabs">
          <a-tab-pane key="question" title="题目">
            <div class="question-content">
              <div class="question-info">
                <div class="difficulty-tags">
                  <a-space wrap>
                    <a-tag
                      v-for="(tag, index) of question?.tags"
                      :key="index"
                      color="green"
                      class="question-tag"
                    >{{ tag }}</a-tag>
                  </a-space>
                </div>
              </div>
              
              <div class="question-description">
                <h3>题目描述</h3>
                <div class="content-text">
                  <MdViewer :value="question?.content || ''" />
                </div>
              </div>
              
              <div class="question-constraints" v-if="question">
                <h3>运行限制</h3>
                <div class="limits">
                  <ul>
                    <li>最大运行时间：{{ question.judgeConfig?.timeLimit ?? 1 }}ms</li>
                    <li>最大运行内存：{{ question.judgeConfig?.memoryLimit ?? 128 }}M</li>
                  </ul>
                </div>
              </div>
            </div>
          </a-tab-pane>
          
          <a-tab-pane key="solution" title="题解">
            <div class="solution-content">
              <div class="solution-header">
                <h3>官方题解</h3>
                <div class="solution-stats">
                  <span>总通过次数: {{ question?.acceptedNum || 0 }}</span>
                  <span>总尝试次数: {{ question?.submitNum || 0 }}</span>
                  <span>通过率: {{ passRate }}</span>
                </div>
              </div>
              <div class="solution-text">
                <div class="solution-code-wrapper" v-if="question?.answer">
                  <div class="solution-code-header">
                    <span class="solution-code-title">官方题解代码</span>
                    <span class="solution-code-lang">{{ form.language }}</span>
                  </div>
                  <pre class="solution-code-body"><code>{{ question.answer }}</code></pre>
                </div>
                <p class="no-solution" v-else>暂无官方题解</p>
              </div>
            </div>
          </a-tab-pane>
          
          <a-tab-pane key="records" title="记录">
            <div class="records-content">
              <div class="records-header">
                <h3>我的提交记录</h3>
                <a-button type="text" @click="refreshRecords" class="refresh-btn">
                  <template #icon>
                    <icon-refresh />
                  </template>
                  刷新
                </a-button>
              </div>
              
              <div class="records-list" v-if="userSubmitRecords.length > 0">
                <div 
                  v-for="record in userSubmitRecords" 
                  :key="record.id"
                  class="record-item"
                  :class="{ 'success': record.status === 2 }"
                >
                  <div class="record-time">{{ formatTime(record.createTime) }}</div>
                  <div class="record-size">{{ formatSize(record.judgeInfo?.memory) }}</div>
                  <div class="record-duration">{{ formatDuration(record.judgeInfo?.time) }}</div>
                  <div class="record-language">{{ record.language }}</div>
                  <div class="record-status" :class="getStatusClass(record.status)">
                    {{ formatStatus(record.status) }}
                  </div>
                </div>
              </div>
              
              <div v-else class="no-records">
                <a-empty description="暂无提交记录" />
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
        </div>
        
        <!-- 分割线 -->
        <div class="splitter" @mousedown="startResize"></div>
        
        <!-- 右侧：代码编辑器 -->
        <div class="right-panel" :style="{ width: rightWidth + '%' }">
        <div class="code-section">
          <div class="code-header">
            <a-form :model="form" layout="inline">
              <a-form-item field="language" label="编译语言:">
                <a-select
                  v-model="form.language"
                  :style="{ width: '120px' }"
                  placeholder="选择语言"
                  class="language-select"
                >
                  <a-option value="java">Java (11)</a-option>
                  <a-option value="C++">C++</a-option>
                  <a-option value="go">Go</a-option>
                  <a-option value="python">Python</a-option>
                </a-select>
              </a-form-item>
            </a-form>
            <a-button type="text" class="settings-btn">
              <template #icon>
                <icon-settings />
              </template>
            </a-button>
          </div>
          
          <div class="code-editor-container">
            <CodeEditor
              :value="form.code as string"
              :language="form.language"
              :handle-change="changeCode"
              class="code-editor"
            />
          </div>
          
          <div class="code-actions">
            <div class="action-left">
              <span class="console-text" @click="toggleConsole">
                {{ showConsole ? '关闭控制台' : '打开控制台' }}
              </span>
            </div>
            <div class="action-right">
              <!-- 调试按钮已隐藏 -->
              <!-- <a-button class="test-btn" @click="runTest">调试</a-button> -->
              <a-button type="primary" class="submit-btn" @click="doSubmit">提交检测</a-button>
            </div>
          </div>
          
          <!-- 控制台 -->
          <div v-if="showConsole" class="console-panel">
            <div class="console-header">
              <span>控制台</span>
              <a-button type="text" size="small" @click="clearConsole">清空</a-button>
            </div>
            <div class="console-content">
              <div v-for="(log, index) in consoleLogs" :key="index" class="console-log">
                {{ log }}
              </div>
              <div v-if="consoleLogs.length === 0" class="console-empty">
                控制台输出将显示在这里...
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect, withDefaults, defineProps, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStore } from "vuex";
import { IconHome, IconRefresh, IconSettings } from '@arco-design/web-vue/es/icon';
import message from "@arco-design/web-vue/es/message";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import {
  QuestionControllerService,
  QuestionSubmitAddRequest,
  QuestionVO,
} from "../../../generated";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

const question = ref<QuestionVO>();
const router = useRouter();
const route = useRoute();
const store = useStore();

const userSubmitRecords = ref([]);

// 通过率计算属性
const passRate = computed(() => {
  const submitNum = question.value?.submitNum || 0;
  const acceptedNum = question.value?.acceptedNum || 0;
  if (submitNum === 0) return '0%';
  return Math.round((acceptedNum / submitNum) * 100) + '%';
});

// 控制台相关
const showConsole = ref(false);
const consoleLogs = ref<string[]>([]);

// 分栏调整相关
const leftWidth = ref(50);
const rightWidth = ref(50);
const isResizing = ref(false);

const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
  } else {
    message.error("加载失败，" + res.message);
  }
};

const form = ref<QuestionSubmitAddRequest>({
  language: "java",
  code: "",
});

/**
 * 提交代码
 */
const doSubmit = async () => {
  if (!question.value?.id) {
    return;
  }

  const res = await QuestionControllerService.doQuestionSubmitUsingPost({
    ...form.value,
    questionId: question.value.id,
  });
  if (res.code === 0) {
    message.success("提交成功");
    // 提交成功后刷新记录列表
    await loadUserSubmitRecords();
  } else {
    message.error("提交失败," + res.message);
  }
};

/**
 * 运行测试
 */
const runTest = () => {
  consoleLogs.value.push(`[${new Date().toLocaleTimeString()}] 开始调试...`);
  showConsole.value = true;
  setTimeout(() => {
    consoleLogs.value.push(`[${new Date().toLocaleTimeString()}] 调试完成`);
  }, 1000);
};

/**
 * 加载用户提交记录
 */
const loadUserSubmitRecords = async () => {
  // 检查登录状态
  const loginUser = store.state.user?.loginUser;
  if (!loginUser || !loginUser.id) {
    console.log("用户未登录，跳过加载提交记录");
    return;
  }

  // 获取当前题目ID
  const questionId = props.id;
  if (!questionId) {
    console.log("题目ID不存在");
    return;
  }

  try {
    const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost({
      userId: loginUser.id,
      questionId: Number(questionId),
      current: 1,
      pageSize: 20,
    });

    if (res.code === 0) {
      userSubmitRecords.value = res.data?.records || [];
    } else {
      console.error("加载提交记录失败:", res.message);
    }
  } catch (error) {
    console.error("加载提交记录失败:", error);
  }
};

/**
 * 刷新记录
 */
const refreshRecords = async () => {
  await loadUserSubmitRecords();
  message.success("记录已刷新");
};

/**
 * 切换控制台显示
 */
const toggleConsole = () => {
  showConsole.value = !showConsole.value;
};

/**
 * 清空控制台
 */
const clearConsole = () => {
  consoleLogs.value = [];
};

/**
 * 开始调整分栏大小
 */
const startResize = (e: MouseEvent) => {
  isResizing.value = true;
  document.addEventListener('mousemove', handleResize);
  document.addEventListener('mouseup', stopResize);
  e.preventDefault();
};

/**
 * 处理分栏大小调整
 */
const handleResize = (e: MouseEvent) => {
  if (!isResizing.value) return;
  
  const container = document.querySelector('.split-container') as HTMLElement;
  if (!container) return;
  
  const containerRect = container.getBoundingClientRect();
  const newLeftWidth = ((e.clientX - containerRect.left) / containerRect.width) * 100;
  
  if (newLeftWidth >= 20 && newLeftWidth <= 80) {
    leftWidth.value = newLeftWidth;
    rightWidth.value = 100 - newLeftWidth;
  }
};

/**
 * 停止调整分栏大小
 */
const stopResize = () => {
  isResizing.value = false;
  document.removeEventListener('mousemove', handleResize);
  document.removeEventListener('mouseup', stopResize);
};

/**
 * 格式化时间
 */
const formatTime = (time: string) => {
  return new Date(time).toLocaleString();
};

/**
 * 格式化大小
 */
const formatSize = (size: number) => {
  return size ? `${size}KB` : '-';
};

/**
 * 格式化持续时间
 */
const formatDuration = (time: number) => {
  return time ? `${time}ms` : '-';
};

/**
 * 获取状态样式类
 */
const getStatusClass = (status: number) => {
  switch (status) {
    case 2: return 'status-success';
    case 3: return 'status-error';
    default: return 'status-pending';
  }
};

/**
 * 格式化状态
 */
const formatStatus = (status: number) => {
  switch (status) {
    case 0: return '等待中';
    case 1: return '判题中';
    case 2: return '成功';
    case 3: return '失败';
    default: return '未知';
  }
};

/**
 * 跳转到题目列表页面
 */
const goToQuestions = () => {
  router.push('/questions');
};

/**
 * 页面加载时，请求数据
 */
onMounted(() => {
  loadData();
  loadUserSubmitRecords();
});

/**
 * 等question.value?.sourceCode有值之后再赋值
 */
watchEffect(() => {
  if (question.value?.sourceCode) {
    form.value.code = question.value.sourceCode;
  }
});

const changeCode = (value: string) => {
  form.value.code = value;
};
</script>

<style scoped>
#viewQuestionView {
  width: 100%;
  margin: 0;
  padding: var(--space-5);
  height: calc(100vh - var(--header-height));
  overflow-y: auto;
  background: var(--bg-page);
}

.split-container {
  display: flex;
  height: 100%;
  gap: 0;
}

.left-panel {
  min-width: 20%;
  max-width: 80%;
  overflow: hidden;
  background: var(--bg-card);
  border-radius: var(--radius-lg) 0 0 var(--radius-lg);
  border: 1px solid var(--border-default);
}

.right-panel {
  min-width: 20%;
  max-width: 80%;
  overflow: hidden;
  background: var(--bg-card);
  border-radius: 0 var(--radius-lg) var(--radius-lg) 0;
  border: 1px solid var(--border-default);
  border-left: none;
}

.splitter {
  width: 4px;
  background-color: var(--border-default);
  cursor: col-resize;
  position: relative;
  transition: background-color var(--duration-fast);
}

.splitter:hover {
  background-color: var(--color-primary-500);
}

.splitter::before {
  content: '';
  position: absolute;
  left: -2px;
  right: -2px;
  top: 0;
  bottom: 0;
}

.question-header {
  margin-bottom: var(--space-4);
  padding: var(--space-4);
  border-bottom: 1px solid var(--border-default);
}

.home-icon {
  cursor: pointer;
  color: var(--text-secondary);
  transition: color var(--duration-fast);
}

.home-icon:hover {
  color: var(--color-primary-500);
}

.question-tabs {
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.question-tabs :deep(.arco-tabs-nav) {
  padding: 0 var(--space-4);
}

.question-content {
  padding: var(--space-4);
}

.difficulty-tags {
  margin-bottom: var(--space-4);
}

.question-tag {
  margin-right: var(--space-2);
  border-radius: var(--radius-sm);
}

.question-description h3,
.question-constraints h3 {
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: 600;
  margin: var(--space-4) 0 var(--space-2) 0;
}

.content-text,
.constraint-text {
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: var(--space-4);
}

.example-section {
  margin: var(--space-4) 0;
}

.example-item {
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  padding: var(--space-4);
  background-color: var(--color-gray-50);
}

.example-label {
  font-weight: 600;
  margin-bottom: var(--space-3);
  color: var(--text-primary);
}

.example-content {
  display: flex;
  gap: var(--space-6);
}

.input-section,
.output-section {
  flex: 1;
}

.section-title {
  font-weight: 500;
  margin-bottom: var(--space-2);
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.code-block {
  background-color: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-sm);
  padding: var(--space-3);
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: var(--text-sm);
  color: var(--text-primary);
}

.limits ul {
  margin: 0;
  padding-left: var(--space-5);
}

.limits li {
  color: var(--text-secondary);
  line-height: 1.6;
  font-size: var(--text-sm);
}

/* 题解样式 */
.solution-content {
  padding: var(--space-4);
}

.solution-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.solution-header h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: 600;
}

.solution-stats {
  display: flex;
  gap: var(--space-4);
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.solution-text {
  color: var(--text-secondary);
  line-height: 1.6;
}

.solution-code-wrapper {
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  margin: var(--space-4) 0;
  background-color: var(--color-gray-50);
  overflow: hidden;
}

.solution-code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  background-color: var(--color-gray-100);
  border-bottom: 1px solid var(--border-default);
}

.solution-code-title {
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.solution-code-lang {
  background-color: var(--color-primary-500);
  color: #fff;
  padding: 2px var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
}

.solution-code-body {
  margin: 0;
  padding: var(--space-4);
  background-color: var(--color-gray-50);
  overflow-x: auto;
  max-height: 400px;
}

.solution-code-body code {
  font-family: Monaco, Menlo, Consolas, monospace;
  font-size: var(--text-sm);
  color: var(--text-primary);
  line-height: 1.6;
  white-space: pre;
}

.no-solution {
  color: var(--text-secondary);
  text-align: center;
  padding: var(--space-10) 0;
  font-size: var(--text-sm);
}

/* 记录样式 */
.records-content {
  padding: var(--space-4);
}

.records-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.records-header h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: 600;
}

.refresh-btn {
  color: var(--color-primary-500);
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  background-color: var(--bg-card);
}

.record-item.success {
  border-color: var(--color-success);
  background-color: var(--color-success-bg);
}

.record-time,
.record-size,
.record-duration,
.record-language {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.record-status {
  font-weight: 500;
  font-size: var(--text-sm);
}

.status-success {
  color: var(--color-success);
}

.status-error {
  color: var(--color-error);
}

.status-pending {
  color: var(--color-warning);
}

.no-records {
  text-align: center;
  padding: var(--space-10) 0;
}

/* 代码编辑器区域 */
.code-section {
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  border: 1px solid var(--border-default);
  border-bottom: none;
  background-color: var(--color-gray-50);
  border-radius: var(--radius-md) var(--radius-md) 0 0;
}

.language-select {
  min-width: 120px;
}

.settings-btn {
  color: var(--text-secondary);
}

.code-editor-container {
  flex: 1;
  border: 1px solid var(--border-default);
  border-bottom: none;
}

.code-editor {
  height: 100%;
}

.code-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  border: 1px solid var(--border-default);
  background-color: var(--color-gray-50);
  border-radius: 0 0 var(--radius-md) var(--radius-md);
}

.console-text {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
}

.console-text:hover {
  color: var(--color-primary-500);
}

.action-right {
  display: flex;
  gap: var(--space-3);
}

.test-btn {
  border-color: var(--color-primary-500);
  color: var(--color-primary-500);
}

.submit-btn {
  background-color: var(--color-primary-500);
  border-color: var(--color-primary-500);
}

/* 控制台样式 */
.console-panel {
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  background-color: #1e1e1e;
  color: #ffffff;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: var(--text-xs);
  margin-top: var(--space-2);
  max-height: 200px;
  display: flex;
  flex-direction: column;
}

.console-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-2) var(--space-3);
  border-bottom: 1px solid #333;
  background-color: #2d2d2d;
  border-radius: var(--radius-md) var(--radius-md) 0 0;
}

.console-header span {
  color: #ffffff;
  font-weight: 500;
}

.console-content {
  flex: 1;
  padding: var(--space-2) var(--space-3);
  overflow-y: auto;
  min-height: 100px;
}

.console-log {
  margin: 2px 0;
  color: #00ff00;
  line-height: 1.4;
}

.console-empty {
  color: #888;
  font-style: italic;
}

/* 响应式设计 */
@media (max-width: 768px) {
  #viewQuestionView {
    padding: var(--space-3);
  }

  .split-container {
    flex-direction: column;
    height: auto;
  }

  .left-panel,
  .right-panel {
    width: 100% !important;
    min-width: 100%;
    max-width: 100%;
    border-radius: var(--radius-lg);
    border: 1px solid var(--border-default);
    margin-bottom: var(--space-3);
  }

  .right-panel {
    border-left: 1px solid var(--border-default);
  }

  .splitter {
    display: none;
  }

  .example-content {
    flex-direction: column;
    gap: var(--space-3);
  }

  .solution-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }

  .solution-stats {
    flex-direction: column;
    gap: 4px;
  }
}
</style>