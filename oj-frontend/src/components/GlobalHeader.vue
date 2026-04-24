<template>
  <div id="globalHeader">
    <div class="header-container">
      <!-- 左侧logo和导航 -->
      <div class="header-left">
        <div class="logo-section">
          <div class="logo-icon">亚</div>
          <span class="logo-text">亚克OJ</span>
        </div>
        <nav class="nav-menu">
          <a-menu mode="horizontal" :selected-keys="selectedKeys" @menu-item-click="doMenuClick">
            <a-menu-item key="/questions">浏览题目</a-menu-item>
            <a-menu-item v-if="isLoggedIn" key="/question_submit">{{ isAdmin ? '浏览题目提交' : '我的提交' }}</a-menu-item>
            <a-menu-item v-if="isAdmin" key="/add/question">创建题目</a-menu-item>
            <a-menu-item v-if="isAdmin" key="/manage/question">管理题目</a-menu-item>
            <a-menu-item key="ai-link" class="ai-menu-item">亚克AI</a-menu-item>
          </a-menu>
        </nav>
      </div>
      
      <!-- 右侧用户区域 -->
      <div class="header-right">
        <div class="user-section">
          <!-- 未登录状态 -->
          <div v-if="!store.state.user.loginUser.id || store.state.user.loginUser.userName === '未登录'" class="auth-buttons">
            <a-button type="text" @click="gologin" class="login-btn">登录</a-button>
            <a-button type="primary" @click="goRegister" class="register-btn">注册</a-button>
          </div>
          
          <!-- 已登录状态 -->
          <div v-else class="user-info">
            <a-button type="text" class="vip-btn" @click="goToMembership">
              <a-icon type="crown" />
              开通会员
            </a-button>
            <a-popover trigger="click" position="bottom">
              <div class="user-avatar">
                <a-avatar size="small">
                  <img
                    v-if="store.state.user.loginUser.userAvatar"
                    :src="store.state.user.loginUser.userAvatar"
                    alt="头像"
                    style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;"
                    @error="handleAvatarError"
                  />
                  <span v-else>{{ (store.state.user.loginUser.userName ?? "无名").charAt(0) }}</span>
                </a-avatar>
                <span class="username">{{ store.state.user.loginUser.userName ?? "无名" }}</span>
              </div>
              <template #content>
                <div class="user-menu">
                  <p class="menu-item" @click="goProfile">个人中心</p>
                  <p class="menu-item" @click="goSettings">设置</p>
                  <p class="menu-item logout" @click="logout">退出登录</p>
                </div>
              </template>
            </a-popover>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { ref, computed } from "vue";
import { useStore } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import message from "@arco-design/web-vue/es/message";

const router = useRouter();
const store = useStore();

// 默认选中的菜单项
const selectedKeys = ref(["/questions"]);

// 路由跳转后，更新选中的菜单项
router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

// 获取真实登录用户数据
store.dispatch("user/getLoginUser");

const isAdmin = computed(() => {
  return store.state.user?.loginUser?.userRole === ACCESS_ENUM.ADMIN;
});

const isLoggedIn = computed(() => {
  const user = store.state.user?.loginUser;
  return !!(user && user.id && user.userName !== '未登录');
});

// 菜单点击处理
const doMenuClick = (key: string) => {
  // 如果是亚克AI链接，打开新窗口跳转到外部链接
  if (key === 'ai-link') {
    window.open('http://localhost:8820/program-master', '_blank');
    return;
  }

  router.push({
    path: key,
  });
};

// 退出登录
const logout = () => {
  store.dispatch("user/logout");
  router.push({
    path: "/questions",
  });
  window.location.reload();
};

// 跳转到登录页
const gologin = () => {
  router.push({
    path: "/user/login",
  });
};

// 跳转到注册页
const goRegister = () => {
  router.push({
    path: "/user/register",
  });
};

// 跳转到个人中心
const goProfile = () => {
  router.push({
    path: "/profile",
  });
};

// 跳转到设置页面
const goSettings = () => {
  router.push({
    path: "/settings",
  });
};

// 处理头像加载错误
const handleAvatarError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  // 如果加载失败，隐藏图片，显示默认首字符
  target.style.display = "none";
};

// 跳转到会员页面
const goToMembership = () => {
  router.push({
    path: "/membership",
  });
};
</script>

<style scoped>
#globalHeader {
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  width: 100%;
  max-width: var(--content-max-width);
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-height);
  padding: 0 var(--space-8);
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.logo-section {
  display: flex;
  align-items: center;
  margin-right: var(--space-8);
  flex-shrink: 0;
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-md);
  transition: background var(--duration-fast);
}

.logo-section:hover {
  background: var(--bg-subtle);
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-primary-600) 100%);
  color: white;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: var(--text-base);
  margin-right: var(--space-2);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.logo-text {
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.5px;
}

.nav-menu {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
}

.nav-menu :deep(.arco-menu) {
  border-bottom: none;
  background: transparent;
  display: flex;
  flex-wrap: nowrap;
  width: 100%;
}

.nav-menu :deep(.arco-menu-item) {
  color: var(--text-secondary);
  font-weight: 500;
  margin: 0 2px;
  padding: 0 var(--space-3);
  border-radius: var(--radius-md);
  white-space: nowrap;
  font-size: var(--text-sm);
  transition: all var(--duration-fast);
}

.nav-menu :deep(.arco-menu-item:hover) {
  background: var(--bg-subtle);
  color: var(--color-primary-500);
}

.nav-menu :deep(.arco-menu-item-selected) {
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  font-weight: 600;
}

/* 亚克AI菜单项 */
.nav-menu :deep(.ai-menu-item) {
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-accent-500) 100%) !important;
  color: white !important;
  border-radius: var(--radius-md) !important;
  font-weight: 600 !important;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
  transition: all var(--duration-fast) !important;
}

.nav-menu :deep(.ai-menu-item:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-section {
  display: flex;
  align-items: center;
}

.auth-buttons {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.login-btn {
  color: var(--text-secondary);
  font-weight: 500;
  transition: color var(--duration-fast);
}

.login-btn:hover {
  color: var(--color-primary-500);
}

.register-btn {
  background: var(--color-primary-500);
  border-color: var(--color-primary-500);
  border-radius: var(--radius-md);
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
  transition: all var(--duration-fast);
}

.register-btn:hover {
  background: var(--color-primary-600);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.vip-btn {
  color: var(--color-warning);
  font-weight: 600;
  background: var(--color-warning-bg);
  border-radius: var(--radius-md);
  padding: var(--space-1) var(--space-3);
  transition: all var(--duration-fast);
}

.vip-btn:hover {
  background: var(--color-warning);
  color: white;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-md);
  transition: all var(--duration-fast);
  border: 1px solid transparent;
}

.user-avatar:hover {
  background: var(--bg-subtle);
  border-color: var(--border-default);
}

.user-avatar :deep(.arco-avatar) {
  border: 2px solid var(--color-primary-200);
}

.username {
  color: var(--text-primary);
  font-weight: 600;
  font-size: var(--text-sm);
}

.user-menu {
  min-width: 140px;
  padding: var(--space-2);
}

.menu-item {
  padding: var(--space-3) var(--space-4);
  margin: 0;
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: all var(--duration-fast);
  font-size: var(--text-sm);
  font-weight: 500;
}

.menu-item:hover {
  background: var(--bg-subtle);
  color: var(--color-primary-500);
}

.menu-item.logout {
  color: var(--color-error);
  border-top: 1px solid var(--border-default);
  margin-top: var(--space-2);
  padding-top: var(--space-3);
}

.menu-item.logout:hover {
  background: var(--color-error-bg);
  color: var(--color-error);
}

/* 响应式设计 */
@media (max-width: 1000px) {
  .header-container {
    padding: 0 var(--space-5);
  }

  .nav-menu :deep(.arco-menu-item) {
    padding: 0 var(--space-2);
    font-size: var(--text-xs);
  }

  .logo-text {
    font-size: var(--text-base);
  }
}

@media (max-width: 900px) {
  .header-container {
    padding: 0 var(--space-4);
  }

  .nav-menu :deep(.arco-menu-item) {
    padding: 0 var(--space-1);
    font-size: var(--text-xs);
  }

  .logo-text {
    font-size: var(--text-sm);
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }

  .logo-text {
    display: none;
  }
}
</style>
