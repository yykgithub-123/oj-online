import { createApp } from "vue";
import App from "./App.vue";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";
import router from "./router";
import store from "./store";
import "@/plugins/axios";
import "@/access";
import "bytemd/dist/index.css";

// 自定义样式
import "@/assets/styles/variables.css";
import "@/assets/styles/common.css";

// 忽略 ResizeObserver 循环警告 - 多层防御
const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback: ResizeObserverCallback) {
    super((entries, observer) => {
      window.requestAnimationFrame(() => {
        try {
          callback(entries, observer);
        } catch (e) {
          // 忽略 ResizeObserver 相关错误
          if (e instanceof Error && e.message.includes('ResizeObserver')) {
            return;
          }
          throw e;
        }
      });
    });
  }
};

// 全局捕获并忽略 ResizeObserver 错误（console.error 层）
const originalError = console.error;
console.error = (...args: any[]) => {
  const message = args[0];
  if (typeof message === 'string' && message.includes('ResizeObserver')) {
    return;
  }
  if (message instanceof Error && message.message?.includes('ResizeObserver')) {
    return;
  }
  originalError.apply(console, args);
};

// 全局 error 事件捕获（window error 层）
window.addEventListener('error', (event) => {
  if (event.message && event.message.includes('ResizeObserver')) {
    event.stopImmediatePropagation();
    event.preventDefault();
    return false;
  }
  return true;
}, true);

// 全局 unhandledrejection 捕获（Promise rejection 层）
window.addEventListener('unhandledrejection', (event) => {
  const reason = event.reason;
  if (reason instanceof Error && reason.message?.includes('ResizeObserver')) {
    event.preventDefault();
    return;
  }
  if (typeof reason === 'string' && reason.includes('ResizeObserver')) {
    event.preventDefault();
    return;
  }
});

const app = createApp(App);

// Vue 全局错误处理（Vue 组件层）
app.config.errorHandler = (err, instance, info) => {
  if (err instanceof Error && err.message?.includes('ResizeObserver')) {
    return;
  }
  console.error('Vue error:', err, info);
};

// Vue 全局警告处理
app.config.warnHandler = (msg, instance, trace) => {
  if (msg.includes('ResizeObserver')) {
    return;
  }
  console.warn(msg, trace);
};

app.use(ArcoVue).use(store).use(router).mount("#app");
