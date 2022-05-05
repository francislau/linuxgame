import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import Particles from 'particles.vue3';
import axios from 'axios'
import { ElMessage } from 'element-plus'

const app = createApp(App)


// 拦截器
axios.interceptors.request.use((request) => {

    const headers = request.headers
    if (!headers.Authorization) headers.Authorization = localStorage.getItem('token') ?? ""

    return request
})


axios.interceptors.response.use(
    response => {
        // 定时刷新access-token
        console.log(response.data.message)
        if (!response.data.value && response.data.message && response.data.message.indexOf('请重新登录') > -1) {
            ElMessage.error(response.data.message)
            router.push('/login')

        } else {
            return response
        }
    },
    error => {
        return Promise.reject(error)
    }
)


app.use(router)
app.use(ElementPlus)
app.use(Particles)


app.mount('#app')