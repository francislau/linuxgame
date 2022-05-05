//index.js
import {
    createRouter,
    createWebHistory
} from 'vue-router'


const routerHistory = createWebHistory()
const routes = [{
        path: "/",
        name: "main",
        component: () => import("../views/Main.vue")
    },{
        path: "/login",
        name: "login",
        component: () => import("../views/Login.vue")
    }
]
const router = createRouter({
    history: createWebHistory(),
    routes
})
export default router