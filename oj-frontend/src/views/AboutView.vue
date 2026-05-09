<template>
  <div id="aboutView">
    <div class="about-container">
      <div class="about-header">
        <h1 class="about-title">关于亚克OJ</h1>
        <p class="about-subtitle">在线判题系统</p>
      </div>
      <div class="about-content">
        <div class="about-section">
          <h2>项目介绍</h2>
          <p>亚克OJ是一个在线判题系统，为编程爱好者提供在线编程练习和竞赛平台。</p>
        </div>
        <div class="about-section">
          <h2>主要功能</h2>
          <ul>
            <li>在线编程练习</li>
            <li>题目管理</li>
            <li>代码提交与判题</li>
            <li>用户排行榜</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#aboutView {
  min-height: 100vh;
  background: var(--bg-page);
  padding: var(--space-5);
}

.about-container {
  max-width: 800px;
  margin: 0 auto;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  border: 1px solid var(--border-default);
}

.about-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.about-title {
  font-size: var(--text-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.about-subtitle {
  font-size: var(--text-base);
  color: var(--text-secondary);
}

.about-section {
  margin-bottom: var(--space-6);
}

.about-section h2 {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-3);
}

.about-section p {
  font-size: var(--text-base);
  color: var(--text-secondary);
  line-height: 1.6;
}

.about-section ul {
  list-style: none;
  padding: 0;
}

.about-section li {
  padding: var(--space-2) 0;
  color: var(--text-secondary);
  font-size: var(--text-base);
}
</style>
