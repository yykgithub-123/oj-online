<template>
  <div id="userProfile">
    <div class="profile-container">
      <!-- 个人信息卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <a-avatar :size="80" class="user-avatar">
              <img v-if="avatarUrl" :src="avatarUrl" alt="头像" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;" />
              <span v-else>{{ (userInfo.userName || "无名").charAt(0) }}</span>
            </a-avatar>
            <input
              type="file"
              ref="fileInput"
              accept="image/jpeg,image/jpg,image/png,image/webp,image/svg+xml"
              style="display: none"
              @change="handleAvatarChange"
            />
            <a-button type="text" class="change-avatar-btn" @click="$refs.fileInput && $refs.fileInput.click()">更换头像</a-button>
          </div>
          <div class="user-info">
            <h2 class="username">{{ userInfo.userName || "无名" }}</h2>
            <p class="user-email">{{ userInfo.userEmail || "未设置邮箱" }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-number">{{ userStats.solvedCount || 0 }}</span>
                <span class="stat-label">已解决</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userStats.submitCount || 0 }}</span>
                <span class="stat-label">总提交</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userStats.acceptRate || 0 }}%</span>
                <span class="stat-label">通过率</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 解题统计 -->
      <div class="stats-section">
        <div class="section-title">解题统计</div>
        <div class="stats-grid">
          <div class="difficulty-card easy">
            <div class="difficulty-header">
              <span class="difficulty-name">简单</span>
              <span class="difficulty-count">{{ userStats.easySolved || 0 }}</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: getProgressWidth('easy') }"></div>
            </div>
          </div>
          <div class="difficulty-card medium">
            <div class="difficulty-header">
              <span class="difficulty-name">中等</span>
              <span class="difficulty-count">{{ userStats.mediumSolved || 0 }}</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: getProgressWidth('medium') }"></div>
            </div>
          </div>
          <div class="difficulty-card hard">
            <div class="difficulty-header">
              <span class="difficulty-name">困难</span>
              <span class="difficulty-count">{{ userStats.hardSolved || 0 }}</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: getProgressWidth('hard') }"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近活动 -->
      <div class="activity-section">
        <div class="section-title">最近活动</div>
        <div class="activity-list">
          <div 
            v-for="activity in recentActivities" 
            :key="activity.id"
            class="activity-item"
          >
            <div class="activity-icon">
              <a-icon type="check-circle" />
            </div>
            <div class="activity-content">
              <div class="activity-description">{{ activity.description }}</div>
              <div class="activity-time">{{ formatTime(activity.time) }}</div>
            </div>
          </div>
          <div v-if="recentActivities.length === 0" class="empty-activity">
            <p>暂无最近活动</p>
          </div>
        </div>
      </div>

      <!-- 个人设置 -->
      <div class="settings-section">
        <div class="section-title">个人设置</div>
        <div class="settings-form">
          <a-form :model="userForm" layout="vertical">
            <a-form-item label="用户名">
              <a-input v-model="userForm.userName" placeholder="请输入用户名" />
            </a-form-item>
            <a-form-item label="邮箱">
              <a-input v-model="userForm.userEmail" placeholder="请输入邮箱" />
            </a-form-item>
            <a-form-item label="个人简介">
              <a-textarea 
                v-model="userForm.userProfile" 
                placeholder="请输入个人简介"
                :rows="3"
              />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="updateProfile">保存修改</a-button>
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

const store = useStore();

// 头像URL
const avatarUrl = ref<string>("");
const fileInput = ref<HTMLInputElement | null>(null);

// 用户信息
const userInfo = ref({
  userName: "",
  userEmail: "",
  userProfile: "",
});

// 用户统计
const userStats = ref({
  solvedCount: 0,
  submitCount: 0,
  acceptRate: 0,
  easySolved: 0,
  mediumSolved: 0,
  hardSolved: 0,
});

// 最近活动
const recentActivities = ref([]);

// 表单数据
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

// 加载用户信息
const loadUserInfo = () => {
  const loginUser = store.state.user.loginUser;
  if (loginUser) {
    userInfo.value = {
      userName: loginUser.userName || "",
      userEmail: loginUser.userEmail || "",
      userProfile: loginUser.userProfile || "",
    };
    userForm.value = { ...userInfo.value };
    // 加载头像
    avatarUrl.value = loginUser.userAvatar || "/yake.webp";
  }
};

// 处理头像文件选择
const handleAvatarChange = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0];
  if (!file) return;

  // 验证文件大小
  const maxSize = 1 * 1024 * 1024; // 1MB
  if (file.size > maxSize) {
    message.error("文件大小不能超过 1MB");
    return;
  }

  // 验证文件类型
  const allowedTypes = ["image/jpeg", "image/jpg", "image/png", "image/webp", "image/svg+xml"];
  if (!allowedTypes.includes(file.type)) {
    message.error("仅支持 jpg/png/webp/svg 格式的图片");
    return;
  }

  try {
    // 上传文件
    const uploadRes = await uploadFileUsingPost(
      { biz: "user_avatar" },
      {},
      file
    );

    if (uploadRes.data && uploadRes.data.code === 0) {
      const newAvatarUrl = uploadRes.data.data;

      // 更新用户头像
      const updateRes = await updateAvatar(newAvatarUrl);
      if (updateRes.data && updateRes.data.code === 0) {
        avatarUrl.value = newAvatarUrl;
        message.success("头像更新成功");

        // 刷新 store 中的用户信息
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

  // 清空文件选择，允许重复选择同一文件
  (event.target as HTMLInputElement).value = "";
};

// 加载用户统计
const loadUserStats = async () => {
  try {
    // 这里应该调用后端API获取用户统计信息
    // 暂时使用模拟数据
    userStats.value = {
      solvedCount: 25,
      submitCount: 100,
      acceptRate: 25.0,
      easySolved: 15,
      mediumSolved: 8,
      hardSolved: 2,
    };
  } catch (error) {
    console.error("加载用户统计失败:", error);
  }
};

// 加载最近活动
const loadRecentActivities = async () => {
  try {
    // 这里应该调用后端API获取最近活动
    // 暂时使用模拟数据
    recentActivities.value = [
      {
        id: 1,
        description: "成功解决了题目：两数之和",
        time: new Date(Date.now() - 2 * 60 * 60 * 1000), // 2小时前
      },
      {
        id: 2,
        description: "成功解决了题目：反转链表",
        time: new Date(Date.now() - 5 * 60 * 60 * 1000), // 5小时前
      },
      {
        id: 3,
        description: "成功解决了题目：二分查找",
        time: new Date(Date.now() - 24 * 60 * 60 * 1000), // 1天前
      },
    ];
  } catch (error) {
    console.error("加载最近活动失败:", error);
  }
};

// 获取进度条宽度
const getProgressWidth = (difficulty: string) => {
  const total = userStats.value.solvedCount || 1;
  let solved = 0;
  
  switch (difficulty) {
    case 'easy':
      solved = userStats.value.easySolved || 0;
      break;
    case 'medium':
      solved = userStats.value.mediumSolved || 0;
      break;
    case 'hard':
      solved = userStats.value.hardSolved || 0;
      break;
  }
  
  return `${Math.min((solved / total) * 100, 100)}%`;
};

// 格式化时间
const formatTime = (time: Date) => {
  const now = new Date();
  const diff = now.getTime() - time.getTime();
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(hours / 24);
  
  if (days > 0) {
    return `${days}天前`;
  } else if (hours > 0) {
    return `${hours}小时前`;
  } else {
    return "刚刚";
  }
};

// 更新个人资料
const updateProfile = async () => {
  try {
    // 这里应该调用后端API更新用户信息
    message.success("个人资料更新成功");
    userInfo.value = { ...userForm.value };
  } catch (error) {
    message.error("更新失败，请稍后重试");
  }
};
</script>

<style scoped>
#userProfile {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;
}

.profile-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 个人信息卡片 */
.profile-card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.profile-header {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: linear-gradient(135deg, #4a90e2, #357abd);
  color: white;
  font-size: 32px;
  font-weight: bold;
}

.change-avatar-btn {
  color: #4a90e2;
  font-size: 12px;
}

.user-info {
  flex: 1;
}

.username {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.user-email {
  margin: 0 0 16px 0;
  color: #666;
  font-size: 14px;
}

.user-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #4a90e2;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

/* 解题统计 */
.stats-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.difficulty-card {
  padding: 16px;
  border-radius: 6px;
  border: 1px solid;
}

.difficulty-card.easy {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.difficulty-card.medium {
  background: #fff7e6;
  border-color: #ffd591;
}

.difficulty-card.hard {
  background: #fff2f0;
  border-color: #ffb3b3;
}

.difficulty-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.difficulty-name {
  font-weight: 500;
}

.difficulty-card.easy .difficulty-name {
  color: #52c41a;
}

.difficulty-card.medium .difficulty-name {
  color: #fa8c16;
}

.difficulty-card.hard .difficulty-name {
  color: #ff4d4f;
}

.difficulty-count {
  font-weight: 600;
  font-size: 18px;
}

.progress-bar {
  height: 4px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.difficulty-card.easy .progress-fill {
  background: #52c41a;
}

.difficulty-card.medium .progress-fill {
  background: #fa8c16;
}

.difficulty-card.hard .progress-fill {
  background: #ff4d4f;
}

/* 最近活动 */
.activity-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.activity-list {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  color: #52c41a;
  font-size: 16px;
  margin-top: 2px;
}

.activity-content {
  flex: 1;
}

.activity-description {
  color: #333;
  font-size: 14px;
  margin-bottom: 4px;
}

.activity-time {
  color: #999;
  font-size: 12px;
}

.empty-activity {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

/* 个人设置 */
.settings-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.settings-form {
  max-width: 500px;
}

/* 响应式设计 */
@media (max-width: 500px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }
  
  .user-stats {
    justify-content: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>