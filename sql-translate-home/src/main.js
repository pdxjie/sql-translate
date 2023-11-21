import Vue from 'vue'
import Antd from 'ant-design-vue'
import App from './App'
import 'ant-design-vue/dist/antd.css'
import router from "@/router/router"
// 引入 Antd 组件
Vue.use(Antd)
new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
