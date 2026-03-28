<template>
  <div id="membershipView">
    <div class="membership-container">
      <div class="membership-header">
        <h1 class="title">开通会员</h1>
        <p class="subtitle">解锁更多功能，提升学习体验</p>
      </div>
      
      <div class="membership-content">
        <div class="feature-card">
          <div class="card-icon">
            <icon-star />
          </div>
          <div class="card-content">
            <h3>该功能开发中！</h3>
            <p>我们正在努力为您打造更好的会员服务体验</p>
          </div>
        </div>
        
        <div class="development-info">
          <div class="info-item">
            <icon-clock-circle />
            <span>预计上线时间：敬请期待</span>
          </div>
          <div class="info-item">
            <icon-gift />
            <span>会员特权：题目解析、专属题库、学习报告等</span>
          </div>
          <div class="info-item">
            <icon-customer-service />
            <span>如有疑问，请联系客服</span>
          </div>
        </div>
        
        <div class="action-buttons">
          <a-button type="primary" size="large" disabled class="subscribe-btn">
            立即开通会员
          </a-button>
          <a-button size="large" @click="goBack" class="back-btn">
            返回首页
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { IconStar, IconClockCircle, IconGift, IconCustomerService } from '@arco-design/web-vue/es/icon';

const router = useRouter();

const goBack = () => {
  router.push('/');
};
</script>

<style scoped>
#membershipView {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.membership-container {
  max-width: 600px;
  width: 100%;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.membership-header {
  background: linear-gradient(135deg, #4a90e2, #357abd);
  color: white;
  text-align: center;
  padding: 40px 20px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.membership-content {
  padding: 40px 30px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  border: 2px dashed #e9ecef;
}

.card-icon {
  font-size: 48px;
  color: #ffd700;
  flex-shrink: 0;
}

.card-content h3 {
  font-size: 24px;
  color: #333;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.card-content p {
  font-size: 16px;
  color: #666;
  margin: 0;
  line-height: 1.5;
}

.development-info {
  margin-bottom: 40px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  color: #555;
  font-size: 14px;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .arco-icon {
  color: #4a90e2;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.subscribe-btn {
  background: linear-gradient(135deg, #4a90e2, #357abd);
  border: none;
  border-radius: 8px;
  padding: 0 32px;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  cursor: not-allowed;
  opacity: 0.6;
}

.back-btn {
  border: 2px solid #4a90e2;
  color: #4a90e2;
  border-radius: 8px;
  padding: 0 32px;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  background: white;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #4a90e2;
  color: white;
}

@media (max-width: 768px) {
  #membershipView {
    padding: 12px;
  }
  
  .membership-header {
    padding: 30px 20px;
  }
  
  .title {
    font-size: 28px;
  }
  
  .subtitle {
    font-size: 14px;
  }
  
  .membership-content {
    padding: 30px 20px;
  }
  
  .feature-card {
    flex-direction: column;
    text-align: center;
    padding: 24px;
  }
  
  .card-content h3 {
    font-size: 20px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .subscribe-btn,
  .back-btn {
    width: 100%;
  }
}
</style>