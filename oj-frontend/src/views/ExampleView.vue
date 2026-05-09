<template>
  <div id="exampleView">
    <div class="example-container">
      <div class="example-header">
        <h1 class="example-title">编辑器示例</h1>
        <p class="example-subtitle">代码编辑器和Markdown编辑器演示</p>
      </div>
      <div class="editor-section">
        <h2>代码编辑器</h2>
        <CodeEditor :value="codeValue" :handle-change="onCodeChange" />
      </div>
      <div class="editor-section">
        <h2>Markdown编辑器</h2>
        <MdEditor :value="mdValue" :handle-change="onMdChange" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import CodeEditor from "@/components/CodeEditor.vue";

const mdValue = ref();
const codeValue = ref();

const onMdChange = (v: string) => {
  mdValue.value = v;
};

const onCodeChange = (v: string) => {
  codeValue.value = v;
};
</script>

<style scoped>
#exampleView {
  min-height: 100vh;
  background: var(--bg-page);
  padding: var(--space-5);
}

.example-container {
  max-width: 1000px;
  margin: 0 auto;
}

.example-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.example-title {
  font-size: var(--text-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.example-subtitle {
  font-size: var(--text-base);
  color: var(--text-secondary);
}

.editor-section {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  margin-bottom: var(--space-6);
  border: 1px solid var(--border-default);
}

.editor-section h2 {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}
</style>
