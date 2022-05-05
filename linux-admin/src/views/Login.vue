<template>
  <!--引入粒子特效-->
  <Particles id="tsparticles" :options="options" />
  <el-form class="login">
    <div class="title">《挑战Linux》后台管理系统</div>
    <el-form-item label="用户名">
      <el-input v-model="user.username" placeholder="请输入用户名" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item label="密　码">
      <el-input v-model="user.password" placeholder="请输入密码" type="password" autocomplete="off" @keyup.enter="login"></el-input>
    </el-form-item>
    <el-button @click="login">登录</el-button>
  </el-form>
</template>
 
<script setup>
import { defineComponent, reactive, ref } from "vue";
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter();
const user = ref({
  username: '',
  password: ''
})

const options = {
  background: {
    color: {
      value: "#000",//粒子颜色
    },
  },
  fpsLimit: 60,
  interactivity: {
    events: {
      onClick: {
        enable: true,
        mode: "push",//可用的click模式有: "push", "remove", "repulse", "bubble"。
      },
      onHover: {
        enable: true,
        mode: "grab",//可用的hover模式有: "grab", "repulse", "bubble"。
      },
      resize: true,
    },
    modes: {
      bubble: {
        distance: 400,
        duration: 2,
        opacity: 0.8,
        size: 40,
      },
      push: {
        quantity: 4,
      },
      repulse: {
        distance: 200,
        duration: 0.4,
      },
    },
  },
  particles: {
    color: {
      value: "#EEE",
    },
    links: {
      color: "#EEE",//'#dedede'。线条颜色。
      distance: 150,//线条长度
      enable: true,//是否有线条
      opacity: 0.5,//线条透明度。
      width: 1,//线条宽度。
    },
    collisions: {
      enable: false,
    },
    move: {
      direction: "none",
      enable: true,
      outMode: "bounce",
      random: false,
      speed: 2,//粒子运动速度。
      straight: false,
    },
    number: {
      density: {
        enable: true,
        area: 800,
      },
      value: 40,//粒子数量。
    },
    opacity: {
      value: 0.3,//粒子透明度。
    },
    shape: {
      type: "circle", //可用的粒子外观类型有："circle","edge","triangle", "polygon","star"
    },
    size: {
      random: true,
      value: 5,
    },
  },
  detectRetina: true,
};

const login = () => {
  console.log(user.value)

  axios
    .post('/api/user/login', user.value)
    .then((response) => {
      console.log(response)
      if (response.data.result == 'success') {
        localStorage.setItem("id", response.data.data.id);
        localStorage.setItem("realname", response.data.data.realname);
        localStorage.setItem("token", response.data.data.token);

        ElMessage({
          message: '登录成功',
          type: 'success',
        })

        router.push('/');
      } else {
        ElMessage(response.data.message)
      }
    })
    .catch(function (error) { // 请求失败处理
      console.log(error);
    });
}
</script>

<style scoped>
.title {
  font-weight: bolder;
  height: 60px;
}
.login {
  width: 300px;
  height: 300px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff !important;
  text-align: center;
}
.login >>> .el-form-item__label {
  color: #fff;
}
.login >>> .el-input__inner {
  background-color: transparent !important;
  border-color: #20b2aa !important;
  color: #fff;
}
</style>