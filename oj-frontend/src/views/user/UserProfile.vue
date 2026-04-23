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
            <p class="user-email">
              <icon-email />
              {{ userInfo.userEmail || "未设置邮箱" }}
            </p>
            <p class="user-profile" v-if="userInfo.userProfile">
              <icon-book />
              {{ userInfo.userProfile }}
            </p>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon solved">
            <icon-check-circle />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ userStats.solvedCount || 0 }}</div>
            <div class="stat-label">已解决</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon submit">
            <icon-code />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ userStats.submitCount || 0 }}</div>
            <div class="stat-label">总提交</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rate">
            <icon-trophy />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ userStats.acceptRate || 0 }}%</div>
            <div class="stat-label">通过率</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rank">
            <icon-fire />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ userStats.ranking || '--' }}</div>
            <div class="stat-label">排名</div>
          </div>
        </div>
      </div>

      <!-- 解题统计 -->
      <div class="section-card">
        <div class="section-header">
          <div class="section-icon chart">
            <icon-bar-chart />
          </div>
          <h3 class="section-title">解题统计</h3>
        </div>
        <div class="section-content">
          <div class="stats-grid">
            <div class="difficulty-card easy">
              <div class="difficulty-header">
                <span class="difficulty-name">简单</span>
                <span class="difficulty-count">{{ userStats.easySolved || 0 }}</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: getProgressWidth('easy') }"></div>
              </div>
              <div class="progress-label">{{ getProgressText('easy') }}</div>
            </div>
            <div class="difficulty-card medium">
              <div class="difficulty-header">
                <span class="difficulty-name">中等</span>
                <span class="difficulty-count">{{ userStats.mediumSolved || 0 }}</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: getProgressWidth('medium') }"></div>
              </div>
              <div class="progress-label">{{ getProgressText('medium') }}</div>
            </div>
            <div class="difficulty-card hard">
              <div class="difficulty-header">
                <span class="difficulty-name">困难</span>
                <span class="difficulty-count">{{ userStats.hardSolved || 0 }}</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: getProgressWidth('hard') }"></div>
              </div>
              <div class="progress-label">{{ getProgressText('hard') }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近活动 -->
      <div class="section-card">
        <div class="section-header">
          <div class="section-icon activity">
            <icon-history />
          </div>
          <h3 class="section-title">最近活动</h3>
        </div>
        <div class="section-content">
          <div class="activity-list">
            <div
              v-for="(activity, index) in recentActivities"
              :key="activity.id"
              class="activity-item"
            >
              <div class="activity-badge" :class="activity.status">
                <icon-check v-if="activity.status === 'success'" />
                <icon-close v-else />
              </div>
              <div class="activity-content">
                <div class="activity-description">{{ activity.description }}</div>
                <div class="activity-time">
                  <icon-clock-circle />
                  {{ formatTime(activity.time) }}
                </div>
              </div>
            </div>
            <div v-if="recentActivities.length === 0" class="empty-activity">
              <icon-empty />
              <p>暂无最近活动</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 个人设置 -->
      <div class="section-card">
        <div class="section-header">
          <div class="section-icon settings">
            <icon-settings />
          </div>
          <h3 class="section-title">个人设置</h3>
        </div>
        <div class="section-content">
          <a-form :model="userForm" layout="vertical" class="settings-form">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="用户名">
                  <a-input v-model="userForm.userName" placeholder="请输入用户名" class="form-input" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱">
                  <a-input v-model="userForm.userEmail" placeholder="请输入邮箱" class="form-input" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item label="个人简介">
              <a-textarea
                v-model="userForm.userProfile"
                placeholder="请输入个人简介"
                :rows="3"
                class="form-input"
              />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="updateProfile" class="save-btn">
                <icon-check />
                保存修改
              </a-button>
            </a-form-item>
          </a-form>
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
  IconEmail,
  IconBook,
  IconCheckCircle,
  IconCode,
  IconTrophy,
  IconFire,
  IconBarChart,
  IconHistory,
  IconSettings,
  IconCheck,
  IconClose,
  IconClockCircle,
  IconEmpty,
} from '@arco-design/web-vue/es/icon';

const store = useStore();

const avatarUrl = ref<string>("");
const fileInput = ref<HTMLInputElement | null>(null);

const userInfo = ref({
  userName: "",
  userEmail: "",
  userProfile: "",
});

const userStats = ref({
  solvedCount: 0,
  submitCount: 0,
  acceptRate: 0,
  easySolved: 0,
  mediumSolved: 0,
  hardSolved: 0,
  ranking: 128,
});

const recentActivities = ref([]);

const userForm = ref({
  userName: "",
  userEmail: "",
  userProfile: "",
});

onMounted(() => {
  loadUserInfo();
  loadUserStats();
  loadRecentActivities();
});

const loadUserInfo = () => {
  const loginUser = store.state.user.loginUser;
  if (loginUser) {
    userInfo.value = {
      userName: loginUser.userName || "",
      userEmail: loginUser.userEmail || "",
      userProfile: loginUser.userProfile || "",
    };
    userForm.value = { ...userInfo.value };
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

const loadUserStats = async () => {
  userStats.value = {
    solvedCount: 25,
    submitCount: 100,
    acceptRate: 25.0,
    easySolved: 15,
    mediumSolved: 8,
    hardSolved: 2,
    ranking: 128,
  };
};

const loadRecentActivities = async () => {
  recentActivities.value = [
    { id: 1, description: "成功解决了题目：两数之和", time: new Date(Date.now() - 2 * 60 * 60 * 1000), status: "success" },
    { id: 2, description: "成功解决了题目：反转链表", time: new Date(Date.now() - 5 * 60 * 60 * 1000), status: "success" },
    { id: 3, description: "尝试解决题目：二叉树遍历失败", time: new Date(Date.now() - 8 * 60 * 60 * 1000), status: "fail" },
    { id: 4, description: "成功解决了题目：二分查找", time: new Date(Date.now() - 24 * 60 * 60 * 1000), status: "success" },
  ];
};

const getProgressWidth = (difficulty: string) => {
  const total = userStats.value.solvedCount || 1;
  let solved = 0;

  switch (difficulty) {
    case 'easy': solved = userStats.value.easySolved || 0; break;
    case 'medium': solved = userStats.value.mediumSolved || 0; break;
    case 'hard': solved = userStats.value.hardSolved || 0; break;
  }

  return `${Math.min((solved / total) * 100, 100)}%`;
};

const getProgressText = (difficulty: string) => {
  const total = userStats.value.solvedCount || 1;
  let solved = 0;

  switch (difficulty) {
    case 'easy': solved = userStats.value.easySolved || 0; break;
    case 'medium': solved = userStats.value.mediumSolved || 0; break;
    case 'hard': solved = userStats.value.hardSolved || 0; break;
  }

  return `${solved}/${total} 题`;
};

const formatTime = (time: Date) => {
  const now = new Date();
  const diff = now.getTime() - time.getTime();
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(hours / 24);

  if (days > 0) return `${days}天前`;
  else if (hours > 0) return `${hours}小时前`;
  else return "刚刚";
};

const updateProfile = async () => {
  try {
    message.success("个人资料更新成功");
    userInfo.value = { ...userForm.value };
  } catch (error) {
    message.error("更新失败，请稍后重试");
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

.user-email,
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
  animation: scaleIn 0.5s var(--ease-out) backwards;
}

.stat-card:nth-child(1) { animation-delay: calc(var(--stagger-delay) * 3); }
.stat-card:nth-child(2) { animation-delay: calc(var(--stagger-delay) * 4); }
.stat-card:nth-child(3) { animation-delay: calc(var(--stagger-delay) * 5); }
.stat-card:nth-child(4) { animation-delay: calc(var(--stagger-delay) * 6); }

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

.stat-icon.solved { background: var(--color-success-bg); color: var(--color-success); }
.stat-icon.submit { background: var(--color-info-bg); color: var(--color-info); }
.stat-icon.rate { background: var(--color-warning-bg); color: var(--color-warning); }
.stat-icon.rank { background: #fef3c7; color: #d97706; }

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

.section-card:nth-of-type(1) { animation-delay: calc(var(--stagger-delay) * 7); }
.section-card:nth-of-type(2) { animation-delay: calc(var(--stagger-delay) * 8); }
.section-card:nth-of-type(3) { animation-delay: calc(var(--stagger-delay) * 9); }

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

.section-icon.chart { background: var(--color-primary-500); }
.section-icon.activity { background: var(--color-accent-500); }
.section-icon.settings { background: #8b5cf6; }

.section-title {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.section-content {
  padding: var(--space-5);
}

/* 解题统计 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-4);
}

.difficulty-card {
  padding: var(--space-5);
  border-radius: var(--radius-lg);
  border: 1px solid;
  transition: all var(--duration-fast);
}

.difficulty-card:hover {
  transform: translateY(-2px);
}

.difficulty-card.easy {
  background: var(--color-success-bg);
  border-color: var(--color-success-border);
}

.difficulty-card.medium {
  background: var(--color-warning-bg);
  border-color: var(--color-warning-border);
}

.difficulty-card.hard {
  background: var(--color-error-bg);
  border-color: var(--color-error-border);
}

.difficulty-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.difficulty-name {
  font-weight: 600;
  font-size: var(--text-sm);
}

.difficulty-card.easy .difficulty-name { color: var(--color-success-text); }
.difficulty-card.medium .difficulty-name { color: var(--color-warning-text); }
.difficulty-card.hard .difficulty-name { color: var(--color-error-text); }

.difficulty-count {
  font-weight: 700;
  font-size: var(--text-2xl);
  color: var(--text-primary);
}

.progress-bar {
  height: 6px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: var(--space-2);
}

.progress-fill {
  height: 100%;
  transition: width var(--duration-normal);
  border-radius: 3px;
}

.difficulty-card.easy .progress-fill { background: var(--color-success); }
.difficulty-card.medium .progress-fill { background: var(--color-warning); }
.difficulty-card.hard .progress-fill { background: var(--color-error); }

.progress-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 活动列表 */
.activity-list {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-4);
  border-radius: var(--radius-md);
  transition: all var(--duration-fast);
}

.activity-item:hover {
  background: var(--bg-subtle);
}

.activity-badge {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.activity-badge.success { background: var(--color-success-bg); color: var(--color-success); }
.activity-badge.fail { background: var(--color-error-bg); color: var(--color-error); }

.activity-content {
  flex: 1;
}

.activity-description {
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: 500;
  margin-bottom: var(--space-1);
}

.activity-time {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.empty-activity {
  text-align: center;
  padding: var(--space-10) 0;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* 设置表单 */
.settings-form {
  max-width: 600px;
}

.form-input :deep(.arco-input),
.form-input :deep(.arco-textarea) {
  border-radius: var(--radius-md);
}

.save-btn {
  border-radius: var(--radius-md);
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.39);
  transition: all var(--duration-fast);
}

.save-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.5);
}

.save-btn:active {
  transform: translateY(0) scale(0.98);
}

/* 响应式设计 */
@media (max-width: 900px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .stats-grid {
    grid-template-columns: 1fr;
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

  .stats-row {
    grid-template-columns: 1fr;
  }

  .profile-card {
    padding: var(--space-5);
  }
}
</style>