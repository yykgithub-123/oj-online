<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <img src="../assets/yake-logo.svg" class="logo" />
          <div>亚克 OJ</div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="http://www.isyyk.top" target="_blank">
          知识分享 by 程序员亚克
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#userLayout {
  text-align: center;
  background: var(--bg-page);
  min-height: 100vh;
}

#userLayout .logo {
  width: 48px;
  height: 48px;
}

#userLayout .header {
  margin-top: var(--space-4);
  background: transparent;
  height: auto;
  line-height: normal;
}

#userLayout .header :deep(.arco-space) {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
}

#userLayout .header div {
  font-size: var(--text-2xl);
  font-weight: 600;
  color: var(--text-primary);
}

#userLayout .content {
  margin-bottom: var(--space-4);
  padding: var(--space-5);
  background: transparent;
}

#userLayout .footer {
  padding: var(--space-4);
  text-align: center;
  background: transparent;
}

#userLayout .footer a {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  text-decoration: none;
  transition: color var(--duration-fast);
}

#userLayout .footer a:hover {
  color: var(--color-primary-500);
}
</style>
<script></script>
