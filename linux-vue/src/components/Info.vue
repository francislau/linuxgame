<template>
    <div class="container">
        <div class="input-label">
            <img src="/password.png" />
        </div>
        <div class="input">
            <input v-model="connPass" readonly />
        </div>

        <div class="input-label">
            <img src="/ysh.png" />
        </div>
        <div class="input">
            <textarea v-model="device.keyCode" readonly></textarea>
        </div>

        <div class="input-label">
            <img src="/local.png" />
        </div>
        <div class="input">
            <textarea v-model="keyCode" readonly></textarea>
        </div>
    </div>
    <div class="dialog-footer">
        <div class="check" @click="checkConn"></div>
        <div class="submit" @click="submit"></div>
    </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, reactive, ref, watch } from 'vue'
import { utils } from '../assets/js/utils';
import { uuid } from 'vue-uuid';

const emit = defineEmits(['close']);

const device = ref({})
const connPass = ref("")
const keyCode = ref("")
const waitKeyCode = ref({});

const keyCodeCallback = (res) => {
    console.log(res);
    console.log(waitKeyCode.value[res.uuid]);

    if (waitKeyCode.value[res.uuid] != undefined) {
        utils.close(waitKeyCode.value[res.uuid]?.loading);

        if (res.keyCode == -2) {
            utils.alert("密码错误，请检查配置root的密码是否为" + device.value.connPass)
            waitKeyCode.value[res.uuid].keyCode = "连接失败，密码有误";
        } else {
            waitKeyCode.value[res.uuid].keyCode = res.keyCode;
        }
    }




}
defineExpose({ keyCodeCallback })

const checkConn = () => {


	let uuid1 = uuid.v1().replace(new RegExp('-','g'),"");
    axios
        .get('/api/usertask/getKeyCode/' + localStorage.getItem('id') + '/' + uuid1)
        .then((response) => {
            console.log(response.data);
            if (response.data.data == 1) {
                waitKeyCode.value[uuid1] = {
                    loading: utils.alert("正在连接，如果长时间无反应，请手动刷新页面", false, false),
                    keyCode: keyCode
                }
                console.log(waitKeyCode.value)
            } else {
                utils.alert("连接失败，请检查环境和代理器");
            }
        })
        .catch(function (error) { // 请求失败处理
            console.log(error);
        });
}


const submit = () => {

    if (keyCode.value == device.value.keyCode) {
        let re = utils.alert("设备码没变，无需要提交修改", false, false);
        setTimeout(() => {
            utils.close(re);
        }, 1000);
        return false;
    }
    if (keyCode.value == "连接失败，密码有误" || keyCode.value == "") {
        let re = utils.alert("请先连接获取设备码", false, false);
        setTimeout(() => {
            utils.close(re);
        }, 1000);
        return false;
    }

    utils.alert("确定要修改设备码吗？", true, true, () => {
        axios
            .post('/api/student/updateVerify', {
                id: localStorage.getItem('id'),
                userDevice: {
                    // connUnverified: JSON.stringify(conn.value),
                    keyCodeUnverified: keyCode.value,
                }
            })
            .then((response) => {
                console.log(response.data);
                utils.alert(response.data.message, true, true, () => {
                    if (response.data.result == 'success') {
                        emit("close");
                    }
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
            device.value = response.data.data.userDevice;
            connPass.value = response.data.data.userDevice.connPass + "（密码不可修改）"
        })
        .catch(function (error) { // 请求失败处理
            console.log(error);
        });

    checkConn();

})

</script>

<style scoped>
.container {
    width: 100%;
    height: 100%;
}
.input-label {
    margin-top: 15px;
}
.input-label img {
    width: 100px;
}
input,
textarea {
    border-radius: 10px;
    border: 3px solid #c0a920;
    background-color: #7d0000;
    margin-top: 10px;
    width: 60%;
    height: 38px;
    color: #ddd;
    font-size: 16px;
    font-weight: bolder;
    padding: 0 10px;
    overflow: hidden;
    resize: none;
}
input::placeholder {
    color: #999;
}
.check {
    width: 100px;
    height: 50px;
    background-image: url("/connection.png");
    background-size: contain;
    background-repeat: no-repeat;
}
.submit {
    width: 100px;
    height: 50px;
    background-image: url("/submit.png");
    background-size: contain;
    background-repeat: no-repeat;
}
.dialog-footer {
    width: 100%;
    height: 80px;
    display: flex;
    justify-content: space-evenly;
}
</style>
