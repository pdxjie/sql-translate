import VueRouter from "vue-router"
import Vue from 'vue'
import LayoutContent from "@/layout/LayoutContent.vue"
// 注入灵魂
Vue.use(VueRouter)
const routes = [
    {
        path: '/',
        name: 'Home',
        component: LayoutContent
    }
]

const router = new VueRouter({
  routes,
  mode: 'hash'
})

export default router