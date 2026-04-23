<template>
  <div class="login-page">
    <!-- 左侧视觉区 -->
    <div class="visual-panel">
      <div class="visual-bg"></div>
      <div class="visual-content">
        <div class="brand-section">
          <div class="logo-box">
            <img src="@/assets/yake-logo.svg" alt="亚克OJ" class="logo" />
          </div>
          <h1 class="brand-name">亚克OJ</h1>
          <p class="brand-slogan">编程能力，从这里起飞</p>
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
  <span class="keyword">public int</span>[] <span class="method">twoSum</span>(<span class="keyword">int</span>[] nums, <span class="keyword">int</span> target) {
    Map&lt;Integer, Integer&gt; map = <span class="keyword">new</span> HashMap&lt;&gt;();
    <span class="keyword">for</span> (<span class="keyword">int</span> i = <span class="number">0</span>; i &lt; nums.length; i++) {
      <span class="keyword">int</span> complement = target - nums[i];
      <span class="keyword">if</span> (map.containsKey(complement)) {
        <span class="keyword">return new int</span>[]{map.get(complement), i};
      }
      map.put(nums[i], i);
    }
    <span class="keyword">return new int</span>[]{};
  }
}</code></pre>
          </div>
          <div class="code-footer">
            <span class="code-author">@Arkyyk</span>
          </div>
        </div>

        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-value">30+</span>
            <span class="stat-label">算法题目</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">30+</span>
            <span class="stat-label">最多活跃用户</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">100+</span>
            <span class="stat-label">代码提交</span>
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
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">登录你的亚克OJ账号</p>
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
              placeholder="请输入密码"
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

        <div class="form-divider">
          <span>或</span>
        </div>

        <div class="quick-features">
          <div class="feature-item">
            <icon-code class="feature-icon" />
            <span>Java语言在线编程</span>
          </div>
          <div class="feature-item">
            <icon-thunderbolt class="feature-icon" />
            <span>毫秒级判题反馈</span>
          </div>
          <div class="feature-item">
            <icon-trophy class="feature-icon" />
            <span>排行榜竞赛系统</span>
          </div>
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
import { IconUser, IconLock, IconCode, IconThunderbolt, IconTrophy } from "@arco-design/web-vue/es/icon";

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
    radial-gradient(ellipse at 20% 50%, rgba(59,130,246,0.3) 0%, transparent 60%),
    radial-gradient(ellipse at 80% 20%, rgba(6,182,212,0.2) 0%, transparent 50%);
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
  line-height: 1.5;
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

/* 统计数据 */
.stats-row {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  font-family: var(--font-display);
}

.stat-label {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255,255,255,0.15);
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
.login-form :deep(.arco-form-item) {
  margin-bottom: 16px;
}

.login-form :deep(.arco-input-wrapper) {
  border-radius: 8px;
  height: 44px;
  background: #f8fafc;
  border-color: #e2e8f0;
}

.login-form :deep(.arco-input-wrapper:hover) {
  border-color: var(--color-primary-400);
}

.login-form :deep(.arco-input-wrapper.arco-input-focus) {
  background: #ffffff;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 3px rgba(59,130,246,0.1);
}

.login-form :deep(.arco-input-prefix) {
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

/* 分割线 */
.form-divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 28px 0;
}

.form-divider::before,
.form-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}

.form-divider span {
  font-size: 12px;
  color: var(--text-tertiary);
}

/* 特性列表 */
.quick-features {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #f1f5f9;
  transition: all 0.2s;
}

.feature-item:hover {
  background: #eff6ff;
  border-color: #dbeafe;
}

.feature-icon {
  color: var(--color-primary-500);
  font-size: 16px;
}

.feature-item span {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}

/* ========== 响应式 ========== */
@media (max-width: 960px) {
  .login-page {
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

  .stats-row {
    justify-content: center;
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