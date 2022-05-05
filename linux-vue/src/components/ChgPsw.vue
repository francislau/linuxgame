<template>
  <div class="container">
    <div class="tips">第一次登录请设置新密码</div>
    <div class="input">
      <input v-model="form.password" type="password" placeholder="请输入密码" />
    </div>
    <div class="input">
      <input v-model="form.password2" type="password" placeholder="请再次输入密码"
      />
    </div>
  </div>
  <div class="dialog-footer">
    <div class="modify" @click="modify"></div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { utils } from "../assets/js/utils.js";
import axios from "axios";

const emit = defineEmits(["close"]);

// 学生模型
const form = reactive({
  password: "",
  password2: "",
});

const modify = function () {
  if (form.password == "") {
    utils.alert("请输入密码");
    return false;
  }
  if (form.password == "666666") {
    utils.alert("不可以设置密码为666666");
    return false;
  }
  if (form.password.length < 6) {
    utils.alert("密码不能少于6位");
    return false;
  }
  if (form.password2 == "") {
    utils.alert("请再次输入密码");
    return false;
  }
  if (form.password != form.password2) {
    utils.alert("两次密码不一致");
    return false;
  }

  axios
    .post("/api/student/chgPsw", {
      id: localStorage.id,
      password: form.password
    })
    .then((response) => {
      console.log(response.data);
      if (response.data.result == "success") {
        localStorage.removeItem("chgPsw");
        emit("close");
      } else {
        utils.alert(response.data.message);
      }
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};
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
.modify {
  width: 100px;
  height: 50px;
  background-image: url("/modify.png");
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
.tips {
  color: #ddd;
  font-size: 20px;
}
.dialog-footer {
  width: 100%;
  height: 80px;
  margin-top: 40px;
  display: flex;
  justify-content: space-evenly;
}
</style>
