<template>
  <div id="adminView">
    <div class="admin-container">
      <div class="admin-header">
        <h1 class="admin-title">管理员可见</h1>
        <p class="admin-subtitle">欢迎来到管理员专属页面</p>
      </div>
      <div class="admin-content">
        <img alt="Vue logo" src="../assets/logo.png" class="logo" />
        <HelloWorld msg="Welcome to Your Vue.js + TypeScript App" />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import HelloWorld from "@/components/HelloWorld.vue";

export default defineComponent({
  name: "AdminView",
  components: {
    HelloWorld,
  },
});
</script>

<style scoped>
#adminView {
  min-height: 100vh;
  background: var(--bg-page);
  padding: var(--space-5);
}

.admin-container {
  max-width: 800px;
  margin: 0 auto;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  border: 1px solid var(--border-default);
}

.admin-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.admin-title {
  font-size: var(--text-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.admin-subtitle {
  font-size: var(--text-base);
  color: var(--text-secondary);
}

.admin-content {
  text-align: center;
}

.logo {
  width: 120px;
  margin-bottom: var(--space-4);
}
</style>
