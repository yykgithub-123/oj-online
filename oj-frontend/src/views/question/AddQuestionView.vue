<template>
  <div id="addQuestionView">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <div class="title-icon-wrapper">
            <icon-plus />
          </div>
          <div class="title-text">
            <h1 class="page-title">{{ updatePage ? '编辑题目' : '创建题目' }}</h1>
            <p class="page-subtitle">{{ updatePage ? '修改题目信息和配置' : '创建新的编程题目' }}</p>
          </div>
        </div>
        <div class="header-actions">
          <a-button type="outline" @click="previewQuestion" class="preview-btn">
            <template #icon>
              <icon-eye />
            </template>
            预览
          </a-button>
        </div>
      </div>
    </div>

    <!-- 表单主体 -->
    <div class="form-container">
      <!-- 基本信息 -->
      <div class="form-section">
        <div class="section-header">
          <div class="section-icon">
            <icon-file />
          </div>
          <h3 class="section-title">基本信息</h3>
        </div>
        <div class="section-content">
          <a-row :gutter="24">
            <a-col :span="12">
              <div class="form-group">
                <label class="form-label required">题目标题</label>
                <a-input
                  v-model="form.title"
                  placeholder="请输入题目标题"
                  allow-clear
                  class="form-input"
                />
              </div>
            </a-col>
            <a-col :span="12">
              <div class="form-group">
                <label class="form-label required">难度等级</label>
                <a-select
                  v-model="form.difficulty"
                  placeholder="请选择难度等级"
                  class="form-input"
                >
                  <a-option value="简单">
                    <span class="difficulty-tag easy">简单</span>
                  </a-option>
                  <a-option value="中等">
                    <span class="difficulty-tag medium">中等</span>
                  </a-option>
                  <a-option value="困难">
                    <span class="difficulty-tag hard">困难</span>
                  </a-option>
                </a-select>
              </div>
            </a-col>
          </a-row>
          <div class="form-group">
            <label class="form-label">标签</label>
            <a-select
              v-model="form.tags"
              placeholder="请选择标签"
              multiple
              allow-clear
              class="form-input"
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
        </div>
      </div>

      <!-- 题目内容 -->
      <div class="form-section">
        <div class="section-header">
          <div class="section-icon content">
            <icon-edit />
          </div>
          <h3 class="section-title">题目内容</h3>
        </div>
        <div class="section-content">
          <div class="form-group">
            <label class="form-label required">题目描述</label>
            <div class="editor-wrapper">
              <MdEditor :value="form.content" :handle-change="onContentChange" />
            </div>
          </div>
        </div>
      </div>

      <!-- 答案和代码 -->
      <div class="form-section">
        <div class="section-header">
          <div class="section-icon code">
            <icon-code />
          </div>
          <h3 class="section-title">答案和代码</h3>
        </div>
        <div class="section-content">
          <div class="form-group">
            <label class="form-label">题目答案</label>
            <div class="editor-wrapper">
              <MdEditor :value="form.answer" :handle-change="onAnswerChange" />
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">初始代码</label>
            <div class="editor-wrapper code-editor">
              <MdEditor :value="form.sourceCode" :handle-change="onSourceCodeChange" />
            </div>
          </div>
        </div>
      </div>

      <!-- 判题配置 -->
      <div class="form-section">
        <div class="section-header">
          <div class="section-icon config">
            <icon-settings />
          </div>
          <h3 class="section-title">判题配置</h3>
        </div>
        <div class="section-content">
          <div class="config-cards">
            <div class="config-card">
              <div class="config-icon time">
                <icon-clock-circle />
              </div>
              <div class="config-info">
                <label class="config-label">时间限制</label>
                <a-input-number
                  v-model="form.judgeConfig.timeLimit"
                  placeholder="请输入"
                  mode="button"
                  min="100"
                  max="10000"
                  class="config-input"
                />
                <span class="config-unit">ms</span>
              </div>
            </div>
            <div class="config-card">
              <div class="config-icon memory">
                <icon-storage />
              </div>
              <div class="config-info">
                <label class="config-label">内存限制</label>
                <a-input-number
                  v-model="form.judgeConfig.memoryLimit"
                  placeholder="请输入"
                  mode="button"
                  min="64"
                  max="512"
                  class="config-input"
                />
                <span class="config-unit">KB</span>
              </div>
            </div>
            <div class="config-card">
              <div class="config-icon stack">
                <icon-layer-group />
              </div>
              <div class="config-info">
                <label class="config-label">堆栈限制</label>
                <a-input-number
                  v-model="form.judgeConfig.stackLimit"
                  placeholder="请输入"
                  mode="button"
                  min="64"
                  max="512"
                  class="config-input"
                />
                <span class="config-unit">KB</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 测试用例 -->
      <div class="form-section">
        <div class="section-header-row">
          <div class="section-header-left">
            <div class="section-icon test">
              <icon-experiment />
            </div>
            <h3 class="section-title">测试用例</h3>
            <span class="case-count-badge">{{ form.judgeCase.length }} 个用例</span>
          </div>
          <a-button @click="handleAdd" type="primary" class="add-case-btn">
            <template #icon>
              <icon-plus />
            </template>
            添加用例
          </a-button>
        </div>
        <div class="section-content">
          <div class="test-cases">
            <div
              v-for="(judgeCaseItem, index) of form.judgeCase"
              :key="index"
              class="test-case-card"
            >
              <div class="case-header">
                <div class="case-number-badge">
                  <span class="badge-number">{{ index + 1 }}</span>
                  <span class="badge-text">测试用例</span>
                </div>
                <a-button
                  v-if="form.judgeCase.length > 1"
                  status="danger"
                  type="text"
                  size="small"
                  @click="handleDelete(index)"
                  class="delete-case-btn"
                >
                  <template #icon>
                    <icon-delete />
                  </template>
                  删除
                </a-button>
              </div>
              <a-row :gutter="16">
                <a-col :span="12">
                  <div class="form-group">
                    <label class="form-label">输入</label>
                    <a-textarea
                      v-model="judgeCaseItem.input"
                      placeholder="请输入测试输入用例"
                      :rows="4"
                      class="form-input case-textarea"
                    />
                  </div>
                </a-col>
                <a-col :span="12">
                  <div class="form-group">
                    <label class="form-label">期望输出</label>
                    <a-textarea
                      v-model="judgeCaseItem.output"
                      placeholder="请输入测试输出用例"
                      :rows="4"
                      class="form-input case-textarea"
                    />
                  </div>
                </a-col>
              </a-row>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <a-button type="primary" size="large" @click="doSubmit" :loading="submitting" class="submit-btn">
          <template #icon>
            <icon-check />
          </template>
          {{ updatePage ? '更新题目' : '创建题目' }}
        </a-button>
        <a-button size="large" @click="resetForm" class="reset-btn">
          <template #icon>
            <icon-refresh />
          </template>
          重置
        </a-button>
        <a-button size="large" @click="goBack" class="back-btn">
          <template #icon>
            <icon-left />
          </template>
          返回
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRoute, useRouter } from "vue-router";
import {
  IconPlus,
  IconEye,
  IconFile,
  IconEdit,
  IconCode,
  IconSettings,
  IconClockCircle,
  IconStorage,
  IconExperiment,
  IconDelete,
  IconCheck,
  IconRefresh,
  IconLeft,
  IconLayerGroup,
} from '@arco-design/web-vue/es/icon';

const route = useRoute();
const router = useRouter();
const submitting = ref(false);

const updatePage = route.path.includes("update");

let form = ref({
  title: "",
  difficulty: "",
  tags: [],
  answer: "",
  content: "",
  sourceCode: "",
  judgeConfig: {
    memoryLimit: 256,
    stackLimit: 128,
    timeLimit: 2000,
  },
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
});

const loadData = async () => {
  const id = route.query.id;
  if (!id) {
    return;
  }
  try {
    const res = await QuestionControllerService.getQuestionByIdUsingGet(id as any);
    if (res.code === 0) {
      form.value = res.data as any;
      if (!form.value.judgeCase) {
        form.value.judgeCase = [{ input: "", output: "" }];
      } else {
        form.value.judgeCase = JSON.parse(form.value.judgeCase as any);
      }
      if (!form.value.judgeConfig) {
        form.value.judgeConfig = { memoryLimit: 256, stackLimit: 128, timeLimit: 2000 };
      } else {
        form.value.judgeConfig = JSON.parse(form.value.judgeConfig as any);
      }
      if (!form.value.tags) {
        form.value.tags = [];
      } else {
        form.value.tags = JSON.parse(form.value.tags as any);
      }
    } else {
      message.error("加载失败，" + res.message);
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  }
};

onMounted(() => {
  loadData();
});

const doSubmit = async () => {
  if (!form.value.title) {
    message.error("请输入题目标题");
    return;
  }
  if (!form.value.content) {
    message.error("请输入题目描述");
    return;
  }
  if (!form.value.difficulty) {
    message.error("请选择难度等级");
    return;
  }

  submitting.value = true;
  try {
    if (updatePage) {
      const res = await QuestionControllerService.updateQuestionUsingPost(form.value);
      if (res.code === 0) {
        message.success("更新成功");
        router.push('/question/manage');
      } else {
        message.error("更新失败，" + res.message);
      }
    } else {
      const res = await QuestionControllerService.addQuestionUsingPost(form.value);
      if (res.code === 0) {
        message.success("创建成功");
        router.push('/question/manage');
      } else {
        message.error("创建失败，" + res.message);
      }
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  } finally {
    submitting.value = false;
  }
};

const handleAdd = () => {
  form.value.judgeCase.push({ input: "", output: "" });
};

const handleDelete = (index: number) => {
  form.value.judgeCase.splice(index, 1);
};

const resetForm = () => {
  form.value = {
    title: "",
    difficulty: "",
    tags: [],
    answer: "",
    content: "",
    sourceCode: "",
    judgeConfig: { memoryLimit: 256, stackLimit: 128, timeLimit: 2000 },
    judgeCase: [{ input: "", output: "" }],
  };
  message.success("表单已重置");
};

const previewQuestion = () => {
  message.info("预览功能开发中...");
};

const goBack = () => {
  router.push('/question/manage');
};

const onContentChange = (value: string) => {
  form.value.content = value;
};

const onAnswerChange = (value: string) => {
  form.value.answer = value;
};

const onSourceCodeChange = (value: string) => {
  form.value.sourceCode = value;
};
</script>

<style scoped>
#addQuestionView {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-6);
  background-color: var(--bg-page);
  min-height: 100vh;
  font-family: var(--font-body);
  animation: fadeIn 0.5s var(--ease-out);
  position: relative;
}

/* 背景纹理 */
#addQuestionView::before {
  content: '';
  position: fixed;
  inset: 0;
  background:
    radial-gradient(at 10% 30%, rgba(139, 92, 246, 0.04) 0px, transparent 50%),
    radial-gradient(at 90% 70%, rgba(59, 130, 246, 0.03) 0px, transparent 50%);
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

.preview-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
}

/* 表单容器 */
.form-container {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

/* 表单区域 */
.form-section {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  animation: fadeInUp 0.6s var(--ease-out) backwards;
}

/* 表单区域交错动画 */
.form-section:nth-of-type(1) { animation-delay: calc(var(--stagger-delay) * 2); }
.form-section:nth-of-type(2) { animation-delay: calc(var(--stagger-delay) * 3); }
.form-section:nth-of-type(3) { animation-delay: calc(var(--stagger-delay) * 4); }
.form-section:nth-of-type(4) { animation-delay: calc(var(--stagger-delay) * 5); }
.form-section:nth-of-type(5) { animation-delay: calc(var(--stagger-delay) * 6); }

.section-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  background: var(--bg-subtle);
  border-bottom: 1px solid var(--border-default);
}

.section-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  background: var(--color-primary-500);
}

.section-icon.content { background: var(--color-success); }
.section-icon.code { background: var(--color-accent-500); }
.section-icon.config { background: var(--color-warning); }
.section-icon.test { background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%); }

.section-title {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.section-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-5);
  background: var(--bg-subtle);
  border-bottom: 1px solid var(--border-default);
}

.section-header-left {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.case-count-badge {
  padding: 4px var(--space-3);
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 600;
}

.add-case-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.add-case-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.section-content {
  padding: var(--space-5);
}

/* 表单组 */
.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.form-label.required::after {
  content: '*';
  color: var(--color-error);
  margin-left: var(--space-1);
}

.form-input {
  width: 100%;
}

.form-input :deep(.arco-input),
.form-input :deep(.arco-select-view),
.form-input :deep(.arco-textarea) {
  border-radius: var(--radius-md);
}

/* 难度标签 */
.difficulty-tag {
  display: inline-block;
  padding: 4px var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: 600;
}

.difficulty-tag.easy { background: var(--color-success-bg); color: var(--color-success-text); }
.difficulty-tag.medium { background: var(--color-warning-bg); color: var(--color-warning-text); }
.difficulty-tag.hard { background: var(--color-error-bg); color: var(--color-error-text); }

/* 编辑器 */
.editor-wrapper {
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.editor-wrapper.code-editor {
  background: #1e293b;
}

/* 判题配置卡片 */
.config-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-4);
}

.config-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  background: var(--bg-subtle);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-default);
}

.config-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.config-icon.time { background: var(--color-warning-bg); color: var(--color-warning); }
.config-icon.memory { background: var(--color-info-bg); color: var(--color-info); }
.config-icon.stack { background: var(--color-success-bg); color: var(--color-success); }

.config-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.config-label {
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-secondary);
}

.config-input {
  width: 120px;
}

.config-unit {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 测试用例 */
.test-cases {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.test-case-card {
  background: var(--bg-subtle);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  transition: all var(--duration-fast);
}

.test-case-card:hover {
  border-color: var(--color-primary-200);
  box-shadow: var(--shadow-sm);
}

.case-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.case-number-badge {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.badge-number {
  width: 24px;
  height: 24px;
  background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
  color: white;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-xs);
  font-weight: 700;
}

.badge-text {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.delete-case-btn {
  color: var(--color-error);
}

.delete-case-btn:hover {
  background: var(--color-error-bg);
}

.case-textarea :deep(.arco-textarea) {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: var(--text-sm);
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-6);
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  animation: fadeInUp 0.6s var(--ease-out) calc(var(--stagger-delay) * 7) backwards;
}

.submit-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.39);
  transition: all var(--duration-fast);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.5);
}

.submit-btn:active {
  transform: translateY(0) scale(0.98);
}

.reset-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
}

.back-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 900px) {
  .config-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  #addQuestionView {
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

  .form-actions {
    flex-direction: column;
  }

  .section-header-row {
    flex-direction: column;
    gap: var(--space-3);
  }

  .section-header-left {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>