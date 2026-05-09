<template>
  <div id="noAuthView">
    <div class="no-auth-container">
      <div class="no-auth-icon">
        <icon-lock />
      </div>
      <h1 class="no-auth-title">无访问权限</h1>
      <p class="no-auth-message">您没有权限访问此页面，请登录或联系管理员。</p>
      <div class="no-auth-actions">
        <a-button type="primary" @click="goLogin">登录</a-button>
        <a-button @click="goHome">返回首页</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { IconLock } from '@arco-design/web-vue/es/icon';

const router = useRouter();

const goLogin = () => {
  router.push('/user/login');
};

const goHome = () => {
  router.push('/');
};
</script>

<style scoped>
#noAuthView {
  min-height: 100vh;
  background: var(--bg-page);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-5);
}

.no-auth-container {
  max-width: 400px;
  text-align: center;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-10);
  border: 1px solid var(--border-default);
}

.no-auth-icon {
  font-size: 48px;
  color: var(--color-warning);
  margin-bottom: var(--space-4);
}

.no-auth-title {
  font-size: var(--text-xl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-3);
}

.no-auth-message {
  font-size: var(--text-base);
  color: var(--text-secondary);
  margin-bottom: var(--space-6);
  line-height: 1.5;
}

.no-auth-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: center;
}
</style>
