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
            <a-menu-item key="/question_submit">浏览题目提交</a-menu-item>
            <a-menu-item key="/add/question">创建题目</a-menu-item>
            <a-menu-item key="/manage/question">管理题目</a-menu-item>
            <a-menu-item key="ai-link" class="ai-menu-item">亚克AI</a-menu-item>
          </a-menu>
        </nav>
      </div>
      
      <!-- 右侧搜索和用户区域 -->
      <div class="header-right">
        <div class="search-box">
          <a-input 
            placeholder="搜索题目/答案/用户" 
            class="search-input"
            v-model="searchKeyword"
            @press-enter="handleSearch"
          >
            <template #suffix>
              <a-icon type="search" @click="handleSearch" />
            </template>
          </a-input>
        </div>
        
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
import { ref } from "vue";
import { useStore } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import message from "@arco-design/web-vue/es/message";

const router = useRouter();
const store = useStore();

// 搜索关键词
const searchKeyword = ref("");

// 默认选中的菜单项
const selectedKeys = ref(["/questions"]);

// 路由跳转后，更新选中的菜单项
router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

// 获取真实登录用户数据
store.dispatch("user/getLoginUser");

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

// 搜索处理
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    console.log("搜索:", searchKeyword.value);
    // 实现搜索逻辑
    message.info(`正在搜索: ${searchKeyword.value}`);
    
    // 这里可以添加实际的搜索逻辑
    // 例如跳转到搜索结果页面
    router.push({
      path: '/questions',
      query: {
        search: searchKeyword.value
      }
    });
  } else {
    message.warning("请输入搜索关键词");
  }
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
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-container {
  /* 移除最大宽度限制，让头部占满整个屏幕 */
  width: 100%;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  /* 确保容器占满宽度 */
  min-width: 100%;
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
  margin-right: 16px;
  flex-shrink: 0;
  min-width: 0;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #4a90e2, #357abd);
  color: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
  margin-right: 8px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.nav-menu {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  display: flex;
  align-items: center;
}

.nav-menu :deep(.arco-menu) {
  border-bottom: none;
  background: transparent;
  display: flex;
  flex-wrap: nowrap;
  overflow: hidden;
  width: 100%;
}

.nav-menu :deep(.arco-menu-item) {
  color: #666;
  font-weight: 500;
  margin: 0 2px;
  padding: 0 8px;
  border-radius: 4px;
  white-space: nowrap;
  min-width: auto;
  flex-shrink: 0;
  font-size: 14px;
}

.nav-menu :deep(.arco-menu-item:hover) {
  background: #f0f7ff;
  color: #4a90e2;
}

.nav-menu :deep(.arco-menu-item-selected) {
  background: #e6f4ff;
  color: #4a90e2;
}

/* 亚克AI菜单项特殊样式 */
.nav-menu :deep(.ai-menu-item) {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: white !important;
  border-radius: 6px !important;
  position: relative;
  font-weight: 600 !important;
  margin: 0 4px !important;
}

.nav-menu :deep(.ai-menu-item:hover) {
  background: linear-gradient(135deg, #5a6fd8, #6a42a0) !important;
  color: white !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.nav-menu :deep(.ai-menu-item::after) {
  content: '🤖';
  margin-left: 4px;
  font-size: 12px;
}

.nav-menu :deep(.arco-menu) {
  flex-wrap: nowrap !important;
  overflow: hidden !important;
}

.nav-menu :deep(.arco-menu-item) {
  flex-shrink: 0 !important;
  white-space: nowrap !important;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  width: 280px;
}

.search-input {
  border-radius: 20px;
  background: #f5f5f5;
  border: 1px solid #e8e8e8;
}

.search-input :deep(.arco-input) {
  background: transparent;
}

.search-input :deep(.arco-input-suffix) {
  cursor: pointer;
  color: #999;
}

.user-section {
  display: flex;
  align-items: center;
}

.auth-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.login-btn {
  color: #666;
  font-weight: 500;
}

.register-btn {
  background: #4a90e2;
  border-color: #4a90e2;
  border-radius: 20px;
  padding: 0 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.vip-btn {
  color: #ff6b35;
  font-weight: 500;
}

.vip-btn :deep(.arco-icon) {
  color: #ff6b35;
  margin-right: 4px;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.user-avatar:hover {
  background: #f5f5f5;
}

.username {
  color: #333;
  font-weight: 500;
}

.user-menu {
  min-width: 120px;
}

.menu-item {
  padding: 8px 12px;
  margin: 0;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}

.menu-item:hover {
  background: #f5f5f5;
}

.menu-item.logout {
  color: #f53f3f;
}

.menu-item.logout:hover {
  background: #ffece8;
}

/* 响应式设计 */
@media (max-width: 1000px) {
  .header-container {
    padding: 0 16px;
  }
  
  .nav-menu :deep(.arco-menu-item) {
    margin: 0 1px;
    padding: 0 6px;
    font-size: 13px;
  }
  
  .search-box {
    width: 180px;
  }
  
  .logo-text {
    font-size: 16px;
  }
}

@media (max-width: 900px) {
  .header-container {
    padding: 0 12px;
  }
  
  .nav-menu :deep(.arco-menu-item) {
    margin: 0;
    padding: 0 4px;
    font-size: 12px;
  }
  
  .search-box {
    width: 150px;
  }
  
  .logo-text {
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 12px;
  }
  
  .nav-menu {
    display: none;
  }
  
  .search-box {
    width: 150px;
  }
  
  .logo-text {
    display: none;
  }
}
</style>
