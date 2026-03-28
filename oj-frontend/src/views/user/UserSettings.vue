<template>
  <div id="userSettings">
    <div class="settings-container">
      <div class="settings-header">
        <h1>个人设置</h1>
        <p>管理您的账户设置和偏好</p>
      </div>

      <!-- 账户设置 -->
      <div class="settings-section">
        <div class="section-title">
          <h2>账户设置</h2>
        </div>
        <div class="settings-card">
          <a-form :model="accountForm" layout="vertical" @submit="updateAccount">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="用户名" name="userName">
                  <a-input 
                    v-model="accountForm.userName" 
                    placeholder="请输入用户名"
                    :maxlength="20"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱" name="userEmail">
                  <a-input 
                    v-model="accountForm.userEmail" 
                    placeholder="请输入邮箱"
                    type="email"
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
              />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="accountLoading">
                保存账户设置
              </a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>

      <!-- 密码设置 -->
      <div class="settings-section">
        <div class="section-title">
          <h2>密码设置</h2>
        </div>
        <div class="settings-card">
          <a-form :model="passwordForm" layout="vertical" @submit="updatePassword">
            <a-form-item label="当前密码" name="currentPassword">
              <a-input-password 
                v-model="passwordForm.currentPassword" 
                placeholder="请输入当前密码"
              />
            </a-form-item>
            <a-form-item label="新密码" name="newPassword">
              <a-input-password 
                v-model="passwordForm.newPassword" 
                placeholder="请输入新密码"
              />
            </a-form-item>
            <a-form-item label="确认新密码" name="confirmPassword">
              <a-input-password 
                v-model="passwordForm.confirmPassword" 
                placeholder="请再次输入新密码"
              />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="passwordLoading">
                修改密码
              </a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>

      <!-- 偏好设置 -->
      <div class="settings-section">
        <div class="section-title">
          <h2>偏好设置</h2>
        </div>
        <div class="settings-card">
          <div class="preference-item">
            <div class="preference-label">
              <h3>主题设置</h3>
              <p>选择您喜欢的界面主题</p>
            </div>
            <div class="preference-control">
              <a-radio-group v-model="preferences.theme" @change="updateTheme">
                <a-radio value="light">浅色主题</a-radio>
                <a-radio value="dark">深色主题</a-radio>
                <a-radio value="auto">跟随系统</a-radio>
              </a-radio-group>
            </div>
          </div>

          <a-divider />

          <div class="preference-item">
            <div class="preference-label">
              <h3>语言设置</h3>
              <p>选择界面显示语言</p>
            </div>
            <div class="preference-control">
              <a-select v-model="preferences.language" style="width: 200px" @change="updateLanguage">
                <a-option value="zh-CN">简体中文</a-option>
                <a-option value="en-US">English</a-option>
              </a-select>
            </div>
          </div>

          <a-divider />

          <div class="preference-item">
            <div class="preference-label">
              <h3>代码编辑器设置</h3>
              <p>配置代码编辑器的显示选项</p>
            </div>
            <div class="preference-control">
              <div class="editor-settings">
                <div class="setting-row">
                  <span>字体大小：</span>
                  <a-slider 
                    v-model="preferences.editorFontSize" 
                    :min="12" 
                    :max="24" 
                    :step="1"
                    style="width: 200px"
                    @change="updateEditorSettings"
                  />
                  <span>{{ preferences.editorFontSize }}px</span>
                </div>
                <div class="setting-row">
                  <span>代码主题：</span>
                  <a-select 
                    v-model="preferences.editorTheme" 
                    style="width: 200px"
                    @change="updateEditorSettings"
                  >
                    <a-option value="vs">Visual Studio</a-option>
                    <a-option value="vs-dark">Visual Studio Dark</a-option>
                    <a-option value="hc-black">High Contrast</a-option>
                  </a-select>
                </div>
              </div>
            </div>
          </div>

          <a-divider />

          <div class="preference-item">
            <div class="preference-label">
              <h3>通知设置</h3>
              <p>管理您接收的通知类型</p>
            </div>
            <div class="preference-control">
              <div class="notification-settings">
                <div class="notification-item">
                  <a-switch v-model="preferences.notifications.email" @change="updateNotifications" />
                  <span>邮件通知</span>
                </div>
                <div class="notification-item">
                  <a-switch v-model="preferences.notifications.browser" @change="updateNotifications" />
                  <span>浏览器通知</span>
                </div>
                <div class="notification-item">
                  <a-switch v-model="preferences.notifications.contest" @change="updateNotifications" />
                  <span>比赛提醒</span>
                </div>
                <div class="notification-item">
                  <a-switch v-model="preferences.notifications.solution" @change="updateNotifications" />
                  <span>题解更新</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据管理 -->
      <div class="settings-section">
        <div class="section-title">
          <h2>数据管理</h2>
        </div>
        <div class="settings-card">
          <div class="data-item">
            <div class="data-label">
              <h3>导出数据</h3>
              <p>导出您的提交记录和个人数据</p>
            </div>
            <div class="data-control">
              <a-button @click="exportData" :loading="exportLoading">
                导出数据
              </a-button>
            </div>
          </div>

          <a-divider />

          <div class="data-item">
            <div class="data-label">
              <h3>清除缓存</h3>
              <p>清除本地缓存数据，可能会提高性能</p>
            </div>
            <div class="data-control">
              <a-button @click="clearCache" :loading="cacheLoading">
                清除缓存
              </a-button>
            </div>
          </div>

          <a-divider />

          <div class="data-item danger">
            <div class="data-label">
              <h3>删除账户</h3>
              <p>永久删除您的账户和所有相关数据</p>
            </div>
            <div class="data-control">
              <a-button danger @click="showDeleteConfirm">
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

const store = useStore();

// 表单数据
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

// 偏好设置
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

// 加载状态
const accountLoading = ref(false);
const passwordLoading = ref(false);
const exportLoading = ref(false);
const cacheLoading = ref(false);

onMounted(() => {
  loadUserSettings();
});

// 加载用户设置
const loadUserSettings = () => {
  const loginUser = store.state.user.loginUser;
  if (loginUser) {
    accountForm.value = {
      userName: loginUser.userName || "",
      userEmail: loginUser.userEmail || "",
      userProfile: loginUser.userProfile || "",
    };
  }

  // 从本地存储加载偏好设置
  const savedPreferences = localStorage.getItem("userPreferences");
  if (savedPreferences) {
    try {
      preferences.value = { ...preferences.value, ...JSON.parse(savedPreferences) };
    } catch (error) {
      console.error("加载偏好设置失败:", error);
    }
  }
};

// 更新账户设置
const updateAccount = async () => {
  accountLoading.value = true;
  try {
    // 这里应该调用后端API更新用户信息
    await new Promise(resolve => setTimeout(resolve, 1000)); // 模拟API调用
    message.success("账户设置更新成功");
  } catch (error) {
    message.error("更新失败，请稍后重试");
  } finally {
    accountLoading.value = false;
  }
};

// 更新密码
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
    // 这里应该调用后端API更新密码
    await new Promise(resolve => setTimeout(resolve, 1000)); // 模拟API调用
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

// 更新主题
const updateTheme = (value: string) => {
  preferences.value.theme = value;
  savePreferences();
  // 这里可以实际应用主题
  message.success(`已切换到${value === 'light' ? '浅色' : value === 'dark' ? '深色' : '自动'}主题`);
};

// 更新语言
const updateLanguage = (value: string) => {
  preferences.value.language = value;
  savePreferences();
  message.success(`语言已切换到${value === 'zh-CN' ? '简体中文' : 'English'}`);
};

// 更新编辑器设置
const updateEditorSettings = () => {
  savePreferences();
  message.success("编辑器设置已更新");
};

// 更新通知设置
const updateNotifications = () => {
  savePreferences();
  message.success("通知设置已更新");
};

// 保存偏好设置
const savePreferences = () => {
  localStorage.setItem("userPreferences", JSON.stringify(preferences.value));
};

// 导出数据
const exportData = async () => {
  exportLoading.value = true;
  try {
    // 这里应该调用后端API导出数据
    await new Promise(resolve => setTimeout(resolve, 2000)); // 模拟API调用
    message.success("数据导出成功，请检查下载文件");
  } catch (error) {
    message.error("数据导出失败，请稍后重试");
  } finally {
    exportLoading.value = false;
  }
};

// 清除缓存
const clearCache = async () => {
  cacheLoading.value = true;
  try {
    // 清除本地存储
    localStorage.removeItem("questionCache");
    localStorage.removeItem("submissionCache");
    await new Promise(resolve => setTimeout(resolve, 1000)); // 模拟清除过程
    message.success("缓存清除成功");
  } catch (error) {
    message.error("缓存清除失败");
  } finally {
    cacheLoading.value = false;
  }
};

// 显示删除确认对话框
const showDeleteConfirm = () => {
  Modal.confirm({
    title: "确认删除账户",
    content: "此操作将永久删除您的账户和所有相关数据，且无法恢复。请确认您要继续吗？",
    okText: "确认删除",
    cancelText: "取消",
    okButtonProps: { danger: true },
    onOk: async () => {
      try {
        // 这里应该调用后端API删除账户
        await new Promise(resolve => setTimeout(resolve, 1000)); // 模拟API调用
        message.success("账户删除成功");
        // 跳转到登录页面
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
  background-color: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;
}

.settings-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.settings-header {
  text-align: center;
  margin-bottom: 32px;
}

.settings-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.settings-header p {
  color: #666;
  font-size: 16px;
}

.settings-section {
  margin-bottom: 32px;
}

.section-title {
  margin-bottom: 16px;
}

.section-title h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.settings-card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 偏好设置样式 */
.preference-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 0;
}

.preference-label h3 {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 4px 0;
}

.preference-label p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.preference-control {
  flex-shrink: 0;
}

.editor-settings {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.setting-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-settings {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 数据管理样式 */
.data-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 0;
}

.data-item.danger .data-label h3 {
  color: #ff4d4f;
}

.data-label h3 {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 4px 0;
}

.data-label p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.data-control {
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 500px) {
  .preference-item,
  .data-item {
    flex-direction: column;
    gap: 12px;
  }
  
  .preference-control,
  .data-control {
    align-self: flex-start;
  }
  
  .editor-settings {
    width: 100%;
  }
  
  .setting-row {
    flex-wrap: wrap;
  }
}
</style>