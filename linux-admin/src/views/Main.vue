<template>
  <el-container>
    <el-header>
      <div>《挑战Linux》后台</div>
      <div>
        <el-dropdown>
          <span>
            {{ realname }}
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="pswDialog = true"
                >修改密码</el-dropdown-item
              >
              <el-dropdown-item @click="logout">退出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-scrollbar>
          <el-menu :default-openeds="['1']" default-active="1">
            <el-menu-item index="1" @click="goHome">
              <el-icon>
                <HomeFilled />
              </el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-sub-menu index="2">
              <template #title>
                <el-icon> <UserFilled /> </el-icon>学生管理
              </template>
              <el-menu-item
                v-for="(class1, index) in class_arr"
                :key="class1"
                :index="'2-' + index"
                @click="manageStu(class1.id)"
                >{{ class1.name }}</el-menu-item
              >
            </el-sub-menu>
            <el-sub-menu index="3">
              <template #title>
                <el-icon> <Platform /> </el-icon>设备管理
              </template>
              <el-menu-item
                v-for="(class1, index) in class_arr"
                :key="class1"
                :index="'3-' + index"
                @click="manageDev(class1.id)"
                >{{ class1.name }}</el-menu-item
              >
            </el-sub-menu>
            <el-sub-menu index="4">
              <template #title>
                <el-icon> <Flag /> </el-icon>练习管理
              </template>
              <el-menu-item
                v-for="(class1, index) in class_arr"
                :key="class1"
                :index="'4-' + index"
                @click="managePractise(class1.id)"
                >{{ class1.name }}</el-menu-item
              >
            </el-sub-menu>
            <el-sub-menu index="5">
              <template #title>
                <el-icon> <Flag /> </el-icon>挑战管理
              </template>
              <el-menu-item
                v-for="(class1, index) in class_arr"
                :key="class1"
                :index="'5-' + index"
                @click="manageChallenge(class1.id)"
                >{{ class1.name }}</el-menu-item
              >
            </el-sub-menu>
            <el-menu-item index="6" @click="manageClass">
              <el-icon>
                <UserFilled />
              </el-icon>
              <span>班级管理</span>
            </el-menu-item>
            <el-sub-menu index="7">
              <template #title>
                <el-icon> <List /> </el-icon>任务管理
              </template>
              <el-menu-item index="7-1" @click="manageLevel">关卡</el-menu-item>
              <el-menu-item index="7-2" @click="manageTask"
                >任务管理</el-menu-item
              >
            </el-sub-menu>
            <el-sub-menu index="8">
              <template #title>
                <el-icon> <List /> </el-icon>任务分配
              </template>
              <el-menu-item
                v-for="(class1, index) in class_arr"
                :key="class1"
                :index="'8-' + index"
                @click="assignTask(class1.id)"
                >{{ class1.name }}</el-menu-item
              >
            </el-sub-menu>
            <el-sub-menu index="9">
              <template #title>
                <el-icon> <List /> </el-icon>开通任务
              </template>
              <el-menu-item
                v-for="(class1, index) in class_arr"
                :key="class1"
                :index="'9-' + index"
                @click="manageOpen(class1.id, class1.name)"
                >{{ class1.name }}</el-menu-item
              >
            </el-sub-menu>
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <el-main>
        <component :data="data" :is="currentView" :key="data"></component>
      </el-main>
    </el-container>
  </el-container>

  <el-dialog v-model="pswDialog" title="修改密码">
    <el-form :model="form">
      <el-form-item label="请输新密码">
        <el-input
          v-model="form.password"
          type="password"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="请再输一次">
        <el-input
          v-model="form.password2"
          type="password"
          autocomplete="off"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="pswDialog = false">取消</el-button>
        <el-button type="primary" @click="chgPsw">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, ref, shallowRef } from "vue";
import { ArrowDown } from "@element-plus/icons-vue";
import axios from "axios";
import student from "../components/Student.vue";
import practise from "../components/Practise.vue";
import challenge from "../components/Challenge.vue";
import device from "../components/Device.vue";
import classes from "../components/Class.vue";
import task from "../components/Task.vue";
import open from "../components/Open.vue";
import level from "../components/Level.vue";
import assignTaskV from "../components/AssignTask.vue";
import home from "../components/Home.vue";
import {
  User,
  Flag,
  UserFilled,
  Platform,
  HomeFilled,
  List,
} from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const data = ref(null);
const realname = ref("");
const router = useRouter();
const pswDialog = ref(false);

const currentView = shallowRef(null);
const class_arr = ref(null);
const form = ref({
  password: "",
  password2: "",
});

// home
const goHome = () => {
  currentView.value = home;
};

// 分配任务
const assignTask = (id) => {
  data.value = id;
  currentView.value = assignTaskV;
};

// 管理关卡
const manageLevel = () => {
  currentView.value = level;
};

// 管理任务
const manageTask = () => {
  currentView.value = task;
};

// 管理开通任务
const manageOpen = (id, name) => {
  data.value = {id:id,name:name};
  currentView.value = open;
};

// 管理学生
const manageStu = (id) => {
  data.value = id;
  currentView.value = student;
};

// 管理设备
const manageDev = (id) => {
  data.value = id;
  currentView.value = device;
};
// 管理练习
const managePractise = (id) => {
  data.value = id;
  currentView.value = practise;
};
// 管理挑战
const manageChallenge = (id) => {
  data.value = id;
  currentView.value = challenge;
};
// 班级管理
const manageClass = () => {
  data.value = {
    tableData: class_arr.value,
  };
  currentView.value = classes;
};

const logout = () => {
  localStorage.clear();
  router.push("/login");
};

// 加载班级信息
const loadClass = () => {
  let id = localStorage.getItem("id");
  if (id) {
    axios
      .get("/api/classes/queryListByUserId/" + id)
      .then((response) => {
        console.log(response.data);
        class_arr.value = response.data;

        if (response.data.length > 0) {
          // manageStu(response.data[0].id)
        }
      })
      .catch(function (error) {
        // 请求失败处理
        console.log(error);
      });
  }
};

const chgPsw = () => {
  console.log(form.value);
  if (form.value.password == "" || form.value.password2 == "") {
    ElMessage.error("请输入密码和确认密码");
    return false;
  }
  if (form.value.password.length < 6) {
    ElMessage.error("密码不能小于六位");
    return false;
  }
  if (form.value.password != form.value.password2) {
    ElMessage.error("密码和确认密码不一致");
    return false;
  }
  if (form.value.password != "" && form.value.password2 != "") {
    axios
      .post("/api/user/chgPsw", {
        id: localStorage.getItem("id"),
        password: form.value.password,
      })
      .then((response) => {
        console.log(response.data);
        if (response.data.result == "success") {
          pswDialog.value = false;
        }
      })
      .catch(function (error) {
        // 请求失败处理
        console.log(error);
      });
  }
};

onMounted(() => {
  loadClass();
  realname.value = "欢迎你，" + localStorage.getItem("realname");
  currentView.value = home;
});
</script>

<style scoped>
.el-header {
  background-color: #b3c0d1;
  line-height: 60px;
  display: flex;
  justify-content: space-between;
}
.el-header > div {
  display: flex;
}
.el-header >>> .el-dropdown {
  line-height: 60px;
}
.logout {
  cursor: pointer;
  margin-right: 20px;
}
.el-footer {
  line-height: 60px;
}

.el-aside {
  background-color: #d3dce6;
  color: var(--el-text-color-primary);
  text-align: center;
  line-height: 200px;
}

.el-main {
  background-color: #e9eef3;
}

body > .el-container {
  margin-bottom: 40px;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}
</style>