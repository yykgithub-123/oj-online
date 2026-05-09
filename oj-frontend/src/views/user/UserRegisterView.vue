<template>
  <div class="register-page">
    <!-- 左侧视觉区 -->
    <div class="visual-panel">
      <div class="visual-bg"></div>
      <div class="visual-content">
        <div class="brand-section">
          <div class="logo-box">
            <img src="@/assets/yake-logo.svg" alt="亚克OJ" class="logo" />
          </div>
          <h1 class="brand-name">亚克OJ</h1>
          <p class="brand-slogan">加入我们，开启算法之旅</p>
        </div>

        <div class="code-box">
          <div class="code-header">
            <div class="code-dots">
              <span class="dot red"></span>
              <span class="dot yellow"></span>
              <span class="dot green"></span>
            </div>
            <span class="file-name">Yyk.java</span>
          </div>
          <div class="code-content">
            <pre><code><span class="keyword">public class</span> <span class="class-name">Yyk</span> {
  <span class="keyword">public int</span> <span class="method">maxSubArray</span>(<span class="keyword">int</span>[] nums) {
    <span class="keyword">int</span> max = nums[<span class="number">0</span>], cur = <span class="number">0</span>;
    <span class="keyword">for</span> (<span class="keyword">int</span> num : nums) {
      cur = Math.<span class="method">max</span>(cur + num, num);
      max = Math.<span class="method">max</span>(max, cur);
    }
    <span class="keyword">return</span> max;
  }
}</code></pre>
          </div>
          <div class="code-footer">
            <span class="code-author">@Arkyyk</span>
          </div>
        </div>

        <div class="features-grid">
          <div class="feature-card">
            <icon-code class="feature-icon" />
            <div class="feature-info">
              <span class="feature-title">Java语言支持</span>
              <span class="feature-desc">Java</span>
            </div>
          </div>
          <div class="feature-card">
            <icon-thunderbolt class="feature-icon" />
            <div class="feature-info">
              <span class="feature-title">实时判题</span>
              <span class="feature-desc">毫秒级编译执行反馈</span>
            </div>
          </div>
          <div class="feature-card">
            <icon-safe class="feature-icon" />
            <div class="feature-info">
              <span class="feature-title">安全沙箱</span>
              <span class="feature-desc">Docker 隔离运行环境</span>
            </div>
          </div>
        </div>

        <div class="visual-footer">
          <a href="http://www.isyyk.top" target="_blank">知识分享 by 程序员亚克</a>
        </div>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="form-panel">
      <div class="form-inner">
        <div class="form-header">
          <h2 class="form-title">创建账号</h2>
          <p class="form-subtitle">注册亚克OJ，开始你的编程之旅</p>
        </div>

        <a-form
          class="register-form"
          layout="vertical"
          :model="form"
          @submit="handleSubmit"
        >
          <a-form-item field="userAccount" hide-label>
            <a-input
              v-model="form.userAccount"
              placeholder="请输入账号"
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
              placeholder="请输入密码（至少8位）"
              size="large"
            >
              <template #prefix>
                <icon-lock />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item field="checkPassword" hide-label>
            <a-input-password
              v-model="form.checkPassword"
              placeholder="请再次确认密码"
              size="large"
            >
              <template #prefix>
                <icon-safe />
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
              注册
            </a-button>
          </a-form-item>

          <div class="form-footer">
            <span class="footer-text">已有账号？</span>
            <a-link @click="goLogin" class="footer-link">立即登录</a-link>
          </div>
        </a-form>

        <div class="register-tips">
          <div class="tip-item">
            <icon-check-circle class="tip-icon" />
            <span>账号长度至少4位</span>
          </div>
          <div class="tip-item">
            <icon-check-circle class="tip-icon" />
            <span>密码长度至少8位</span>
          </div>
          <div class="tip-item">
            <icon-check-circle class="tip-icon" />
            <span>注册即可免费使用全部功能</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { UserControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import {
  IconUser,
  IconLock,
  IconSafe,
  IconCode,
  IconThunderbolt,
  IconCheckCircle,
} from "@arco-design/web-vue/es/icon";

const router = useRouter();

const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
} as API.UserRegisterRequest);

const handleSubmit = async () => {
  const res = await UserControllerService.userRegisterUsingPost(form);
  if (res.code === 0) {
    message.success("注册成功");
    router.push({ path: "/user/login", replace: true });
  } else {
    message.error("注册失败，" + res.message);
  }
};

const goLogin = () => {
  router.push("/user/login");
};
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  font-family: var(--font-body);
}

/* ========== 左侧视觉面板 ========== */
.visual-panel {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  min-height: 100vh;
}

.visual-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 50%, #1d4ed8 100%);
}

.visual-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at 1px 1px, rgba(255,255,255,0.06) 1px, transparent 0);
  background-size: 32px 32px;
}

.visual-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 80% 50%, rgba(139,92,246,0.25) 0%, transparent 60%),
    radial-gradient(ellipse at 20% 80%, rgba(6,182,212,0.2) 0%, transparent 50%);
}

.visual-content {
  position: relative;
  z-index: 1;
  padding: 48px;
  max-width: 520px;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 32px;
  animation: fadeInUp 0.6s var(--ease-out);
}

.brand-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
}

.logo-box {
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255,255,255,0.2);
}

.logo {
  width: 32px;
  height: 32px;
  filter: brightness(0) invert(1);
}

.brand-name {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
  font-family: var(--font-display);
}

.brand-slogan {
  font-size: 15px;
  color: rgba(255,255,255,0.7);
  margin: 0;
}

/* 代码展示框 */
.code-box {
  background: rgba(15,23,42,0.8);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 12px;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.code-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}

.code-dots {
  display: flex;
  gap: 6px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.red { background: #ef4444; }
.dot.yellow { background: #f59e0b; }
.dot.green { background: #22c55e; }

.file-name {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  font-family: var(--font-mono);
}

.code-content {
  padding: 16px;
  font-family: var(--font-mono);
  font-size: 12.5px;
  line-height: 1.7;
  overflow-x: auto;
}

.code-content code {
  color: #e2e8f0;
}

.keyword { color: #c084fc; }
.class-name { color: #60a5fa; }
.method { color: #34d399; }
.number { color: #fb923c; }

.code-footer {
  display: flex;
  justify-content: flex-end;
  padding: 6px 16px 10px;
  border-top: 1px solid rgba(255,255,255,0.06);
}

.code-author {
  font-size: 11px;
  color: rgba(255,255,255,0.3);
  font-family: var(--font-mono);
  font-style: italic;
}

/* 特性网格 */
.features-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px;
  backdrop-filter: blur(8px);
  transition: all 0.2s;
}

.feature-card:hover {
  background: rgba(255,255,255,0.12);
  border-color: rgba(255,255,255,0.18);
}

.feature-card .feature-icon {
  font-size: 20px;
  color: #60a5fa;
  flex-shrink: 0;
}

.feature-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.feature-title {
  font-size: 13px;
  font-weight: 600;
  color: #ffffff;
}

.feature-desc {
  font-size: 11px;
  color: rgba(255,255,255,0.5);
}

.visual-footer {
  margin-top: auto;
}

.visual-footer a {
  color: rgba(255,255,255,0.4);
  font-size: 12px;
  text-decoration: none;
  transition: color 0.2s;
}

.visual-footer a:hover {
  color: rgba(255,255,255,0.7);
}

/* ========== 右侧表单面板 ========== */
.form-panel {
  width: 480px;
  min-width: 480px;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  padding: 48px;
}

.form-inner {
  width: 100%;
  max-width: 360px;
  animation: fadeIn 0.5s var(--ease-out) 0.2s backwards;
}

.form-header {
  margin-bottom: 32px;
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* 表单样式 */
.register-form :deep(.arco-form-item) {
  margin-bottom: 16px;
}

.register-form :deep(.arco-input-wrapper) {
  border-radius: 8px;
  height: 44px;
  background: #f8fafc;
  border-color: #e2e8f0;
}

.register-form :deep(.arco-input-wrapper:hover) {
  border-color: var(--color-primary-400);
}

.register-form :deep(.arco-input-wrapper.arco-input-focus) {
  background: #ffffff;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 3px rgba(59,130,246,0.1);
}

.register-form :deep(.arco-input-prefix) {
  color: var(--text-tertiary);
}

.submit-btn {
  border-radius: 8px;
  font-weight: 600;
  height: 44px;
  font-size: 15px;
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-primary-600) 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(59,130,246,0.35);
  transition: all 0.2s;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(59,130,246,0.45);
}

.submit-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(59,130,246,0.3);
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 16px;
}

.footer-text {
  font-size: 13px;
  color: var(--text-secondary);
}

.footer-link {
  font-size: 13px;
  color: var(--color-primary-500);
  font-weight: 600;
}

/* 注册提示 */
.register-tips {
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tip-icon {
  font-size: 14px;
  color: var(--color-success);
  flex-shrink: 0;
}

.tip-item span {
  font-size: 12px;
  color: var(--text-tertiary);
}

/* ========== 响应式 ========== */
@media (max-width: 960px) {
  .register-page {
    flex-direction: column;
  }

  .visual-panel {
    min-height: auto;
    padding: 40px 24px;
  }

  .visual-content {
    padding: 0;
    max-width: 100%;
    gap: 24px;
  }

  .code-box {
    display: none;
  }

  .brand-section {
    align-items: center;
    text-align: center;
  }

  .features-grid {
    display: none;
  }

  .form-panel {
    width: 100%;
    min-width: 100%;
    min-height: auto;
    padding: 32px 24px;
  }

  .form-inner {
    max-width: 100%;
  }
}
</style>