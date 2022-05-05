<template>
  <div class="table-container">
    <div class="table-header">
      已为<el-tag>{{ props.data.name }}</el-tag
      >开通了<el-tag>{{ multipleSelection.length }}</el-tag
      >个任务，还有<el-tag>{{
        tableData.length - multipleSelection.length
      }}</el-tag
      >个任务未开通。
    </div>
    <el-table
      ref="multipleTableRef"
      :data="tableData"
      style="width: 100%"
      border
      @select="select"
      @select-all="selectAll"
      @selection-change="selectChange"
      :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
    >
      <el-table-column fixed type="selection" width="40" />
      <el-table-column fixed type="index" label="序号" width="60" />
      <el-table-column property="taskSn" label="#" width="60" />
      <el-table-column property="taskTitle" label="任务标题" width="150" />
      <el-table-column property="taskDesc" label="任务描述" width="850" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUpdated } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

// 对外暴露属性
const props = defineProps({
  data: Object,
});

// 路由
const router = useRouter();
const type = ref("修改任务信息");
const tableData = ref([]);

// 表格
const multipleTableRef = ref(null);
const multipleSelection = ref([]);

const selectAll = (rows) => {
  console.log(rows);

  let arr = [];
  for (let i = 0; i < tableData.value.length; i++) {
    const element = tableData.value[i];
    arr.push({
      userId: localStorage.getItem("id"),
      classId: props.data.id,
      taskId: element.id,
      taskOrder: 1,
    });
  }

  if (rows.length) {
    addUserTaskOpen(arr);
  } else {
    deleteUserTaskOpen(arr);
  }
};
const select = (rows, row) => {
  console.log(row);
  console.log(rows);
  let selected = rows.length && rows.indexOf(row) !== -1;

  if (selected) {
    addUserTaskOpen([
      {
        userId: localStorage.getItem("id"),
        classId: props.data.id,
        taskId: row.id,
        taskOrder: 1,
      },
    ]);
  } else {
    deleteUserTaskOpen([
      {
        userId: localStorage.getItem("id"),
        classId: props.data.id,
        taskId: row.id,
      },
    ]);
  }
};

const selectChange = (obj) => {
  multipleSelection.value = obj;
};

const addUserTaskOpen = (data) => {
  axios
    .post("/api/usertask/addUserTaskOpen", data)
    .then((response) => {
      console.log(response.data);
      console.log(multipleTableRef.value);
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

const deleteUserTaskOpen = (data) => {
  axios
    .post("/api/usertask/deleteUserTaskOpen", data)
    .then((response) => {
      console.log(response.data);
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

onMounted(() => {
  console.log(props);

  axios
    .get("/api/task/queryList")
    .then((response) => {
      console.log(response.data);
      tableData.value = response.data.data;

      axios
        .get(
          "/api/usertask/queryUserTaskOpenListByUserIdAndClassId/" +
            localStorage.getItem("id") +
            "/" +
            props.data.id
        )
        .then((response) => {
          console.log(response.data);
          if (response.data) {
            let taskIds = response.data.data.map((item) => {
              return item.taskId;
            });

            for (let i = 0; i < tableData.value.length; i++) {
              const element = tableData.value[i];
              if (taskIds.indexOf(element.id) > -1) {
                multipleTableRef.value.toggleRowSelection(element, true);
              }
            }
          }
        })
        .catch(function (error) {
          // 请求失败处理
          console.log(error);
        });
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
});
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