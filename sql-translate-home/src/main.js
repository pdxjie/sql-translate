import Vue from 'vue'
import Antd from 'ant-design-vue';
import App from './App';
import 'ant-design-vue/dist/antd.css';
Vue.config.productionTip = false
// 导入axios 没有./  (axios网络请求工具:1不依赖dom,2.前后端都可以用,3. 丰富拦截,扩展功能,4可封装,复用性强)
import axios from 'axios';
// 挂载到vue的全局(原型上),在每个组件都可以使用 ,prototype是固定的,$axios是自定义的
Vue.prototype.$axios = axios;
// 指定默认的请求域名
axios.defaults.baseURL = "http://localhost:8000"
Vue.use(Antd)
new Vue({
  axios,
  render: h => h(App),
}).$mount('#app')
