<template>
  <div id="userProfile">
    <div class="profile-container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="title-section">
            <div class="title-icon-wrapper">
              <icon-user />
            </div>
            <div class="title-text">
              <h1 class="page-title">个人中心</h1>
              <p class="page-subtitle">管理您的个人信息和学习进度</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 个人信息卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <a-avatar :size="100" class="user-avatar">
                <img v-if="avatarUrl" :src="avatarUrl" alt="头像" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;" />
                <span v-else>{{ (userInfo.userName || "无名").charAt(0) }}</span>
              </a-avatar>
              <div class="avatar-edit" @click="$refs.fileInput && $refs.fileInput.click()">
                <icon-camera />
              </div>
            </div>
            <input
              type="file"
              ref="fileInput"
              accept="image/jpeg,image/jpg,image/png,image/webp,image/svg+xml"
              style="display: none"
              @change="handleAvatarChange"
            />
          </div>
          <div class="user-info">
            <h2 class="username">{{ userInfo.userName || "无名" }}</h2>
            <p class="user-account">
              <icon-user />
              {{ userInfo.userAccount || "未知账号" }}
            </p>
            <p class="user-profile" v-if="userInfo.userProfile">
              <icon-book />
              {{ userInfo.userProfile }}
            </p>
          </div>
        </div>
      </div>

      <!-- 提交统计 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon total">
            <icon-file />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ submitStats.total }}</div>
            <div class="stat-label">总提交</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon success">
            <icon-check-circle />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ submitStats.successCount }}</div>
            <div class="stat-label">通过</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon fail">
            <icon-close-circle />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ submitStats.failCount }}</div>
            <div class="stat-label">失败</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rate">
            <icon-trophy />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ submitStats.successRate }}%</div>
            <div class="stat-label">通过率</div>
          </div>
        </div>
      </div>

      <!-- 详细信息 -->
      <div class="info-card">
        <div class="info-header">
          <icon-idcard />
          <h3>账号信息</h3>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ userInfo.userName || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">账号</span>
            <span class="info-value">{{ userInfo.userAccount || '未知' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">角色</span>
            <span class="info-value">
              <a-tag :color="userInfo.userRole === 'admin' ? 'arcoblue' : 'green'" size="small">
                {{ userInfo.userRole === 'admin' ? '管理员' : '普通用户' }}
              </a-tag>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">注册时间</span>
            <span class="info-value">{{ userInfo.createTime || '未知' }}</span>
          </div>
          <div class="info-item full-width" v-if="userInfo.userProfile">
            <span class="info-label">个人简介</span>
            <span class="info-value">{{ userInfo.userProfile }}</span>
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
import { uploadFileUsingPost } from "@/api/fileController";
import { updateAvatar } from "@/api/userController";
import { UserControllerService } from "../../../generated";
import {
  IconUser,
  IconCamera,
  IconBook,
  IconFile,
  IconCheckCircle,
  IconCloseCircle,
  IconTrophy,
  IconIdcard,
} from '@arco-design/web-vue/es/icon';
import { getSubmitStats } from "@/api/questionSubmitController";

const store = useStore();

const avatarUrl = ref<string>("");
const fileInput = ref<HTMLInputElement | null>(null);

const userInfo = ref({
  userName: "",
  userAccount: "",
  userProfile: "",
  userRole: "",
  createTime: "",
});

const submitStats = ref({
  total: 0,
  successCount: 0,
  failCount: 0,
  successRate: 0,
});

onMounted(() => {
  loadUserInfo();
  loadSubmitStats();
});

const loadUserInfo = () => {
  const loginUser = store.state.user.loginUser;
  if (loginUser) {
    userInfo.value = {
      userName: loginUser.userName || "",
      userAccount: loginUser.userAccount || "",
      userProfile: loginUser.userProfile || "",
      userRole: loginUser.userRole || "user",
      createTime: loginUser.createTime
        ? new Date(loginUser.createTime).toLocaleDateString("zh-CN")
        : "",
    };
    avatarUrl.value = loginUser.userAvatar || "/yake.webp";
  }
};

const handleAvatarChange = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0];
  if (!file) return;

  const maxSize = 1 * 1024 * 1024;
  if (file.size > maxSize) {
    message.error("文件大小不能超过 1MB");
    return;
  }

  const allowedTypes = ["image/jpeg", "image/jpg", "image/png", "image/webp", "image/svg+xml"];
  if (!allowedTypes.includes(file.type)) {
    message.error("仅支持 jpg/png/webp/svg 格式的图片");
    return;
  }

  try {
    const uploadRes = await uploadFileUsingPost({ biz: "user_avatar" }, {}, file);

    if (uploadRes.data && uploadRes.data.code === 0) {
      const newAvatarUrl = uploadRes.data.data;

      const updateRes = await updateAvatar(newAvatarUrl);
      if (updateRes.data && updateRes.data.code === 0) {
        avatarUrl.value = newAvatarUrl;
        message.success("头像更新成功");

        const userRes = await UserControllerService.getLoginUserUsingGet();
        if (userRes.data && userRes.data.code === 0) {
          store.dispatch("user/getLoginUser");
        }
      } else {
        message.error("更新头像失败");
      }
    } else {
      message.error("上传失败：" + (uploadRes.data?.message || "未知错误"));
    }
  } catch (error) {
    console.error("上传头像失败:", error);
    message.error("上传头像失败，请重试");
  }

  (event.target as HTMLInputElement).value = "";
};

const loadSubmitStats = async () => {
  try {
    const res = await getSubmitStats();
    if (res.data.code === 0) {
      submitStats.value = res.data.data;
    }
  } catch (e) {
    console.error("加载提交统计失败", e);
  }
};
</script>

<style scoped>
#userProfile {
  background-color: var(--bg-page);
  min-height: 100vh;
  padding: var(--space-6) 0;
  font-family: var(--font-body);
  position: relative;
}

/* 背景纹理 */
#userProfile::before {
  content: '';
  position: fixed;
  inset: 0;
  background:
    radial-gradient(at 20% 20%, rgba(139, 92, 246, 0.05) 0px, transparent 50%),
    radial-gradient(at 80% 40%, rgba(59, 130, 246, 0.04) 0px, transparent 50%),
    radial-gradient(at 40% 80%, rgba(6, 182, 212, 0.03) 0px, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

.profile-container {
  max-width: 1000px;
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

/* 个人信息卡片 */
.profile-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  margin-bottom: var(--space-5);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  animation: fadeInUp 0.6s var(--ease-out) calc(var(--stagger-delay) * 2) backwards;
}

.profile-header {
  display: flex;
  gap: var(--space-6);
  align-items: flex-start;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
}

.user-avatar {
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-accent-500) 100%);
  color: white;
  font-size: var(--text-3xl);
  font-weight: 600;
  border: 3px solid var(--bg-card);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  background: var(--color-primary-500);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  border: 2px solid var(--bg-card);
  transition: all var(--duration-fast);
}

.avatar-edit:hover {
  background: var(--color-primary-600);
  transform: scale(1.1);
}

.user-info {
  flex: 1;
}

.username {
  margin: 0 0 var(--space-2) 0;
  color: var(--text-primary);
  font-size: var(--text-2xl);
  font-weight: 700;
}

.user-account,
.user-profile {
  margin: 0 0 var(--space-2) 0;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
  margin-bottom: var(--space-5);
}

.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  transition: all var(--duration-fast);
  animation: fadeInUp 0.5s var(--ease-out) backwards;
}

.stat-card:nth-child(1) { animation-delay: 0.15s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.25s; }
.stat-card:nth-child(4) { animation-delay: 0.3s; }

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-icon.total { background: var(--color-info-bg, #e0f2fe); color: var(--color-info, #3b82f6); }
.stat-icon.success { background: var(--color-success-bg, #dcfce7); color: var(--color-success, #22c55e); }
.stat-icon.fail { background: var(--color-error-bg, #fee2e2); color: var(--color-error, #ef4444); }
.stat-icon.rate { background: #fef3c7; color: #d97706; }

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-top: var(--space-1);
}

/* 账号信息卡片 */
.info-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  animation: fadeInUp 0.6s var(--ease-out) 0.35s backwards;
}

.info-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  background: var(--bg-subtle, #f8fafc);
  border-bottom: 1px solid var(--border-default);
  font-size: 18px;
  color: var(--color-primary-500, #3b82f6);
}

.info-header h3 {
  margin: 0;
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0;
  padding: 0;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-default);
  border-right: 1px solid var(--border-default);
}

.info-item:nth-child(2n) {
  border-right: none;
}

.info-item.full-width {
  grid-column: 1 / -1;
  border-right: none;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary, #94a3b8);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-value {
  font-size: var(--text-sm);
  color: var(--text-primary);
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 900px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  #userProfile {
    padding: var(--space-4) 0;
  }

  .profile-container {
    padding: 0 var(--space-4);
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .header-content {
    padding: var(--space-4);
  }

  .title-section {
    flex-direction: column;
  }

  .profile-card {
    padding: var(--space-5);
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-item {
    border-right: none;
  }
}
</style>