<script setup>
import axios from 'axios'
import { onMounted, reactive, ref, watch } from 'vue'
import { utils } from '../assets/js/utils';

const emit = defineEmits(['close']);

const conn = ref({
    ip: "",
    port: "",
    username: ""
})

const conn_pass = ref("")
const key_code_unverified = ref("")

watch(
    () => conn.value,
    (newval, oldValue) => {
        key_code_unverified.value = ""
    },
    {
        deep: true
    }
)

const checkConn = function () {
    console.log(conn.value)
    if (conn.value.ip == "") {
        utils.alert("请填写IP地址");
        return false;
    }
    if (conn.value.port == "") {
        utils.alert("请填写端口号");
        return false;
    }
    if (conn.value.username == "") {
        utils.alert("请填写用户名");
        return false;
    }
    let loading = utils.alert("正在连接...", false, false);
    axios
        .get('/api/ssh/checkConn', {
            params: {
                ip: conn.value.ip,
                port: conn.value.port,
                username: conn.value.username,
                password: conn_pass.value.replace("（密码不可修改）", ""),
            }
        })
        .then((response) => {
            console.log(response.data);
            utils.close(loading);
            if (response.data.result == 'success') {
                utils.alert("连接成功 ^_^");
                key_code_unverified.value = response.data.keyCode;
            } else {
                utils.alert(response.data.message);
            }
        })
        .catch(function (error) { // 请求失败处理
            console.log(error);
        });
}
const submit = function () {
    if (key_code_unverified.value == '') {
        utils.alert("请先测试是否连接");
        return false;
    }
    utils.alert("确定要修改吗？", true, true, () => {
        axios
            .post('/api/student/updateVerify', {
                id: localStorage.getItem('id'),
                userDevice: {
                    connUnverified: JSON.stringify(conn.value),
                    keyCodeUnverified: key_code_unverified.value,
                }
            })
            .then((response) => {
                console.log(response.data);
                utils.alert(response.data.message, true, true, () => {
                    emit("close");
                });
            })
            .catch(function (error) { // 请求失败处理
                console.log(error);
            });
    }, () => { });
}

onMounted(() => {

    axios
        .get('/api/student/query/' + localStorage.getItem('id'))
        .then((response) => {
            console.log(response.data);
            conn.value = JSON.parse(response.data.data.userDevice.conn);
            conn_pass.value = response.data.data.userDevice.connPass + "（密码不可修改）"
        })
        .catch(function (error) { // 请求失败处理
            console.log(error);
        });
})

</script>

<template>
    <div class="container">
        <div class="input">
            <input v-model="conn.ip" placeholder="请输入IP地址" />
        </div>
        <div class="input">
            <input v-model="conn.port" placeholder="请输入端口号，如：22" />
        </div>
        <div class="input">
            <input v-model="conn.username" placeholder="请输入用户名" />
        </div>
        <div class="input">
            <input v-model="conn_pass" placeholder="你分配的密码" readonly />
        </div>
    </div>
    <div class="dialog-footer">
        <div class="check" @click="checkConn"></div>
        <div class="modify" @click="submit"></div>
    </div>
</template>

<style scoped>
.container {
    width: 100%;
    height: 100%;
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
.check {
    width: 102px;
    height: 50px;
    background-image: url("/connection.png");
    background-size: cover;
}
.modify {
    width: 102px;
    height: 50px;
    background-image: url("/modify.png");
    background-size: cover;
}
.dialog-footer {
    width: 100%;
    height: 80px;
    display: flex;
    justify-content: space-evenly;
}
</style>
