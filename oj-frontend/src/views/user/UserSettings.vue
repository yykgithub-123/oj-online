<template>
  <div id="userSettings">
    <div class="settings-container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="title-section">
            <div class="title-icon-wrapper">
              <icon-settings />
            </div>
            <div class="title-text">
              <h1 class="page-title">个人设置</h1>
              <p class="page-subtitle">管理您的账户设置和偏好</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 账户设置 -->
      <div class="section-card">
        <div class="section-header">
          <div class="section-icon account">
            <icon-user />
          </div>
          <h3 class="section-title">账户设置</h3>
        </div>
        <div class="section-content">
          <a-form :model="accountForm" layout="vertical" @submit="updateAccount" class="settings-form">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="用户名" name="userName">
                  <a-input
                    v-model="accountForm.userName"
                    placeholder="请输入用户名"
                    :maxlength="20"
                    class="form-input"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱" name="userEmail">
                  <a-input
                    v-model="accountForm.userEmail"
                    placeholder="请输入邮箱"
                    type="email"
                    class="form-input"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item label="个人简介" name="userProfile">
              <a-textarea
                v-model="accountForm.userProfile"
                placeholder="请输入个人简介"
                :rows="4"
                :maxlength="200"
                show-count
                class="form-input"
              />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="accountLoading" class="submit-btn">
                <icon-check />
                保存账户设置
              </a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>

      <!-- 密码设置 -->
      <div class="section-card">
        <div class="section-header">
          <div class="section-icon password">
            <icon-lock />
          </div>
          <h3 class="section-title">密码设置</h3>
        </div>
        <div class="section-content">
          <a-form :model="passwordForm" layout="vertical" @submit="updatePassword" class="settings-form">
            <a-row :gutter="24">
              <a-col :span="8">
                <a-form-item label="当前密码" name="currentPassword">
                  <a-input-password
                    v-model="passwordForm.currentPassword"
                    placeholder="请输入当前密码"
                    class="form-input"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="新密码" name="newPassword">
                  <a-input-password
                    v-model="passwordForm.newPassword"
                    placeholder="请输入新密码"
                    class="form-input"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="确认新密码" name="confirmPassword">
                  <a-input-password
                    v-model="passwordForm.confirmPassword"
                    placeholder="请再次输入新密码"
                    class="form-input"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="passwordLoading" class="submit-btn">
                <icon-key />
                修改密码
              </a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>

      <!-- 偏好设置 -->
      <div class="section-card">
        <div class="section-header">
          <div class="section-icon preference">
            <icon-palette />
          </div>
          <h3 class="section-title">偏好设置</h3>
        </div>
        <div class="section-content">
          <!-- 主题设置 -->
          <div class="preference-item">
            <div class="preference-info">
              <h4 class="preference-title">主题设置</h4>
              <p class="preference-desc">选择您喜欢的界面主题</p>
            </div>
            <div class="preference-control">
              <div class="theme-options">
                <div
                  class="theme-option"
                  :class="{ active: preferences.theme === 'light' }"
                  @click="updateTheme('light')"
                >
                  <icon-sun />
                  <span>浅色</span>
                </div>
                <div
                  class="theme-option"
                  :class="{ active: preferences.theme === 'dark' }"
                  @click="updateTheme('dark')"
                >
                  <icon-moon />
                  <span>深色</span>
                </div>
                <div
                  class="theme-option"
                  :class="{ active: preferences.theme === 'auto' }"
                  @click="updateTheme('auto')"
                >
                  <icon-desktop />
                  <span>自动</span>
                </div>
              </div>
            </div>
          </div>

          <a-divider />

          <!-- 语言设置 -->
          <div class="preference-item">
            <div class="preference-info">
              <h4 class="preference-title">语言设置</h4>
              <p class="preference-desc">选择界面显示语言</p>
            </div>
            <div class="preference-control">
              <a-select v-model="preferences.language" style="width: 160px" @change="updateLanguage" class="form-select">
                <a-option value="zh-CN">简体中文</a-option>
                <a-option value="en-US">English</a-option>
              </a-select>
            </div>
          </div>

          <a-divider />

          <!-- 代码编辑器设置 -->
          <div class="preference-item">
            <div class="preference-info">
              <h4 class="preference-title">代码编辑器</h4>
              <p class="preference-desc">配置代码编辑器的显示选项</p>
            </div>
            <div class="preference-control">
              <div class="editor-settings">
                <div class="setting-row">
                  <span class="setting-label">字体大小</span>
                  <a-slider
                    v-model="preferences.editorFontSize"
                    :min="12"
                    :max="24"
                    :step="1"
                    style="width: 120px"
                    @change="updateEditorSettings"
                  />
                  <span class="setting-value">{{ preferences.editorFontSize }}px</span>
                </div>
                <div class="setting-row">
                  <span class="setting-label">代码主题</span>
                  <a-select
                    v-model="preferences.editorTheme"
                    style="width: 160px"
                    @change="updateEditorSettings"
                    class="form-select"
                  >
                    <a-option value="vs">Visual Studio</a-option>
                    <a-option value="vs-dark">VS Dark</a-option>
                    <a-option value="hc-black">High Contrast</a-option>
                  </a-select>
                </div>
              </div>
            </div>
          </div>

          <a-divider />

          <!-- 通知设置 -->
          <div class="preference-item">
            <div class="preference-info">
              <h4 class="preference-title">通知设置</h4>
              <p class="preference-desc">管理您接收的通知类型</p>
            </div>
            <div class="preference-control">
              <div class="notification-settings">
                <div class="notification-item">
                  <a-switch v-model="preferences.notifications.email" @change="updateNotifications" />
                  <span class="notification-label">
                    <icon-email />
                    邮件通知
                  </span>
                </div>
                <div class="notification-item">
                  <a-switch v-model="preferences.notifications.browser" @change="updateNotifications" />
                  <span class="notification-label">
                    <icon-notification />
                    浏览器通知
                  </span>
                </div>
                <div class="notification-item">
                  <a-switch v-model="preferences.notifications.contest" @change="updateNotifications" />
                  <span class="notification-label">
                    <icon-trophy />
                    比赛提醒
                  </span>
                </div>
                <div class="notification-item">
                  <a-switch v-model="preferences.notifications.solution" @change="updateNotifications" />
                  <span class="notification-label">
                    <icon-book />
                    题解更新
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据管理 -->
      <div class="section-card">
        <div class="section-header">
          <div class="section-icon data">
            <icon-storage />
          </div>
          <h3 class="section-title">数据管理</h3>
        </div>
        <div class="section-content">
          <!-- 导出数据 -->
          <div class="data-item">
            <div class="data-info">
              <h4 class="data-title">
                <icon-download />
                导出数据
              </h4>
              <p class="data-desc">导出您的提交记录和个人数据</p>
            </div>
            <div class="data-control">
              <a-button @click="exportData" :loading="exportLoading" class="action-btn export">
                <icon-download />
                导出数据
              </a-button>
            </div>
          </div>

          <a-divider />

          <!-- 清除缓存 -->
          <div class="data-item">
            <div class="data-info">
              <h4 class="data-title">
                <icon-clear />
                清除缓存
              </h4>
              <p class="data-desc">清除本地缓存数据，可能会提高性能</p>
            </div>
            <div class="data-control">
              <a-button @click="clearCache" :loading="cacheLoading" class="action-btn clear">
                <icon-clear />
                清除缓存
              </a-button>
            </div>
          </div>

          <a-divider />

          <!-- 删除账户 -->
          <div class="data-item danger">
            <div class="data-info">
              <h4 class="data-title">
                <icon-delete />
                删除账户
              </h4>
              <p class="data-desc">永久删除您的账户和所有相关数据</p>
            </div>
            <div class="data-control">
              <a-button status="danger" @click="showDeleteConfirm" class="action-btn delete">
                <icon-delete />
                删除账户
              </a-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useStore } from "vuex";
import { Modal } from "@arco-design/web-vue";
import message from "@arco-design/web-vue/es/message";
import {
  IconSettings,
  IconUser,
  IconLock,
  IconKey,
  IconPalette,
  IconSun,
  IconMoon,
  IconDesktop,
  IconEmail,
  IconNotification,
  IconTrophy,
  IconBook,
  IconStorage,
  IconDownload,
  IconClear,
  IconDelete,
  IconCheck,
} from '@arco-design/web-vue/es/icon';

const store = useStore();

const accountForm = ref({
  userName: "",
  userEmail: "",
  userProfile: "",
});

const passwordForm = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const preferences = ref({
  theme: "light",
  language: "zh-CN",
  editorFontSize: 14,
  editorTheme: "vs",
  notifications: {
    email: true,
    browser: true,
    contest: true,
    solution: false,
  },
});

const accountLoading = ref(false);
const passwordLoading = ref(false);
const exportLoading = ref(false);
const cacheLoading = ref(false);

onMounted(() => {
  loadUserSettings();
});

const loadUserSettings = () => {
  const loginUser = store.state.user.loginUser;
  if (loginUser) {
    accountForm.value = {
      userName: loginUser.userName || "",
      userEmail: loginUser.userEmail || "",
      userProfile: loginUser.userProfile || "",
    };
  }

  const savedPreferences = localStorage.getItem("userPreferences");
  if (savedPreferences) {
    try {
      preferences.value = { ...preferences.value, ...JSON.parse(savedPreferences) };
    } catch (error) {
      console.error("加载偏好设置失败:", error);
    }
  }
};

const updateAccount = async () => {
  accountLoading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 1000));
    message.success("账户设置更新成功");
  } catch (error) {
    message.error("更新失败，请稍后重试");
  } finally {
    accountLoading.value = false;
  }
};

const updatePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    message.error("两次输入的密码不一致");
    return;
  }

  if (passwordForm.value.newPassword.length < 6) {
    message.error("密码长度至少为6位");
    return;
  }

  passwordLoading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 1000));
    message.success("密码修改成功");
    passwordForm.value = {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    };
  } catch (error) {
    message.error("密码修改失败，请稍后重试");
  } finally {
    passwordLoading.value = false;
  }
};

const updateTheme = (value: string) => {
  preferences.value.theme = value;
  savePreferences();
  const themeNames = { light: '浅色', dark: '深色', auto: '自动' };
  message.success(`已切换到${themeNames[value]}主题`);
};

const updateLanguage = (value: string) => {
  preferences.value.language = value;
  savePreferences();
  message.success(`语言已切换到${value === 'zh-CN' ? '简体中文' : 'English'}`);
};

const updateEditorSettings = () => {
  savePreferences();
  message.success("编辑器设置已更新");
};

const updateNotifications = () => {
  savePreferences();
  message.success("通知设置已更新");
};

const savePreferences = () => {
  localStorage.setItem("userPreferences", JSON.stringify(preferences.value));
};

const exportData = async () => {
  exportLoading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 2000));
    message.success("数据导出成功，请检查下载文件");
  } catch (error) {
    message.error("数据导出失败，请稍后重试");
  } finally {
    exportLoading.value = false;
  }
};

const clearCache = async () => {
  cacheLoading.value = true;
  try {
    localStorage.removeItem("questionCache");
    localStorage.removeItem("submissionCache");
    await new Promise(resolve => setTimeout(resolve, 1000));
    message.success("缓存清除成功");
  } catch (error) {
    message.error("缓存清除失败");
  } finally {
    cacheLoading.value = false;
  }
};

const showDeleteConfirm = () => {
  Modal.confirm({
    title: "确认删除账户",
    content: "此操作将永久删除您的账户和所有相关数据，且无法恢复。请确认您要继续吗？",
    okText: "确认删除",
    cancelText: "取消",
    okButtonProps: { danger: true },
    onOk: async () => {
      try {
        await new Promise(resolve => setTimeout(resolve, 1000));
        message.success("账户删除成功");
        store.dispatch("user/logout");
      } catch (error) {
        message.error("账户删除失败，请稍后重试");
      }
    },
  });
};
</script>

<style scoped>
#userSettings {
  background-color: var(--bg-page);
  min-height: 100vh;
  padding: var(--space-6) 0;
  font-family: var(--font-body);
  position: relative;
}

/* 背景纹理 */
#userSettings::before {
  content: '';
  position: fixed;
  inset: 0;
  background:
    radial-gradient(at 15% 25%, rgba(59, 130, 246, 0.05) 0px, transparent 50%),
    radial-gradient(at 85% 35%, rgba(139, 92, 246, 0.04) 0px, transparent 50%),
    radial-gradient(at 50% 75%, rgba(6, 182, 212, 0.03) 0px, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

.settings-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 var(--space-6);
  position: relative;
  z-index: 1;
}

/* 页面头部 */
.page-header {
  margin-bottom: var(--space-6);
  animation: fadeInUp 0.6s var(--ease-out) backwards;
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
  background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
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

/* 区块卡片 */
.section-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  margin-bottom: var(--space-5);
  overflow: hidden;
  animation: fadeInUp 0.6s var(--ease-out) backwards;
}

.section-card:nth-of-type(1) { animation-delay: calc(var(--stagger-delay) * 2); }
.section-card:nth-of-type(2) { animation-delay: calc(var(--stagger-delay) * 3); }
.section-card:nth-of-type(3) { animation-delay: calc(var(--stagger-delay) * 4); }
.section-card:nth-of-type(4) { animation-delay: calc(var(--stagger-delay) * 5); }

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
}

.section-icon.account { background: var(--color-primary-500); }
.section-icon.password { background: var(--color-warning); }
.section-icon.preference { background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%); }
.section-icon.data { background: var(--color-accent-500); }

.section-title {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.section-content {
  padding: var(--space-5);
}

/* 表单样式 */
.settings-form {
  max-width: 600px;
}

.form-input :deep(.arco-input),
.form-input :deep(.arco-input-password),
.form-input :deep(.arco-textarea) {
  border-radius: var(--radius-md);
}

.form-select :deep(.arco-select-view) {
  border-radius: var(--radius-md);
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

/* 偏好设置 */
.preference-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: var(--space-4) 0;
}

.preference-info {
  flex: 1;
}

.preference-title {
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.preference-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

.preference-control {
  flex-shrink: 0;
  margin-left: var(--space-6);
}

/* 主题选项 */
.theme-options {
  display: flex;
  gap: var(--space-2);
}

.theme-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-3) var(--space-4);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--duration-fast);
  background: var(--bg-card);
  min-width: 70px;
}

.theme-option:hover {
  border-color: var(--color-primary-300);
  background: var(--color-primary-50);
}

.theme-option.active {
  border-color: var(--color-primary-500);
  background: var(--color-primary-50);
  color: var(--color-primary-600);
}

.theme-option span {
  font-size: var(--text-xs);
  font-weight: 500;
}

/* 编辑器设置 */
.editor-settings {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.setting-row {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.setting-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  min-width: 70px;
}

.setting-value {
  font-size: var(--text-sm);
  color: var(--text-primary);
  font-weight: 500;
  min-width: 40px;
}

/* 通知设置 */
.notification-settings {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-3);
}

.notification-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.notification-label {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-primary);
}

/* 数据管理 */
.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) 0;
}

.data-item.danger .data-title {
  color: var(--color-error);
}

.data-info {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.data-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.data-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

.data-control {
  flex-shrink: 0;
}

.action-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
  transition: all var(--duration-fast);
}

.action-btn:hover {
  transform: translateY(-1px);
}

.action-btn:active {
  transform: translateY(0) scale(0.98);
}

.action-btn.export {
  background: var(--color-primary-50);
  border-color: var(--color-primary-200);
  color: var(--color-primary-600);
}

.action-btn.clear {
  background: var(--color-warning-bg);
  border-color: var(--color-warning-border);
  color: var(--color-warning-text);
}

.action-btn.delete {
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

/* 响应式设计 */
@media (max-width: 700px) {
  #userSettings {
    padding: var(--space-4) 0;
  }

  .settings-container {
    padding: 0 var(--space-4);
  }

  .header-content {
    padding: var(--space-4);
  }

  .title-section {
    flex-direction: column;
  }

  .preference-item {
    flex-direction: column;
    gap: var(--space-3);
  }

  .preference-control {
    margin-left: 0;
  }

  .theme-options {
    width: 100%;
    justify-content: stretch;
  }

  .theme-option {
    flex: 1;
  }

  .notification-settings {
    grid-template-columns: 1fr;
  }

  .data-item {
    flex-direction: column;
    gap: var(--space-3);
    text-align: center;
  }

  .data-info {
    flex-direction: column;
  }

  .setting-row {
    flex-wrap: wrap;
  }
}
</style>