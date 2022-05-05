<template>
  <div class="table-container">
    <div class="table-header">
      <el-button type="primary" :icon="Plus" @click="add">添加</el-button>
      <el-popconfirm title="确定删除?" @confirm="delArray">
        <template #reference>
          <el-button :icon="Delete" type="danger">批量删除</el-button>
        </template>
      </el-popconfirm>
      <el-upload
        class
        :action="'/api/importTask/'"
        :show-file-list="false"
        :before-upload="handleBeforeUpload"
        :on-success="handleSuccess"
        style="display: inline-block; margin:0 12px;"
      >
        <el-button type="warning" :icon="Upload">导入</el-button>
      </el-upload>
      <el-button type="success" :icon="Download" @click="exportExcel">导出</el-button>
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
      <el-table-column fixed type="selection" width="40" />
      <el-table-column fixed type="index" label="序号" width="60" />
      <el-table-column property="taskSn" label="#" width="60" />
      <el-table-column property="taskTitle" label="任务标题" width="120" />
      <el-table-column property="taskDesc" label="任务描述" width="250" />
      <el-table-column property="reset" label="初始化命令" width="250" />
      <el-table-column property="command" label="检查命令" width="250" />
      <el-table-column property="answer" label="参考答案" width="250" />
      <el-table-column fixed="right" label="操作" width="120">
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
    <el-form :model="task">
      <el-form-item label="任务编号">
        <el-input v-model="task.taskSn" placeholder="请输入任务编号" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="任务标题">
        <el-input v-model="task.taskTitle" placeholder="请输入任务标题" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="任务描述">
        <el-input v-model="task.taskDesc" type="textarea" placeholder="请输入任务描述" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="初始化命令">
        <el-input v-model="task.reset" type="textarea" placeholder="请输入初始化命令" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="检查命令">
        <el-input v-model="task.command" type="textarea" placeholder="请输入检查命令" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="参考答案">
        <el-input v-model="task.answer" type="textarea" placeholder="请输入参考答案" autocomplete="off"></el-input>
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
const type = ref("修改任务信息")
const tableData = ref(null)

// 是否显示对话框
const dialogFormVisible = ref(false)
// 是否显示加载中
const loading = ref(false)

// 任务模型
const task = reactive({
  index: '',
  id: '',
  taskSn: '',
  taskTitle: '',
  taskDesc: '',
  reset: '',
  command: '',
  answer: '',
  errorMessage: '',
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

// 导出到excel
const exportExcel = () => {
  const { href } = router.resolve({
    path: '/api/task/export/' + props.data.classId
  });
  window.open(href);
}

// 当修改了多选项
const selsChange = (obj) => {
  console.log(obj.$index)
  console.log(obj)
  multipleSelection.value = obj
}


// 修改任务信息
const edit = (obj) => {
  type.value = '任务信息';
  
  task.index = obj.$index;
  task.id = obj.row.id;
  task.taskSn = obj.row.taskSn;
  task.taskTitle = obj.row.taskTitle;
  task.taskDesc = obj.row.taskDesc;
  task.reset = obj.row.reset;
  task.command = obj.row.command;
  task.answer = obj.row.answer;
  task.errorMessage = obj.row.errorMessage;
  dialogFormVisible.value = true
}

// 添加任务
const add = (obj) => {
  type.value = '添加任务';
  task.index = task.id = task.taskSn = task.taskTitle = task.taskDesc = task.reset = task.command = task.answer = task.errorMessage = task.score = '';
  dialogFormVisible.value = true
}

// 提交
const submit = () => {
  if (type.value == '添加任务') {
    axios
      .post('/api/task/add', task)
      .then((response) => {
        console.log(response)
        dialogFormVisible.value = false
        task.id = response.data.data
        multipleTableRef.value.data.push(deepClone(task));
      })
      .catch(function (error) { // 请求失败处理
        console.log(error);
      });
  } else {
    axios
      .post('/api/task/update', task)
      .then((response) => {
        dialogFormVisible.value = false
        multipleTableRef.value.data[task.index] = deepClone(task)
      })
      .catch(function (error) { // 请求失败处理
        console.log(error);
      });
  }
}

// 删除单个
const del = (obj) => {
  axios
    .get('/api/task/delete', {
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
      .get('/api/task/batchDelete', {
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
    .get('/api/task/queryList')
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