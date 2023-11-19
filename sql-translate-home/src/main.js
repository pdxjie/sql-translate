import Vue from 'vue'
import Antd from 'ant-design-vue'
import App from './App'
import 'ant-design-vue/dist/antd.css'
// 引入 Antd 组件
Vue.use(Antd)
new Vue({
  render: h => h(App),
}).$mount('#app')
