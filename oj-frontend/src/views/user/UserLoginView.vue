<template>
  <div class="login-page">
    <!-- 简洁背景 -->
    <div class="bg-pattern"></div>

    <!-- 主内容区 -->
    <div class="login-wrapper">
      <!-- 左侧品牌区 -->
      <div class="brand-panel">
        <div class="brand-header">
          <div class="logo-box">
            <img src="@/assets/yake-logo.svg" alt="亚克OJ" class="logo" />
          </div>
          <div class="brand-text">
            <h1 class="brand-name">亚克OJ</h1>
            <p class="brand-desc">在线判题系统</p>
          </div>
        </div>

        <!-- 代码展示 -->
        <div class="code-box">
          <div class="code-header">
            <span class="file-name">hello.java</span>
          </div>
          <div class="code-content">
            <pre><code><span class="keyword">public class</span> <span class="class-name">Solution</span> {
  <span class="keyword">public</span> <span class="type">int</span> <span class="method">solve</span>() {
    <span class="keyword">return</span> <span class="number">42</span>;
  }
}</code></pre>
          </div>
        </div>

        <!-- 统计数据 -->
        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-value">1,200+</span>
            <span class="stat-label">题目</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">50K+</span>
            <span class="stat-label">用户</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">100M+</span>
            <span class="stat-label">提交</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-panel">
        <div class="form-card">
          <div class="form-header">
            <h2 class="form-title">登录</h2>
            <p class="form-subtitle">欢迎回来</p>
          </div>

          <a-form
            class="login-form"
            layout="vertical"
            :model="form"
            @submit="handleSubmit"
          >
            <a-form-item field="userAccount" hide-label>
              <a-input
                v-model="form.userAccount"
                placeholder="账号"
                size="large"
              >
                <template #prefix>
                  <icon-user />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item field="userPassword" hide-label>
              <a-input-password
                v-model="form.userPassword"
                placeholder="密码"
                size="large"
              >
                <template #prefix>
                  <icon-lock />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item hide-label>
              <a-button
                type="primary"
                html-type="submit"
                long
                size="large"
                class="submit-btn"
              >
                登录
              </a-button>
            </a-form-item>

            <div class="form-footer">
              <span class="footer-text">还没有账号？</span>
              <a-link @click="goRegister" class="footer-link">立即注册</a-link>
            </div>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { IconUser, IconLock } from "@arco-design/web-vue/es/icon";

const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const router = useRouter();
const store = useStore();

const handleSubmit = async () => {
  const res = await UserControllerService.userLoginUsingPost(form);
  if (res.code === 0) {
    await store.dispatch("user/getLoginUser");
    router.push({ path: "/", replace: true });
  } else {
    message.error("登录失败，" + res.message);
  }
};

const goRegister = () => {
  router.push("/user/register");
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  font-family: var(--font-body);
}

/* 背景图案 */
.bg-pattern {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at 1px 1px, #e2e8f0 1px, transparent 0);
  background-size: 24px 24px;
  opacity: 0.6;
}

/* 主内容布局 */
.login-wrapper {
  display: flex;
  gap: var(--space-10);
  max-width: 880px;
  width: 100%;
  padding: var(--space-6);
  position: relative;
  z-index: 10;
}

/* 左侧品牌区 */
.brand-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

.brand-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.logo-box {
  width: 44px;
  height: 44px;
  background: var(--color-primary-500);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo {
  width: 28px;
  height: 28px;
  filter: brightness(0) invert(1);
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  font-size: var(--text-xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  font-family: var(--font-display);
}

.brand-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

/* 代码展示框 */
.code-box {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.code-header {
  background: #f1f5f9;
  padding: var(--space-2) var(--space-3);
  border-bottom: 1px solid var(--border-default);
}

.file-name {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  font-family: var(--font-mono);
}

.code-content {
  padding: var(--space-4);
  font-family: var(--font-mono);
  font-size: var(--text-sm);
  line-height: 1.6;
}

.code-content code {
  color: var(--text-primary);
}

.keyword { color: #7c3aed; }
.class-name { color: #2563eb; }
.type { color: #0891b2; }
.method { color: #059669; }
.number { color: #dc2626; }

/* 统计数据 */
.stats-row {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--color-primary-500);
  font-family: var(--font-display);
}

.stat-label {
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.stat-divider {
  width: 1px;
  height: 24px;
  background: var(--border-default);
}

/* 右侧表单 */
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
}

.form-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-8);
  width: 100%;
  max-width: 360px;
  box-shadow: var(--shadow-sm);
}

.form-header {
  margin-bottom: var(--space-6);
  text-align: center;
}

.form-title {
  font-size: var(--text-xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  font-family: var(--font-display);
}

.form-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

/* 表单样式 */
.login-form :deep(.arco-form-item) {
  margin-bottom: var(--space-4);
}

.login-form :deep(.arco-input-wrapper) {
  border-radius: var(--radius-md);
}

.login-form :deep(.arco-input-prefix) {
  color: var(--text-secondary);
}

/* 提交按钮 */
.submit-btn {
  border-radius: var(--radius-md);
  font-weight: 500;
  height: 40px;
  transition: transform 0.15s, box-shadow 0.15s;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 表单底部 */
.form-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  margin-top: var(--space-4);
}

.footer-text {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.footer-link {
  font-size: var(--text-sm);
  color: var(--color-primary-500);
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    gap: var(--space-6);
    padding: var(--space-4);
  }

  .brand-panel {
    align-items: center;
    text-align: center;
  }

  .brand-header {
    flex-direction: column;
    gap: var(--space-2);
  }

  .stats-row {
    justify-content: center;
  }

  .code-box {
    width: 100%;
    max-width: 300px;
  }

  .form-card {
    max-width: 100%;
  }
}
</style>