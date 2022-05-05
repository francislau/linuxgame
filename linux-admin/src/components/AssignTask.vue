<template>
  <div class="table-container">
    <div class="table-header">
      <el-button type="primary" :icon="Plus" @click="batchAdd"
        >批量添加</el-button
      >
    </div>
    <el-table
      ref="multipleTableRef"
      :data="tableData"
      style="width: 100%"
      border
      v-loading="loading"
      row-key="id"
      row-class-name="table-row"
      @selection-change="tableSelsChange"
      :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
    >
      <el-table-column type="expand">
        <template #default="props">
          <div>
            <el-tag>任务列表</el-tag>
            <el-tag type="success">已开始</el-tag>
            <el-tag type="info">可删除</el-tag>
          </div>
          <el-divider style="margin: 8px 0"></el-divider>
          <div>
            <el-tag
              @close="onClose(item, index, props.row)"
              :type="
                item.taskStatus == 1 && item.practiseMinStartAt == null
                  ? item.repeat == 1
                    ? 'danger'
                    : 'info'
                  : 'success'
              "
              :closable="
                item.taskStatus == 1 && item.practiseMinStartAt == null
              "
              v-for="(item, index) in props.row.task"
              :key="item"
              >#{{ item.task.taskSn }}</el-tag
            >
          </div>
        </template>
      </el-table-column>
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="80" />
      <el-table-column property="stuNo" label="学号" width="150" />
      <el-table-column property="realname" label="姓名" width="100" />
      <el-table-column property="realname" label="练习情况" width="200">
        <template #default="scope">
          <div class="table-progress">
            <div>已分配: {{ scope.row.practise_length }}</div>
            <el-progress
              :percentage="scope.row.practise_length ? 100 : 0"
              :stroke-width="3"
            />
          </div>
          <div class="table-progress">
            <div>已开始: {{ scope.row.practise_start }}</div>
            <el-progress
              :percentage="
                scope.row.practise_length
                  ? Math.floor(
                      (scope.row.practise_start * 100) /
                        scope.row.practise_length
                    )
                  : 0
              "
              :stroke-width="3"
              color="var(--el-color-success)"
            />
          </div>
          <div class="table-progress">
            <div>已完成: {{ scope.row.practise_finish }}</div>
            <el-progress
              :percentage="
                scope.row.practise_length
                  ? Math.floor(
                      (scope.row.practise_finish * 100) /
                        scope.row.practise_length
                    )
                  : 0
              "
              :stroke-width="3"
              color="var(--el-color-warning)"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column property="realname" label="挑战情况" width="200">
        <template #default="scope">
          <div class="table-progress">
            <div>已分配: {{ scope.row.task_length }}</div>
            <el-progress
              :percentage="scope.row.task_length ? 100 : 0"
              :stroke-width="3"
            />
          </div>
          <div class="table-progress">
            <div>已开始: {{ scope.row.task_start }}</div>
            <el-progress
              :percentage="
                scope.row.task_length
                  ? Math.floor(
                      (scope.row.task_start * 100) / scope.row.task_length
                    )
                  : 0
              "
              :stroke-width="3"
              color="var(--el-color-success)"
            />
          </div>
          <div class="table-progress">
            <div>已完成: {{ scope.row.task_finish }}</div>
            <el-progress
              :percentage="
                scope.row.task_length
                  ? Math.floor(
                      (scope.row.task_finish * 100) / scope.row.task_length
                    )
                  : 0
              "
              :stroke-width="3"
              color="var(--el-color-warning)"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="text" size="small" @click.prevent="add(scope)"
            >添加任务</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>

  <div class="drawer">
    <el-drawer v-model="drawer" size="60%">
      <template #title>
        <h4>
          任务列表(
          {{ drawerMultipleSelection.length + "/" + taskList.length }}
          )
        </h4>
      </template>
      <template #default>
        <el-table
          ref="drawerTableRef"
          :data="taskList"
          @selection-change="drawerSelsChange"
        >
          <el-table-column fixed type="selection" width="40" />
          <el-table-column fixed property="taskSn" label="#" width="60" />
          <el-table-column property="taskTitle" label="任务标题" width="120" />
          <el-table-column property="taskDesc" label="任务描述" />
        </el-table>
      </template>
      <template #footer>
        <div style="flex: auto">
          <el-button type="primary" @click="submit"
            >确定添加给（{{ selection.realname }}）</el-button
          >
          <el-button @click="drawer = false">关闭</el-button>
        </div>
      </template>
    </el-drawer>
  </div>

  <el-dialog v-model="dialogFormVisible" :title="type">
    <el-table :data="taskList">
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="80" />
      <el-table-column property="taskTitle" label="任务标题" width="150" />
      <el-table-column property="taskDesc" label="任务描述" width="200" />
    </el-table>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import axios from "axios";
import { Plus, Upload, Download, Delete } from "@element-plus/icons-vue";
const drawer2 = ref(true);
// 对外暴露属性
const props = defineProps({
  data: Number,
});
const tableData = ref(null);

// 对外暴露方法
const emit = defineEmits(["manageStu"]);

// 路由
const router = useRouter();
const type = ref("修改学生信息");

// 是否显示对话框
const dialogFormVisible = ref(false);
// 是否显示加载中
const loading = ref(false);
// 任务表格
const taskList = ref([]);
// 抽屉
const drawer = ref(false);
// 已选提示
const selection = ref({});

// 获取当前组件实例
const instance = getCurrentInstance();

// 学生模型
const student = reactive({
  index: "",
  id: "",
  realname: "",
  stuNo: "",
  stuClass: "",
});

// 保存多选项
const tableMultipleSelection = ref([]);
const drawerMultipleSelection = ref([]);

// 表格
const multipleTableRef = ref(null);
// drawer里的表格
const drawerTableRef = ref(null);

onMounted(() => {
  axios
    .get("/api/task/queryList")
    .then((response) => {
      // console.log(response)
      taskList.value = response.data.data;
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });

  axios
    .get("/api/student/queryListByClassId", {
      params: {
        id: props.data,
      },
    })
    .then((response) => {
      tableData.value = response.data.data;

      loadTask(0);
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
});

// 同步加载用户的任务
const loadTask = async function (userId) {
  for (let i = 0; i < tableData.value.length; i++) {
    let item = tableData.value[i];

    if (userId > 0 && tableData.value[i].id != userId) {
      continue;
    }

    await axios
      .get("/api/usertask/queryByUserId/1/" + item.id)
      .then((response) => {
        console.log(response.data);
        item.task = response.data.data;
        item.practise_length = 0;
        item.practise_start = 0;
        item.practise_finish = 0;
        item.task_length = 0;
        item.task_start = 0;
        item.task_finish = 0;

        for (let item1 of response.data.data) {
          for (let item2 of response.data.data) {
            if (item1.id != item2.id && item1.task.id == item2.task.id) {
              item1.repeat = 1;
            }
          }
          if (item1.practiseMinStartAt != null) {
            item.practise_start++;
          }
          if (item1.practiseMinPassAt != null) {
            item.practise_finish++;
          }
          if (item1.taskStatus == 2) {
            item.task_start++;
          }
          if (item1.taskStatus == 3) {
            item.task_finish++;
          }
          item.practise_length++;
          item.task_length++;
        }

        //instance.proxy.$forceUpdate();
      });
  }

  // console.log(tableData.value)
};

// 上传前
const handleBeforeUpload = (obj) => {
  loading.value = true;
};

// 上传成功后
const handleSuccess = (obj) => {
  emit("manageStu", props.data.classId);
  loading.value = false;
};

// 导出到excel
const exportExcel = () => {
  const { href } = router.resolve({
    path: "/api/exportStudent/" + props.data.classId,
  });
  window.open(href);
};

// 当修改了表格的多选项
const tableSelsChange = (obj) => {
  console.log(obj);
  tableMultipleSelection.value = obj;
};

// 当修改了任务的多选项
const drawerSelsChange = (obj) => {
  console.log(obj);
  drawerMultipleSelection.value = obj;
};

// 当点击删除任务时
const onClose = (obj, index, row) => {
  console.log(obj);

  axios
    .get("/api/usertask/delete/" + obj.id)
    .then((response) => {
      // console.log(response)

      loadTask(obj.userId);
      console.log(tableData);
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

/*
const loadTaskOne = () => {
  axios.get("/api/usertask/queryByUserId/1/" + item.id).then((response) => {
    console.log(response.data);
    item.task = response.data.data;
    item.practise_length = 0;
    item.practise_start = 0;
    item.practise_finish = 0;
    item.task_length = 0;
    item.task_start = 0;
    item.task_finish = 0;

    for (let item1 of response.data.data) {
      for (let item2 of response.data.data) {
        if (item1.id != item2.id && item1.task.id == item2.task.id) {
          item1.repeat = 1;
        }
      }
      if (item1.practiseMinStartAt != null) {
        item.practise_start++;
      }
      if (item1.practiseMinPassAt != null) {
        item.practise_finish++;
      }
      if (item1.taskStatus == 2) {
        item.task_start++;
      }
      if (item1.taskStatus == 3) {
        item.task_finish++;
      }
      item.practise_length++;
      item.task_length++;
    }

    instance.proxy.$forceUpdate();
  });
};
*/

// 修改学生信息
const edit = (obj) => {
  type.value = "学生信息";

  student.index = obj.$index;
  student.id = obj.row.id;
  student.realname = obj.row.realname;
  student.stuNo = obj.row.stuNo;
  dialogFormVisible.value = true;
};

// 批量添加任务
const batchAdd = () => {
  if (!tableMultipleSelection.value.length) {
    return false;
  }
  selection.value = {
    batchAdd: true,
    realname: "已选" + tableMultipleSelection.value.length + "人",
  };
  drawer.value = true;
};

// 添加任务
const add = (obj) => {
  console.log(obj);
  selection.value = obj.row;
  drawer.value = true;
};

// 提交
const submit = () => {
  console.log(drawerMultipleSelection.value);
  let ids = [];
  // 单独
  if (selection.value.batchAdd == undefined) {
    ids = selection.value.id;
  } else {
    // 多选
    let addarr = [];

    for (let i = 0; i < tableMultipleSelection.value.length; i++) {
      addarr.push(tableMultipleSelection.value[i].id);
    }
    ids = addarr.join(",");
  }

  let select_tasks = drawerMultipleSelection.value.map((item) => {
    return {
      taskId: item.id,
      taskRatio: item.ratio,
    };
  });
  console.log(select_tasks);
  if (select_tasks.length == 0) {
    return false;
  }
  axios
    .post("/api/usertask/add/" + ids, select_tasks)
    .then((response) => {
      ElMessage({
        message: "添加成功，已去除重复任务",
        type: "success",
      });
      console.log(response);
      loadTask(0);
      drawerTableRef.value.clearSelection();
      drawer.value = false;
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

const deepClone = (obj) => {
  return JSON.parse(JSON.stringify(obj));
};
</script>

<style scoped>
.table-container {
  padding: 10px;
  background-color: #fff;
}
.table-header {
  padding: 10px;
}
/* 
.table-row{
    height: 50px;
    transition: all 0.2s;
}
.table-row:hover {
    height: 200px;
} */
.el-tag {
  margin-left: 10px;
  margin-bottom: 10px;
}
/* .drawer >>> .el-drawer {
  padding-bottom: 80px;
} */
.table-progress {
  display: flex;
}
.table-progress > :first-child {
  width: 65px;
  margin-right: 5px;
  font-size: 12px;
}
.el-progress {
  margin-bottom: 1px;
  flex-grow: 1;
}
.el-progress .el-progress__text {
  font-size: 12px !important;
}
.drawer-footer {
  position: absolute;
  bottom: 0px;
  padding: 20px 0;
}
</style>