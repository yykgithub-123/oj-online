<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout {
  background: #f5f7fa;
  /* 确保布局占满整个屏幕宽度 */
  width: 100%;
  min-width: 100%;
}

#basicLayout .header {
  padding: 0;
  height: 64px;
  line-height: 64px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
  /* 确保头部占满宽度 */
  width: 100%;
}

#basicLayout .content {
  background: #f5f7fa;
  padding: 0;
  min-height: calc(100vh - 64px);
  /* 确保内容区域占满宽度 */
  width: 100%;
}
</style>
<script>
import GlobalHeader from "@/components/GlobalHeader";

export default {
  components: { GlobalHeader },
};
</script>
