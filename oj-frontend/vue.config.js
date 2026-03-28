const { defineConfig } = require("@vue/cli-service");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");

module.exports = defineConfig({
  publicPath: "./",
  transpileDependencies: true,
  productionSourceMap: false,
  lintOnSave: false, // 禁用ESLint检查
  chainWebpack(config) {
    config.plugin("monaco").use(new MonacoWebpackPlugin());
  },
  devServer: {
    port: 8082,
    proxy: {
      '/api': {
        target: 'http://localhost:8821',
        changeOrigin: true,
        logLevel: 'debug'
      }
    }
  }
});