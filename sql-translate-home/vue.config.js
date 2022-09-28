//const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin')
// 导出模块
module.exports = {
    devServer: {
      proxy: { // 配置跨域
        '/api': {
          target: `http://localhost:8000`,   //自定义的请求后台接口
          changeOrigin: true, // 允许跨域
          pathRewrite: {
            '^/api': '' // 重写请求
          }
        }
      }
    }
  }
  
    