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
            <a-form-item label="用户名" name="userName">
              <a-input
                v-model="accountForm.userName"
                placeholder="请输入用户名"
                :maxlength="20"
                class="form-input"
              />
            </a-form-item>
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
          <!-- 代码编辑器设置 -->
          <div class="preference-item">
            <div class="preference-info">
              <h4 class="preference-title">代码编辑器</h4>
              <p class="preference-desc">配置代码编辑器的显示选项（做题页面生效）</p>
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
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useStore } from "vuex";
import message from "@arco-design/web-vue/es/message";
import axios from "axios";
import {
  IconSettings,
  IconUser,
  IconLock,
  IconKey,
  IconPalette,
  IconCheck,
} from '@arco-design/web-vue/es/icon';

const store = useStore();

const accountForm = ref({
  userName: "",
  userProfile: "",
});

const passwordForm = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const preferences = ref({
  editorFontSize: 14,
  editorTheme: "vs-dark",
});

const accountLoading = ref(false);
const passwordLoading = ref(false);

onMounted(() => {
  loadUserSettings();
});

const loadUserSettings = () => {
  const loginUser = store.state.user.loginUser;
  if (loginUser) {
    accountForm.value = {
      userName: loginUser.userName || "",
      userProfile: loginUser.userProfile || "",
    };
  }

  const savedPreferences = localStorage.getItem("userPreferences");
  if (savedPreferences) {
    try {
      const saved = JSON.parse(savedPreferences);
      if (saved.editorFontSize) preferences.value.editorFontSize = saved.editorFontSize;
      if (saved.editorTheme) preferences.value.editorTheme = saved.editorTheme;
    } catch (error) {
      console.error("加载偏好设置失败:", error);
    }
  }
};

const updateAccount = async () => {
  accountLoading.value = true;
  try {
    const res = await axios.post("/api/user/update/my", {
      userName: accountForm.value.userName,
      userProfile: accountForm.value.userProfile,
    });
    if (res.data.code === 0) {
      message.success("个人信息更新成功");
      await store.dispatch("user/getLoginUser");
    } else {
      message.error(res.data.message || "更新失败");
    }
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

  if (passwordForm.value.newPassword.length < 8) {
    message.error("密码长度至少为8位");
    return;
  }

  passwordLoading.value = true;
  try {
    const res = await axios.post("/api/user/change/password", {
      oldPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword,
      confirmPassword: passwordForm.value.confirmPassword,
    });
    if (res.data.code === 0) {
      message.success("密码修改成功");
      passwordForm.value = {
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      };
    } else {
      message.error(res.data.message || "密码修改失败");
    }
  } catch (error: any) {
    const msg = error?.response?.data?.message || "密码修改失败，请稍后重试";
    message.error(msg);
  } finally {
    passwordLoading.value = false;
  }
};

const updateEditorSettings = () => {
  localStorage.setItem("userPreferences", JSON.stringify(preferences.value));
  message.success("编辑器设置已更新，重新打开做题页面后生效");
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

  .setting-row {
    flex-wrap: wrap;
  }
}
</style>