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
        :action="'/api/student/import/' + props.data"
        :show-file-list="false"
        :before-upload="handleBeforeUpload"
        :on-success="handleSuccess"
        style="display: inline-block; margin: 0 12px"
      >
        <el-button type="warning" :icon="Upload">导入</el-button>
      </el-upload>
      <el-button type="success" :icon="Download" @click="exportExcel"
        >导出</el-button
      >
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
      <el-table-column property="stuNo" label="学号" width="150" />
      <el-table-column property="realname" label="姓名" width="150" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-popover
            placement="top"
            :width="100"
            trigger="hover"
            content="恢复密码为666666"
          >
            <template #reference>
              <el-button
                type="text"
                size="small"
                @click.prevent="resetPsw(scope.row.id)"
                >恢复密码</el-button
              >
            </template>
          </el-popover>

          <el-button type="text" size="small" @click.prevent="edit(scope)"
            >修改</el-button
          >

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
    <el-form :model="student">
      <el-form-item label="学号">
        <el-input
          v-model="student.stuNo"
          placeholder="请输入学号"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input
          v-model="student.realname"
          placeholder="请输入姓名"
          autocomplete="off"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import {
  ref,
  reactive,
  computed,
  watch,
  onUpdated,
  onMounted,
  onUnmounted,
} from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { Plus, Upload, Download, Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

// 对外暴露属性
const props = defineProps({
  data: Number,
});
const tableData = ref(null);

const load = () => {
  axios
    .get("/api/student/queryListByClassId", {
      params: {
        id: props.data,
      },
    })
    .then((response) => {
      tableData.value = response.data.data;
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

onMounted(() => {
  load();
});

// 对外暴露方法
const emit = defineEmits(["manageStu"]);

// 路由
const router = useRouter();
const type = ref("修改学生信息");

// 是否显示对话框
const dialogFormVisible = ref(false);
// 是否显示加载中
const loading = ref(false);

// 学生模型
const student = reactive({
  index: "",
  id: "",
  realname: "",
  stuNo: "",
  stuClass: "",
});

// 保存多选项
const multipleSelection = ref({});

// 表格
const multipleTableRef = ref(null);

// 上传前
const handleBeforeUpload = (obj) => {
  loading.value = true;
};

// 上传成功后
const handleSuccess = (obj) => {
  load();
  loading.value = false;
};

// 导出到excel
const exportExcel = () => {
  const { href } = router.resolve({
    path: "/api/student/export/" + props.data,
  });
  window.open(href);
};

// 当修改了多选项
const selsChange = (obj) => {
  console.log(obj.$index);
  console.log(obj);
  multipleSelection.value = obj;
};

// 修改学生信息
const edit = (obj) => {
  type.value = "学生信息";

  student.index = obj.$index;
  student.id = obj.row.id;
  student.realname = obj.row.realname;
  student.stuNo = obj.row.stuNo;
  dialogFormVisible.value = true;
};

// 添加学生
const add = (obj) => {
  type.value = "添加学生";
  student.index = student.id = student.realname = student.stuNo = "";
  student.stuClass = props.data;

  dialogFormVisible.value = true;
};

// 提交
const submit = () => {
  console.log(student);

  if (type.value == "添加学生") {
    console.log(student);
    axios
      .post("/api/student/add", student)
      .then((response) => {
        console.log(response);
        dialogFormVisible.value = false;
        student.id = response.data.data;
        multipleTableRef.value.data.push(deepClone(student));
      })
      .catch(function (error) {
        // 请求失败处理
        console.log(error);
      });
  } else {
    axios
      .post("/api/student/update", student)
      .then((response) => {
        dialogFormVisible.value = false;
        multipleTableRef.value.data[student.index] = deepClone(student);
      })
      .catch(function (error) {
        // 请求失败处理
        console.log(error);
      });
  }
};

// 恢复密码666666
const resetPsw = (id) => {
  axios
    .get("/api/student/resetPsw/" + id)
    .then((response) => {
      console.log(response);
      if (response.data.result == "success") {
        ElMessage.success("密码恢复成功");
      }
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

// 删除单个
const del = (obj) => {
  axios
    .get("/api/student/delete", {
      params: {
        id: obj.row.id,
      },
    })
    .then((response) => {
      console.log(response);
      multipleTableRef.value.data.splice(obj.$index, 1);

      dialogFormVisible.value = false;
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

// 批量删除
const delArray = () => {
  let delarr = [];

  console.log(multipleSelection.value);

  for (let i = 0; i < multipleSelection.value.length; i++) {
    delarr.push(multipleSelection.value[i].id);
  }

  if (delarr.length > 0) {
    let ids = delarr.join(",");
    console.log(ids);

    axios
      .get("/api/student/batchDelete/" + ids)
      .then((response) => {
        console.log(response);
        for (let i = multipleTableRef.value.data.length - 1; i >= 0; i--) {
          if (delarr.indexOf(multipleTableRef.value.data[i].id) > -1) {
            multipleTableRef.value.data.splice(i, 1);
          }
        }
        dialogFormVisible.value = false;
      })
      .catch(function (error) {
        // 请求失败处理
        console.log(error);
      });
  }
};

const deepClone = (obj) => {
  return JSON.parse(JSON.stringify(obj));
};
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