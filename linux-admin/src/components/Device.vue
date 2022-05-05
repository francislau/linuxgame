<template>
  <div class="table-container">
    <div class="table-header">
      <el-button :icon="Check" type="primary" @click="batchVerify">批量通过</el-button>
    </div>
    <el-table
      ref="multipleTableRef"
      :data="tableData"
      style="width: 100%"
      border
      v-loading="loading"
      @selection-change="selsChange"
      :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
      :cell-style="cellStyle"
    >
      <el-table-column fixed type="selection" width="40" />
      <el-table-column fixed type="index" label="序号" width="40" />
      <el-table-column fixed property="stuNo" label="学号" width="80" />
      <el-table-column fixed property="realname" label="姓名" width="80" />
      <!-- <el-table-column label="现用主机" width="150">
        <template #default="scope">
          <div v-if="scope.row.userDevice && scope.row.userDevice.conn">
            <div>host:{{ JSON.parse(scope.row.userDevice.conn).ip }}:{{ JSON.parse(scope.row.userDevice.conn).port }}</div>
            <div>username:{{ JSON.parse(scope.row.userDevice.conn).username }}</div>
            <div>password:{{ scope.row.userDevice.connPass }}</div>
          </div>
        </template>
      </el-table-column>-->
      <el-table-column property="userDevice.keyCode" label="现用设备码" width="350" />
      <!-- <el-table-column label="待审核主机" width="150">
        <template #default="scope">
          <div v-if="scope.row.userDevice && scope.row.userDevice.connUnverified">
            <div>host:{{ JSON.parse(scope.row.userDevice.connUnverified).ip }}:{{ JSON.parse(scope.row.userDevice.connUnverified).port }}</div>
            <div>username:{{ JSON.parse(scope.row.userDevice.connUnverified).username }}</div>
            <div>password:{{ scope.row.userDevice.connPass }}</div>
          </div>
        </template>
      </el-table-column>-->
      <el-table-column property="userDevice.keyCodeUnverified" label="待审核设备码" width="350" />
      <el-table-column property="userDevice.verifyMsg" label="信息" width="150" />
      <el-table-column fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button type="text" size="small" @click.prevent="verifyOne(scope)">通过</el-button>
          <el-button type="text" size="small" @click.prevent="clear(scope)">清空设备码</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from "vue-router"
import axios from 'axios'
import { ElMessage } from 'element-plus'
import {
  Check,
} from '@element-plus/icons-vue'

const cellStyle = ({ row, column, rowIndex, columnIndex }) => {
  if (row.userDevice && row.userDevice.connUnverified) {
    return { background: 'rgb(236, 245, 255)' };
  }

}

const showrow1 = ({ row }) => {
  //console.log(row)
}

// 对外暴露属性
const props = defineProps({
  data: Number
})
const tableData = ref(null)
const classId = ref("")

const load = () => {
  axios
    .get('/api/student/queryListByClassId', {
      params: {
        id: classId.value
      }
    })
    .then((response) => {
      console.log(response)
      tableData.value = response.data.data
    })
    .catch(function (error) { // 请求失败处理
      console.log(error);
    });
}

onMounted(() => {
  classId.value = props.data;
  load();
})

// 对外暴露方法
const emit = defineEmits(['manageStu'])

// 路由
const router = useRouter();
const type = ref("修改学生信息")

// 是否显示对话框
const dialogFormVisible = ref(false)
// 是否显示加载中
const loading = ref(false)

// 学生模型
const student = reactive({
  index: '',
  id: '',
  realname: '',
  stuNo: '',
  stuClass: '',
})

// 保存多选项
const multipleSelection = ref({})

// 表格
const multipleTableRef = ref(null)

// 批量审核
const batchVerify = () => {
  let arr = [];

  for (let i = 0; i < multipleSelection.value.length; i++) {
    arr.push(multipleSelection.value[i].id);
  }

  if (arr.length > 0) {
    let ids = arr.join(",")
    console.log(ids)
    verify(ids, true);
  }
}

// 审核通过
const verifyOne = (obj) => {
  console.log(obj)
  if (obj.row.userDevice.keyCodeUnverified) {
    verify(obj.row.id, false);
  } else {
    ElMessage('无需审核')
  }
}

// 清空设备码
const clear = (obj) => {
  console.log(obj)
  if (obj.row.userDevice.keyCode) {
    axios
      .get('/api/student/clear/' + obj.row.id)
      .then((response) => {
        console.log(response.data)
        if (response.data.result == 'success') {
          load();
          
          ElMessage({
            message: "清空成功",
            type: "success",
          })
        }
      })
      .catch(function (error) { // 请求失败处理
        console.log(error);
      });
  } else {
    ElMessage('无需审核')
  }
}

const verify = (ids, bool) => {

  axios
    .get('/api/student/verify/' + ids)
    .then((response) => {
      console.log(response.data)
      if (response.data.result == 'success') {
        load();
        let msg = "";
        let type = "success";
        if (bool) {
          let no = multipleSelection.value.length - response.data.data;
          if (no == multipleSelection.value.length) {
            msg = "无需审核";
            type = "info"
          } else if (no == 0) {
            msg = response.data.message;
          } else {
            msg = response.data.message + "（其中有" + no + "条无需审核）";
          }

        } else {
          msg = response.data.message;
        }
        
        ElMessage({
          message: msg,
          type: type,
        })
      }
    })
    .catch(function (error) { // 请求失败处理
      console.log(error);
    });
}

// 当修改了多选项
const selsChange = (obj) => {
  console.log(obj.$index)
  console.log(obj)
  multipleSelection.value = obj
}
</script>

<style>
.table-container {
  padding: 10px;
  background-color: #fff;
}
.table-header {
  padding: 10px;
}
</style>