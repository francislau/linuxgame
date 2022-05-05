<template>
    <div class="container">
        <div class="input">
            <input v-model="student.stuNo" placeholder="请输入学号" ref="stuNo"/>
        </div>
        <div class="input">
            <input v-model="student.password" type="password" placeholder="请输入密码" @keyup.enter="login" />
        </div>
    </div>
    <div class="dialog-footer">
        <div class="login" @click="login"></div>
        <div class="about" @click="about"></div>
    </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance,onMounted } from 'vue'
import { utils } from '../assets/js/utils.js'
import axios from "axios";

const emit = defineEmits(['close']);
const stuNo = ref();


onMounted(()=>{
	stuNo.value.focus();
});


// 学生模型
const student = reactive({
    index: '',
    id: '',
    realname: '',
    stuNo: '',
    stuClass: '',
    password: ''
})

const login = function () {
    if (student.stuNo == '') {
		stuNo.value.focus();
        utils.alert("请输入学号");
        return false;
    }
    if (student.password == '') {
		stuNo.value.focus();
        utils.alert("请输入密码");
        return false;
    }

    axios
        .post('/api/student/login', student)
        .then((response) => {
            console.log(response.data)
            if (response.data.result == 'success') {
                localStorage.setItem("id", response.data.data.id);
                localStorage.setItem("realname", response.data.data.realname);
                localStorage.setItem("stuNo", response.data.data.stuNo);
                localStorage.setItem("token", response.data.data.token);

                if(student.password == '666666') {
                    localStorage.setItem("chgPsw", 1);
                }
               
                emit("close", 'login');
            } else {
				stuNo.value.focus();
                utils.alert(response.data.message);
            }
        })
        .catch(function (error) { // 请求失败处理
            console.log(error);
        });
}

const about = function () {
	window.open('https://linux.pingenetwork.com/help.html', '_blank');
}
</script>

<style scoped>
.container {
    width: 100%;
    padding-top: 50px;
}
input {
    border-radius: 10px;
    border: 5px solid #c0a920;
    background-color: #7d0000;
    margin-top: 20px;
    width: 60%;
    height: 45px;
    color: #ddd;
    font-size: 30px;
    font-weight: bolder;
    padding: 0 10px;
}
input::placeholder {
    color: #999;
}
.login {
    width: 100px;
    height: 50px;
    background-image: url("/login.png");
    background-size: contain;
    background-repeat: no-repeat;
}
.about {
    width: 100px;
    height: 50px;
    background-image: url("/about.png");
    background-size: contain;
    background-repeat: no-repeat;
}
.dialog-footer {
    width: 100%;
    height: 80px;
    margin-top: 40px;
    display: flex;
    justify-content: space-evenly;
}
</style>
