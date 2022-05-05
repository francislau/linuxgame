import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import { utils } from "./assets/js/utils.js";

const app = createApp(App)

app.use(router)

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
        if (!response.data.value && response.data.message.indexOf('请重新登录') > -1) {
            let s = utils.alert(response.data.message, false, false);
            localStorage.clear();

            setTimeout(() => {
                utils.close(s);
                router.go(0)
            }, 1000);

        } else {
            return response
        }
    },
    error => {
        return Promise.reject(error)
    }
)

app.mount('#app')
