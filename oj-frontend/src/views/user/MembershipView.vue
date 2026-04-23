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
  background: var(--bg-page);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-5);
}

.membership-container {
  max-width: 600px;
  width: 100%;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  border: 1px solid var(--border-default);
}

.membership-header {
  background: var(--color-primary-500);
  color: white;
  text-align: center;
  padding: var(--space-10) var(--space-5);
}

.title {
  font-size: var(--text-2xl);
  font-weight: 600;
  margin: 0 0 var(--space-3) 0;
}

.subtitle {
  font-size: var(--text-base);
  margin: 0;
  opacity: 0.9;
}

.membership-content {
  padding: var(--space-10) var(--space-6);
}

.feature-card {
  display: flex;
  align-items: center;
  gap: var(--space-5);
  background: var(--color-gray-50);
  border-radius: var(--radius-md);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  border: 2px dashed var(--border-default);
}

.card-icon {
  font-size: var(--text-3xl);
  color: var(--color-warning);
  flex-shrink: 0;
}

.card-content h3 {
  font-size: var(--text-xl);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  font-weight: 600;
}

.card-content p {
  font-size: var(--text-base);
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.5;
}

.development-info {
  margin-bottom: var(--space-10);
}

.info-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) 0;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  border-bottom: 1px solid var(--border-default);
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .arco-icon {
  color: var(--color-primary-500);
  font-size: var(--text-base);
}

.action-buttons {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
}

.subscribe-btn {
  background: var(--color-primary-500);
  border-color: var(--color-primary-500);
  border-radius: var(--radius-sm);
  padding: 0 var(--space-8);
  height: 44px;
  font-size: var(--text-base);
  font-weight: 500;
  cursor: not-allowed;
  opacity: 0.6;
}

.back-btn {
  border: 1px solid var(--color-primary-500);
  color: var(--color-primary-500);
  border-radius: var(--radius-sm);
  padding: 0 var(--space-8);
  height: 44px;
  font-size: var(--text-base);
  font-weight: 500;
  background: var(--bg-card);
  transition: all var(--duration-fast);
}

.back-btn:hover {
  background: var(--color-primary-500);
  color: white;
}

@media (max-width: 768px) {
  #membershipView {
    padding: var(--space-3);
  }

  .membership-header {
    padding: var(--space-6) var(--space-5);
  }

  .title {
    font-size: var(--text-xl);
  }

  .subtitle {
    font-size: var(--text-sm);
  }

  .membership-content {
    padding: var(--space-6) var(--space-5);
  }

  .feature-card {
    flex-direction: column;
    text-align: center;
    padding: var(--space-6);
  }

  .card-content h3 {
    font-size: var(--text-lg);
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