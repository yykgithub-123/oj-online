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
  background: var(--bg-page);
  width: 100%;
  min-width: 100%;
}

#basicLayout .header {
  padding: 0;
  height: var(--header-height);
  line-height: var(--header-height);
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-default);
  position: sticky;
  top: 0;
  z-index: 100;
  width: 100%;
}

#basicLayout .content {
  background: var(--bg-page);
  padding: 0;
  min-height: calc(100vh - var(--header-height));
  width: 100%;
}
</style>
<script>
import GlobalHeader from "@/components/GlobalHeader";

export default {
  components: { GlobalHeader },
};
</script>
