<template>
  <div class="table-container">
    <div class="table-header">
      <el-button type="primary" :icon="Plus" @click="add">添加</el-button>
      <el-popconfirm title="确定删除?" @confirm="delArray">
        <template #reference>
          <el-button :icon="Delete" type="danger">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>
    <el-table
      ref="multipleTableRef"
      :data="tableData"
      style="width: 100%"
      border
      v-loading="loading"
      @selection-change="selsChange"
      :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="80" />
      <el-table-column property="title" label="关卡标题" width="150" />
      <el-table-column property="difficulty" label="关卡难度" width="100" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="text" size="small" @click.prevent="edit(scope)">修改</el-button>

          <el-popconfirm title="确定删除?" @confirm="del(scope)">
            <template #reference>
              <el-button type="text" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-dialog v-model="dialogFormVisible" :title="type">
    <el-form :model="level">
      <el-form-item label="关卡标题">
        <el-input v-model="level.title" placeholder="请输入关卡标题" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="关卡难度">
        <el-input v-model="level.difficulty" placeholder="请输入关卡难度" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false;">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from "vue-router"
import axios from 'axios'
import {
  Plus,
  Upload,
  Download,
  Delete,
} from '@element-plus/icons-vue'


// 对外暴露属性
const props = defineProps({
  data: Number
})
// 对外暴露方法
const emit = defineEmits(['manageStu'])

// 路由
const router = useRouter();
const type = ref("修改关卡信息")
const tableData = ref(null)

// 是否显示对话框
const dialogFormVisible = ref(false)
// 是否显示加载中
const loading = ref(false)

// 关卡模型
const level = reactive({
  index: '',
  id: '',
  title: '',
  difficulty: ''
})

// 保存多选项
const multipleSelection = ref({})

// 表格
const multipleTableRef = ref(null)

// 上传前
const handleBeforeUpload = (obj) => {
  loading.value = true
}

// 上传成功后
const handleSuccess = (obj) => {
  emit("manageStu", props.data.classId);
  loading.value = false
}

// 当修改了多选项
const selsChange = (obj) => {
  console.log(obj.$index)
  console.log(obj)
  multipleSelection.value = obj
}

// 修改任务信息
const edit = (obj) => {
  type.value = '关卡信息';

  level.index = obj.$index;
  level.id = obj.row.id;
  level.title = obj.row.title;
  level.difficulty = obj.row.difficulty;
  dialogFormVisible.value = true
}

// 添加任务
const add = (obj) => {
  type.value = '添加关卡';
  level.index = level.id = level.title = level.difficulty = '';
  dialogFormVisible.value = true
}

// 提交
const submit = () => {
  if (type.value == '添加关卡') {
    axios
      .post('/api/task/addLevel', level)
      .then((response) => {
        console.log(response)
        dialogFormVisible.value = false
        level.id = response.data.data
        multipleTableRef.value.data.push(deepClone(level));
      })
      .catch(function (error) { // 请求失败处理
        console.log(error);
      });
  } else {
    axios
      .post('/api/task/updateLevel', level)
      .then((response) => {
        dialogFormVisible.value = false
        multipleTableRef.value.data[level.index] = deepClone(level)
      })
      .catch(function (error) { // 请求失败处理
        console.log(error);
      });
  }
}

// 删除单个
const del = (obj) => {
  axios
    .get('/api/task/deleteLevel', {
      params: {
        id: obj.row.id
      }
    })
    .then((response) => {
      console.log(response)
      multipleTableRef.value.data.splice(obj.$index, 1)

      dialogFormVisible.value = false
    })
    .catch(function (error) { // 请求失败处理
      console.log(error);
    });
}

// 批量删除
const delArray = () => {
  let delarr = [];

  console.log(multipleSelection.value)

  for (let i = 0; i < multipleSelection.value.length; i++) {
    delarr.push(multipleSelection.value[i].id);
  }

  if (delarr.length > 0) {
    let ids = delarr.join(",")
    console.log(ids)

    axios
      .get('/api/task/batchDeleteLevel', {
        params: {
          ids: ids
        }
      })
      .then((response) => {
        console.log(response)
        for (let i = multipleTableRef.value.data.length - 1; i >= 0; i--) {
          if (delarr.indexOf(multipleTableRef.value.data[i].id) > -1) {
            multipleTableRef.value.data.splice(i, 1)
          }
        }
        dialogFormVisible.value = false
      })
      .catch(function (error) { // 请求失败处理
        console.log(error);
      });
  }
}

const deepClone = (obj) => {
  return JSON.parse(JSON.stringify(obj));
}

onMounted(() => {

  axios
    .get('/api/task/queryLevelList')
    .then((response) => {
      console.log(response.data)
      tableData.value = response.data.data

    })
    .catch(function (error) { // 请求失败处理
      console.log(error);
    });
})
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