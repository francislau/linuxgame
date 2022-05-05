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
      :data="data.tableData"
      style="width: 100%"
      border
      v-loading="loading"
      @selection-change="selsChange"
      :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="80" />
      <el-table-column property="name" label="班级名称" width="150" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
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
    <el-form>
      <el-form-item label="班级名称">
        <el-input
          v-model="classes.name"
          placeholder="请输入名称"
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
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { Plus, Upload, Download, Delete } from "@element-plus/icons-vue";

// 对外暴露属性
const props = defineProps({
  data: Object,
});
// 对外暴露方法
const emit = defineEmits(["manageStu"]);

// 路由
const router = useRouter();
const type = ref("修改班级名称");

// 是否显示对话框
const dialogFormVisible = ref(false);
// 是否显示加载中
const loading = ref(false);

// 班级模型
const classes = reactive({
  index: "",
  id: "",
  name: "",
});

// 保存多选项
const multipleSelection = ref({});

// 表格
const multipleTableRef = ref(null);

// 上传前
const handleBeforeUpload = (obj) => {
  loading.value = true;
};

// 当修改了多选项
const selsChange = (obj) => {
  console.log(obj.$index);
  console.log(obj);
  multipleSelection.value = obj;
};

// 修改班级信息
const edit = (obj) => {
  type.value = "班级名称";

  classes.index = obj.$index;
  classes.id = obj.row.id;
  classes.name = obj.row.name;
  dialogFormVisible.value = true;
};

// 添加班级
const add = (obj) => {
  type.value = "添加班级";
  classes.index = classes.id = classes.name = "";
  dialogFormVisible.value = true;
};

// 提交
const submit = () => {
  if (type.value == "添加班级") {
    let id = localStorage.getItem("id");
    if (id) {
      classes.userId = id;
      axios
        .post("/api/classes/add", classes)
        .then((response) => {
          console.log(response);
          dialogFormVisible.value = false;
          classes.id = response.data;
          multipleTableRef.value.data.push(deepClone(classes));
        })
        .catch(function (error) {
          // 请求失败处理
          console.log(error);
        });
    }
  } else {
    axios
      .post("/api/classes/update", classes)
      .then((response) => {
        dialogFormVisible.value = false;
        multipleTableRef.value.data[classes.index] = deepClone(classes);
      })
      .catch(function (error) {
        // 请求失败处理
        console.log(error);
      });
  }
};

// 删除单个
const del = (obj) => {
  axios
    .get("/api/classes/delete", {
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
      .get("/api/classes/batchDelete", {
        params: {
          ids: ids,
        },
      })
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