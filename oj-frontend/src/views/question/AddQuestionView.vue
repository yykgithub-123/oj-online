<template>
  <div id="addQuestionView">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <a-icon type="plus-circle" class="title-icon" />
        {{ updatePage ? '编辑题目' : '创建题目' }}
      </h2>
      <p class="page-description">{{ updatePage ? '修改题目信息和配置' : '创建新的编程题目' }}</p>
    </div>

    <a-card class="form-card" :bordered="false">
      <a-form :model="form" label-align="left" layout="vertical" class="question-form">
        <!-- 基本信息 -->
        <div class="form-section">
          <h3 class="section-title">
            <a-icon type="info-circle" />
            基本信息
          </h3>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item field="title" label="题目标题" :rules="[{ required: true, message: '请输入题目标题' }]">
                <a-input 
                  v-model="form.title" 
                  placeholder="请输入题目标题" 
                  allow-clear
                  class="form-input"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item field="difficulty" label="难度等级" :rules="[{ required: true, message: '请选择难度等级' }]">
                <a-select 
                  v-model="form.difficulty" 
                  placeholder="请选择难度等级"
                  class="form-input"
                >
                  <a-option value="简单">简单</a-option>
                  <a-option value="中等">中等</a-option>
                  <a-option value="困难">困难</a-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item field="tags" label="标签">
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
          </a-form-item>
        </div>

        <!-- 题目内容 -->
        <div class="form-section">
          <h3 class="section-title">
            <a-icon type="file-text" />
            题目内容
          </h3>
          <a-form-item field="content" label="题目描述" :rules="[{ required: true, message: '请输入题目描述' }]">
            <MdEditor :value="form.content" :handle-change="onContentChange" />
          </a-form-item>
        </div>

        <!-- 答案和代码 -->
        <div class="form-section">
          <h3 class="section-title">
            <a-icon type="code" />
            答案和代码
          </h3>
          <a-form-item field="answer" label="题目答案">
            <MdEditor :value="form.answer" :handle-change="onAnswerChange" />
          </a-form-item>
          <a-form-item field="sourceCode" label="初始代码">
            <MdEditor :value="form.sourceCode" :handle-change="onSourceCodeChange" />
          </a-form-item>
        </div>

        <!-- 判题配置 -->
        <div class="form-section">
          <h3 class="section-title">
            <a-icon type="setting" />
            判题配置
          </h3>
          <a-row :gutter="16">
            <a-col :span="8">
              <a-form-item field="judgeConfig.timeLimit" label="时间限制 (ms)">
                <a-input-number
                  v-model="form.judgeConfig.timeLimit"
                  placeholder="请输入时间限制"
                  mode="button"
                  min="100"
                  max="10000"
                  size="large"
                  class="form-input"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item field="judgeConfig.memoryLimit" label="内存限制 (KB)">
                <a-input-number
                  v-model="form.judgeConfig.memoryLimit"
                  placeholder="请输入内存限制"
                  mode="button"
                  min="64"
                  max="512"
                  size="large"
                  class="form-input"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item field="judgeConfig.stackLimit" label="堆栈限制 (KB)">
                <a-input-number
                  v-model="form.judgeConfig.stackLimit"
                  placeholder="请输入堆栈限制"
                  mode="button"
                  min="64"
                  max="512"
                  size="large"
                  class="form-input"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <!-- 测试用例 -->
        <div class="form-section">
          <div class="section-header">
            <h3 class="section-title">
              <a-icon type="experiment" />
              测试用例
            </h3>
            <a-button @click="handleAdd" type="outline" status="success" size="small">
              <template #icon>
                <a-icon type="plus" />
              </template>
              添加用例
            </a-button>
          </div>
          
          <div class="test-cases">
            <a-card 
              v-for="(judgeCaseItem, index) of form.judgeCase" 
              :key="index"
              class="test-case-card"
              :bordered="false"
            >
              <div class="case-header">
                <span class="case-number">测试用例 {{ index + 1 }}</span>
                <a-button 
                  status="danger" 
                  size="small" 
                  @click="handleDelete(index)"
                  v-if="form.judgeCase.length > 1"
                >
                  <template #icon>
                    <a-icon type="delete" />
                  </template>
                  删除
                </a-button>
              </div>
              
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item :field="`form.judgeCase[${index}].input`" label="输入">
                    <a-textarea
                      v-model="judgeCaseItem.input"
                      placeholder="请输入测试输入用例"
                      :rows="4"
                      class="form-input"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item :field="`form.judgeCase[${index}].output`" label="期望输出">
                    <a-textarea
                      v-model="judgeCaseItem.output"
                      placeholder="请输入测试输出用例"
                      :rows="4"
                      class="form-input"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
            </a-card>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <a-space>
            <a-button type="primary" size="large" @click="doSubmit" :loading="submitting">
              <template #icon>
                <a-icon type="check" />
              </template>
              {{ updatePage ? '更新题目' : '创建题目' }}
            </a-button>
            <a-button @click="resetForm" size="large">
              <template #icon>
                <a-icon type="reload" />
              </template>
              重置
            </a-button>
            <a-button @click="previewQuestion" size="large">
              <template #icon>
                <a-icon type="eye" />
              </template>
              预览
            </a-button>
          </a-space>
        </div>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const submitting = ref(false);

// 如果页面地址包含 update，视为更新页面
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

/**
 * 根据题目 id 获取老的数据
 */
const loadData = async () => {
  const id = route.query.id;
  if (!id) {
    return;
  }
  try {
    const res = await QuestionControllerService.getQuestionByIdUsingGet(
      id as any
    );
    if (res.code === 0) {
      form.value = res.data as any;
      // json 转 js 对象
      if (!form.value.judgeCase) {
        form.value.judgeCase = [
          {
            input: "",
            output: "",
          },
        ];
      } else {
        form.value.judgeCase = JSON.parse(form.value.judgeCase as any);
      }
      if (!form.value.judgeConfig) {
        form.value.judgeConfig = {
          memoryLimit: 256,
          stackLimit: 128,
          timeLimit: 2000,
        };
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
  // 表单验证
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
    // 区分更新还是创建
    if (updatePage) {
      const res = await QuestionControllerService.updateQuestionUsingPost(
        form.value
      );
      if (res.code === 0) {
        message.success("更新成功");
        router.push('/question/manage');
      } else {
        message.error("更新失败，" + res.message);
      }
    } else {
      const res = await QuestionControllerService.addQuestionUsingPost(
        form.value
      );
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

/**
 * 新增判题用例
 */
const handleAdd = () => {
  form.value.judgeCase.push({
    input: "",
    output: "",
  });
};

/**
 * 删除判题用例
 */
const handleDelete = (index: number) => {
  form.value.judgeCase.splice(index, 1);
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.value = {
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
  };
  message.success("表单已重置");
};

/**
 * 预览题目
 */
const previewQuestion = () => {
  message.info("预览功能开发中...");
  // 这里可以添加预览逻辑
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

/* 表单卡片样式 */
.form-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.question-form {
  padding: 0;
}

/* 表单区域样式 */
.form-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.section-title .arco-icon {
  margin-right: 8px;
  color: #165dff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

/* 表单输入样式 */
.form-input {
  width: 100%;
}

/* 测试用例样式 */
.test-cases {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.test-case-card {
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  background-color: #fafafa;
}

.case-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e6eb;
}

.case-number {
  font-weight: 600;
  color: #1d2129;
}

/* 操作按钮样式 */
.form-actions {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 500px) {
  #addQuestionView {
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
