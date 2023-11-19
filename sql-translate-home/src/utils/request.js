import axios from "axios"

// Axios 配置
const config = {
    baseURL: '/api',
    timeout: 10000
}
// 创建 Axios 实例
const instance = axios.create(config)

// 请求拦截器
instance.interceptors.request.use(
    config => {
        // 在发送请求之前做些什么
        return config
    },
    error => {
        // 对请求错误做些什么
        return Promise.reject(error)
    }
)

// 响应拦截器
instance.interceptors.response.use(
    response => {
        // 对响应数据做点什么
        return response.data
    },
    error => {
        // 对响应错误做点什么
        return Promise.reject(error)
    }
)

export default instance