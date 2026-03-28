<template>
  <div id="userRegisterPage" class="register-container">
    <!-- 左侧插图区域 -->
    <div class="left-section">
      <div class="background-pattern"></div>
      <div class="illustration-content">
        <!-- Logo和品牌名称 -->
        <div class="brand-section">
          <img src="@/assets/yake-logo.svg" alt="亚克OJ" class="logo" />
          <h1 class="brand-name">亚克OJ</h1>
        </div>
        
        <!-- 主要插图 -->
        <div class="main-illustration">
          <div class="monitor">
            <div class="monitor-screen">
              <div class="code-lines">
                <div class="code-line">function yyk() {</div>
                <div class="code-line">  return "Hello World";</div>
                <div class="code-line">}</div>
              </div>
            </div>
          </div>
          
          <!-- 人物插图 -->
          <div class="people">
            <div class="person person-1">
              <div class="speech-bubble">id=</div>
            </div>
            <div class="person person-2">
              <div class="speech-bubble">&lt;/&gt;</div>
            </div>
            <div class="person person-3">
              <div class="speech-bubble">...</div>
            </div>
          </div>
          
          <!-- 装饰元素 -->
          <div class="decorations">
            <div class="plant"></div>
            <div class="coffee-cup"></div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 右侧注册表单区域 -->
    <div class="right-section">
      <div class="register-form-container">
        <h2 class="register-title">用户注册</h2>
        <a-form
          class="register-form"
          label-align="left"
          auto-label-width
          :model="form"
          @submit="handleSubmit"
        >
          <a-form-item field="userAccount" label="账号">
            <a-input v-model="form.userAccount" placeholder="请输入账号" />
          </a-form-item>
          <a-form-item field="userPassword" tooltip="密码不小于 8 位" label="密码">
            <a-input-password
              v-model="form.userPassword"
              placeholder="请输入密码"
            />
          </a-form-item>
          <a-form-item
            field="checkPassword"
            tooltip="确认密码不小于 8 位"
            label="确认密码"
          >
            <a-input-password
              v-model="form.checkPassword"
              placeholder="请输入确认密码"
            />
          </a-form-item>
          <a-form-item>
            <div class="form-actions">
              <a-button type="primary" html-type="submit" class="register-btn">
                注册
              </a-button>
              <a-link @click="gologin" class="login-link">老用户登录</a-link>
            </div>
          </a-form-item>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { UserControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const router = useRouter();

const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
} as API.UserRegisterRequest);

/**
 * 提交
 */
const handleSubmit = async () => {
  const res = await UserControllerService.userRegisterUsingPost(form);
  if (res.code == 0) {
    message.success("注册成功");
    router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    message.error("注册失败，" + res.message);
  }
};

const gologin = () => {
  router.push({
    path: "/user/login"
  });
};
</script>

<style scoped>
.register-container {
  display: flex;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
  width: 100vw;
  gap: 0;
}

/* 重置全局样式 */
#userRegisterPage {
  margin: 0;
  padding: 0;
  height: 100vh;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

/* 左侧区域 */
.left-section {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 20px;
  margin: 0;
}

.background-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(255,255,255,0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.3;
}

.illustration-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  max-width: 450px;
  min-height: 500px;
}

.brand-section {
  margin-bottom: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.logo {
  width: 60px;
  height: 60px;
  filter: brightness(0) invert(1);
}

.brand-name {
  font-size: 2.5rem;
  font-weight: bold;
  margin: 0;
  color: white;
}

.main-illustration {
  position: relative;
  width: 400px;
  height: 300px;
  margin: 0 auto;
}

.monitor {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200px;
  height: 120px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.monitor-screen {
  width: 100%;
  height: 100%;
  background: #1e1e1e;
  border-radius: 6px;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.code-lines {
  color: #fff;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  text-align: left;
}

.code-line {
  margin: 2px 0;
  color: #4fc3f7;
}

.code-line:nth-child(2) {
  color: #ff6b6b;
}

.people {
  position: absolute;
  width: 100%;
  height: 100%;
}

.person {
  position: absolute;
  width: 40px;
  height: 60px;
  background: #ff6b6b;
  border-radius: 20px;
}

.person-1 {
  top: 20px;
  left: 50px;
}

.person-2 {
  top: 40px;
  right: 60px;
}

.person-3 {
  top: 10px;
  right: 30px;
}

.speech-bubble {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  color: #333;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 10px;
  white-space: nowrap;
}

.decorations {
  position: absolute;
  width: 100%;
  height: 100%;
}

.plant {
  position: absolute;
  bottom: 20px;
  left: 30px;
  width: 30px;
  height: 60px;
  background: #4caf50;
  border-radius: 15px;
}

.coffee-cup {
  position: absolute;
  bottom: 30px;
  right: 40px;
  width: 25px;
  height: 30px;
  background: white;
  border-radius: 0 0 12px 12px;
}

.coffee-cup::after {
  content: '';
  position: absolute;
  top: -8px;
  right: -5px;
  width: 8px;
  height: 8px;
  background: #ff9800;
  border-radius: 50%;
}

/* 右侧区域 */
.right-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 20px;
  margin: 0;
}

.register-form-container {
  background: white;
  padding: 50px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 450px;
  min-height: 480px;
}

.register-title {
  text-align: center;
  margin-bottom: 45px;
  color: #333;
  font-size: 2rem;
  font-weight: 600;
}

.register-form {
  width: 100%;
}

.register-form :deep(.arco-form-item) {
  margin-bottom: 28px;
}

.register-form :deep(.arco-form-item:last-child) {
  margin-bottom: 0;
}

.register-form :deep(.arco-form-item-label) {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.register-form :deep(.arco-input) {
  height: 48px;
  font-size: 16px;
}

.register-form :deep(.arco-input-password) {
  height: 48px;
  font-size: 16px;
}

.form-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-top: 35px;
}

.register-btn {
  width: 110px;
  height: 44px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 16px;
}

.login-link {
  color: #667eea;
  font-weight: 500;
  font-size: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
    height: 100vh;
  }
  
  .left-section {
    flex: none;
    height: 50vh;
    padding: 10px;
    margin: 0;
  }
  
  .right-section {
    flex: 1;
    padding: 10px;
    margin: 0;
  }
  
  .main-illustration {
    width: 300px;
    height: 200px;
  }
  
  .brand-name {
    font-size: 2rem;
  }
  
  .logo {
    width: 40px;
    height: 40px;
  }
}
</style>
