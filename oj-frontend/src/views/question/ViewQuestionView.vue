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
                <h3>输入描述</h3>
                <div class="constraint-text">
                  输入共1行，包含2个整数n、x，之间用一个空格隔开。
                </div>
                
                <h3>输出描述</h3>
                <div class="constraint-text">
                  输出共1行，包含一个整数，表示x出现的次数。
                </div>
                
                <h3>输入输出样例</h3>
                <div class="example-section">
                  <div class="example-item">
                    <div class="example-label">示例</div>
                    <div class="example-content">
                      <div class="input-section">
                        <div class="section-title">输入</div>
                        <div class="code-block">11 1</div>
                      </div>
                      <div class="output-section">
                        <div class="section-title">输出</div>
                        <div class="code-block">4</div>
                      </div>
                    </div>
                  </div>
                </div>
                
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
                  <span>总通过次数: {{ submitStats.totalPassed || 1768 }}</span>
                  <span>总尝试次数: {{ submitStats.totalAttempts || 1852 }}</span>
                  <span>通过率: {{ submitStats.passRate || '95.5%' }}</span>
                </div>
              </div>
              <div class="solution-text">
                <div v-html="getSolutionContent()"></div>
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
import { onMounted, ref, watchEffect, withDefaults, defineProps } from "vue";
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

// 添加缺失的响应式数据
const submitStats = ref({
  totalPassed: 1768,
  totalAttempts: 1852,
  passRate: '95.5%'
});

const userSubmitRecords = ref([]);

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

/**
 * 根据题目获取对应的题解内容
 */
const getSolutionContent = () => {
  if (!question.value) return '<p>暂无题解</p>';
  
  const questionId = question.value.id;
  const questionTitle = question.value.title || '';
  
  // 根据题目ID或标题返回不同的题解内容
  const solutions: Record<string, string> = {
    '1': `
      <p>这是一个计数问题，需要统计数字x在1到n中出现的次数。</p>
      <p><strong>解题思路：</strong></p>
      <ol>
        <li>遍历从1到n的所有数字</li>
        <li>对每个数字，检查其各个位数是否包含x</li>
        <li>统计总的出现次数</li>
      </ol>
      <p><strong>时间复杂度：</strong>O(n * log n)</p>
      <p><strong>空间复杂度：</strong>O(1)</p>
    `,
    '2': `
      <p>这是一个字符串处理问题，需要找到无重复字符的最长子串。</p>
      <p><strong>解题思路：</strong></p>
      <ol>
        <li>使用滑动窗口技术</li>
        <li>维护一个哈希表记录字符出现位置</li>
        <li>当遇到重复字符时，移动左指针</li>
      </ol>
      <p><strong>时间复杂度：</strong>O(n)</p>
      <p><strong>空间复杂度：</strong>O(min(m,n))</p>
    `,
    '3': `
      <p>这是一个回文字符串问题，需要找到最长的回文子串。</p>
      <p><strong>解题思路：</strong></p>
      <ol>
        <li>中心扩展算法</li>
        <li>对每个可能的中心点进行扩展</li>
        <li>考虑奇数长度和偶数长度的回文串</li>
      </ol>
      <p><strong>时间复杂度：</strong>O(n²)</p>
      <p><strong>空间复杂度：</strong>O(1)</p>
    `
  };
  
  // 如果有对应的题解，返回对应内容，否则返回通用题解
  if (solutions[questionId?.toString() || '']) {
    return solutions[questionId?.toString() || ''];
  }
  
  // 根据题目标题关键词匹配题解
  if (questionTitle.includes('两数之和')) {
    return `
      <p>这是一个经典的哈希表问题。</p>
      <p><strong>解题思路：</strong></p>
      <ol>
        <li>使用哈希表存储已遍历的数字和索引</li>
        <li>对于每个数字，检查target-num是否在哈希表中</li>
        <li>如果存在，返回两个索引</li>
      </ol>
      <p><strong>时间复杂度：</strong>O(n)</p>
      <p><strong>空间复杂度：</strong>O(n)</p>
    `;
  } else if (questionTitle.includes('排序')) {
    return `
      <p>这是一个排序算法问题。</p>
      <p><strong>解题思路：</strong></p>
      <ol>
        <li>可以使用快速排序、归并排序等算法</li>
        <li>根据数据规模选择合适的排序算法</li>
        <li>注意边界条件的处理</li>
      </ol>
      <p><strong>时间复杂度：</strong>O(n log n)</p>
      <p><strong>空间复杂度：</strong>O(log n)</p>
    `;
  }
  
  // 默认通用题解
  return `
    <p>这道题目考查的是基本的算法和数据结构知识。</p>
    <p><strong>解题思路：</strong></p>
    <ol>
      <li>仔细分析题目要求</li>
      <li>选择合适的算法和数据结构</li>
      <li>注意边界条件和特殊情况</li>
      <li>优化时间和空间复杂度</li>
    </ol>
    <p><strong>建议：</strong>多练习类似题目，掌握常见的算法模式。</p>
  `;
};
</script>

<style scoped>
#viewQuestionView {
  /* 移除最大宽度限制，让页面占满整个屏幕 */
  width: 100%;
  margin: 0;
  padding: 20px;
  height: calc(100vh - 64px);
  /* 确保内容可以滚动 */
  overflow-y: auto;
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
}

.right-panel {
  min-width: 20%;
  max-width: 80%;
  overflow: hidden;
}

.splitter {
  width: 4px;
  background-color: #e5e6eb;
  cursor: col-resize;
  position: relative;
  transition: background-color 0.2s;
}

.splitter:hover {
  background-color: #165dff;
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
  margin-bottom: 16px;
}

.home-icon {
  cursor: pointer;
  color: #666;
  transition: color 0.2s;
}

.home-icon:hover {
  color: #4a90e2;
}

.question-tabs {
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.question-content {
  padding: 16px 0;
}

.difficulty-tags {
  margin-bottom: 16px;
}

.question-tag {
  margin-right: 8px;
}

.question-description h3,
.question-constraints h3 {
  color: #1d2129;
  font-size: 16px;
  font-weight: 600;
  margin: 16px 0 8px 0;
}

.content-text,
.constraint-text {
  color: #4e5969;
  line-height: 1.6;
  margin-bottom: 16px;
}

.example-section {
  margin: 16px 0;
}

.example-item {
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  padding: 16px;
  background-color: #f7f8fa;
}

.example-label {
  font-weight: 600;
  margin-bottom: 12px;
  color: #1d2129;
}

.example-content {
  display: flex;
  gap: 24px;
}

.input-section,
.output-section {
  flex: 1;
}

.section-title {
  font-weight: 500;
  margin-bottom: 8px;
  color: #4e5969;
}

.code-block {
  background-color: #ffffff;
  border: 1px solid #e5e6eb;
  border-radius: 4px;
  padding: 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  color: #1d2129;
}

.limits ul {
  margin: 0;
  padding-left: 20px;
}

.limits li {
  color: #4e5969;
  line-height: 1.6;
}

.solution-content {
  padding: 16px 0;
}

.solution-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.solution-header h3 {
  margin: 0;
  color: #1d2129;
  font-size: 16px;
  font-weight: 600;
}

.solution-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #86909c;
}

.solution-text {
  color: #4e5969;
  line-height: 1.6;
}

.solution-text ol {
  padding-left: 20px;
}

.records-content {
  padding: 16px 0;
}

.records-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.records-header h3 {
  margin: 0;
  color: #1d2129;
  font-size: 16px;
  font-weight: 600;
}

.refresh-btn {
  color: #165dff;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  background-color: #ffffff;
}

.record-item.success {
  border-color: #00b42a;
  background-color: #f6ffed;
}

.record-time,
.record-size,
.record-duration,
.record-language {
  flex: 1;
  font-size: 14px;
  color: #4e5969;
}

.record-status {
  font-weight: 500;
}

.status-success {
  color: #00b42a;
}

.status-error {
  color: #f53f3f;
}

.status-pending {
  color: #ff7d00;
}

.no-records {
  text-align: center;
  padding: 40px 0;
}

.code-section {
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #e5e6eb;
  border-bottom: none;
  background-color: #f7f8fa;
  border-radius: 6px 6px 0 0;
}

.language-select {
  min-width: 120px;
}

.settings-btn {
  color: #86909c;
}

.code-editor-container {
  flex: 1;
  border: 1px solid #e5e6eb;
  border-bottom: none;
}

.code-editor {
  height: 100%;
}

.code-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #e5e6eb;
  background-color: #f7f8fa;
  border-radius: 0 0 6px 6px;
}

.console-text {
  color: #86909c;
  font-size: 14px;
  cursor: pointer;
}

.console-text:hover {
  color: #165dff;
}

.action-right {
  display: flex;
  gap: 12px;
}

.test-btn {
  border-color: #165dff;
  color: #165dff;
}

.submit-btn {
  background-color: #165dff;
  border-color: #165dff;
}

.console-panel {
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  background-color: #1e1e1e;
  color: #ffffff;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  margin-top: 8px;
  max-height: 200px;
  display: flex;
  flex-direction: column;
}

.console-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #333;
  background-color: #2d2d2d;
  border-radius: 6px 6px 0 0;
}

.console-header span {
  color: #ffffff;
  font-weight: 500;
}

.console-content {
  flex: 1;
  padding: 8px 12px;
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

@media (max-width: 768px) {
  #viewQuestionView {
    padding: 12px;
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
  }
  
  .splitter {
    display: none;
  }
  
  .example-content {
    flex-direction: column;
    gap: 12px;
  }
  
  .solution-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .solution-stats {
    flex-direction: column;
    gap: 4px;
  }
}
</style>