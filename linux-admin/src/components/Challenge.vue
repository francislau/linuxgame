<template>
  <div class="table-container">
    <div class="table-header">
      <el-button type="success" :icon="Download" @click="exportExcel">导出成绩</el-button>
      <el-select v-model="select" style="float: right;" placeholder="Select">
        <el-option key="score" label="参考分数" value="score"></el-option>
        <el-option key="detail" label="详情" value="detail"></el-option>
      </el-select>
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
      <el-table-column fixed type="selection" width="55" />
      <el-table-column fixed property="stuNo" label="学号" width="80" />
      <el-table-column fixed property="realname" label="姓名" width="80" />
      <el-table-column
        v-for="(item, index) in headerTask"
        :property="'userTask.' + index"
        :label="'#' + item.label"
        :width="select == 'score' ? 80 : 150"
        :key="index"
      >
        <template #header="scope">
          <el-popover placement="top" title :width="450" trigger="hover">
            <template #reference>
              <div>
                <div>{{ scope.column.label }}</div>
              </div>
            </template>
            <div>
              #{{ headerTask[scope.column.property.replaceAll("userTask.", "")].task.taskSn }}
              {{ headerTask[scope.column.property.replaceAll("userTask.", "")].task.taskTitle }}
            </div>
            {{ headerTask[scope.column.property.replaceAll("userTask.", "")].task.taskDesc }}
          </el-popover>
        </template>
        <template #default="scope">
          <div v-if="select == 'score'">
            <el-popover
              v-if="scope.row.userTask[index]"
              placement="bottom-start"
              title
              :width="460"
              trigger="hover"
            >
              <template #reference>
                <div>{{ scope.row.userTask[index].taskScore }}</div>
              </template>
              <div>分数：100分 = 70+完成时间排名得分+完成用时排名得分+尝试次数得分</div>
              <div v-if="scope.row.userTask[index].taskScore > 0">
                分数：{{ scope.row.userTask[index].taskScore }}分 = 70
                +
                10×({{ total.pass[index].total }}-{{ scope.row.userTask[index].passRank }})/({{ total.pass[index].total }}-1)
                +
                10×({{ total.length[index].total }}-{{ scope.row.userTask[index].lengthRank }})/({{ total.length[index].total }}-1)
                +
                10×({{ total.attempt[index].total }}-{{ scope.row.userTask[index].attemptRank }})/({{ total.attempt[index].total }}-1)
              </div>
              <div v-else>分数：0分 = 未完成</div>
              <div>提交最快的是：{{ total.pass[index].top }}</div>
              <div>用时最短的是：{{ total.length[index].top }}</div>
              <div>做得最多次的是：{{ total.attempt[index].top }}</div>
            </el-popover>
          </div>
          <div class="cell-style" v-else-if="scope.row.userTask[index]">
            <el-popover v-if="scope.row.userTask[index].passAt" placement="bottom-start" title :width="220" trigger="hover">
              <template #reference>
                <div class="cell-style">
                  <div>完成时间：{{ scope.row.userTask[index].passAt ? utils.timeFormat(scope.row.userTask[index].passAt, 'date') : '' }}</div>
                  <div>　　用时：{{ utils.lengthFormat(scope.row.userTask[index].length) }}</div>
                  <div>尝试次数：{{ scope.row.userTask[index].attempt }}</div>
                </div>
              </template>
              <div>
                <div>完成时间：{{ scope.row.userTask[index].passAt ? utils.timeFormat(scope.row.userTask[index].passAt, 'datetime') : '' }}</div>
                <div>　　开始：{{ scope.row.userTask[index].startAt ? utils.timeFormat(scope.row.userTask[index].startAt) : '' }}</div>
                <div>　　结束：{{ scope.row.userTask[index].passAt ? utils.timeFormat(scope.row.userTask[index].passAt) : '' }}</div>
              </div>
            </el-popover>
            <div v-else>未完成</div>
            <!-- {{ scope.row.userTask[index] ? utils.timeFormat(scope.row.userTask[index].practiseMinStartAt, scope.row.userTask[index].practiseMinPassAt) + '×' + scope.row.userTask[index].practiseAttempt : '' }} -->
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-dialog v-model="dialogFormVisible" :title="type">
    <el-form :model="student">
      <el-form-item label="学号">
        <el-input v-model="student.stuNo" placeholder="请输入学号" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="student.realname" placeholder="请输入姓名" autocomplete="off"></el-input>
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
import { export_json_to_excel } from '../assets/js/Export2Excel'
import { useRouter } from "vue-router"
import { utils } from '../assets/js/utils';
import axios from 'axios'
import {
  Plus,
  Upload,
  Download,
  Delete,
} from '@element-plus/icons-vue'




const formatJson = (filterVal, jsonData) => {
  return jsonData.map(v => filterVal.map(j => v[j]))
}


const exportExcel = () => {
  console.log(tableData.value)
  let exportData = [];
  let header = ['stuNo', 'realname'];
  let tHeader = ['学号', '姓名']
  for (const item in headerTask.value) {
    header.push(item);
    tHeader.push("#" + headerTask.value[item].label);
  }
  for (let i = 0; i < tableData.value.length; i++) {
    let temp = {}
    temp['stuNo'] = tableData.value[i].stuNo;
    temp['realname'] = tableData.value[i].realname;

    for (const item in headerTask.value) {
      temp[item] = tableData.value[i]['userTask'][item]?.taskScore
    }

    exportData.push(temp);
  }

  let data = formatJson(header, exportData)

  export_json_to_excel({ header: tHeader, data: data, filename: '练习成绩导出' });
}



// 对外暴露属性
const props = defineProps({
  data: Number
})
const tableData = ref(null)

const select = ref("score")

const headerTask = ref({});

const total = ref({
  attempt: {},
  length: {},
  pass: {},
});

const load = () => {

  headerTask.value = {};
  tableData.value = null;

  axios
    .get('/api/student/queryListByClassId', {
      params: {
        id: props.data
      }
    })
    .then((student) => {
      console.log(student);
      console.log(student.data);
      let students = {};

      for (let i = 0; i < student.data.data.length; i++) {
        const element = student.data.data[i];
        students[element.id] = element.realname;
      }

      console.log(students);

      axios
        .get('/api/usertask/queryByClassId/' + props.data)
        .then((usertask) => {
          console.log("usertask");
          console.log(usertask);
          console.log(student.data.data.length)
          for (let i = 0; i < student.data.data.length; i++) {

            const stu = student.data.data[i];
            let userTask = {};
            for (let j = 0; j < usertask.data.data.length; j++) {
              const element = usertask.data.data[j];

              if (element.userId == stu.id) {
                // ut.push(element);
                userTask["task" + element.task.id] = element;
                headerTask.value["task" + element.task.id] = {
                  label: element.task.taskSn,
                  task: element.task
                }

                if (total.value["attempt"]["task" + element.task.id] == null) {
                  total.value["attempt"]["task" + element.task.id] = { total: 0, top: null }
                }
                if (total.value["length"]["task" + element.task.id] == null) {
                  total.value["length"]["task" + element.task.id] = { total: 0, top: null }
                }
                if (total.value["pass"]["task" + element.task.id] == null) {
                  total.value["pass"]["task" + element.task.id] = { total: 0, top: null }
                }
                if (total.value["task" + element.task.id] == null) {
                  total.value["task" + element.task.id] = 0;
                }

                if (element.attemptRank > total.value["attempt"]["task" + element.task.id]['total']) {
                  total.value["attempt"]["task" + element.task.id]['total'] = element.attemptRank;
                }
                if (element.lengthRank > total.value["length"]["task" + element.task.id]['total']) {
                  total.value["length"]["task" + element.task.id]['total'] = element.lengthRank;
                }
                if (element.passRank > total.value["pass"]["task" + element.task.id]['total']) {
                  total.value["pass"]["task" + element.task.id]['total'] = element.passRank;
                }
                if (element.attemptRank == 1) {
                  total.value["attempt"]["task" + element.task.id]['top'] = students[element.userId];
                }
                if (element.lengthRank == 1) {
                  total.value["length"]["task" + element.task.id]['top'] = students[element.userId];
                }
                if (element.passRank == 1) {
                  total.value["pass"]["task" + element.task.id]['top'] = students[element.userId];
                }
                if (element.taskScore > total.value["task" + element.task.id]) {
                  total.value["task" + element.task.id] = element.taskScore;
                }
              }
            }

            // student.data.data[i].userTask = userTask;
            stu.userTask = userTask;
          }

          console.log("student.data");
          console.log(student.data.data);


          console.log(123123123)
          console.log(headerTask.value)
          console.log(total.value)

        })
        .catch(function (error) { // 请求失败处理
          console.log(error);
        });

      tableData.value = student.data.data
      // tableData.value.header = headerTask.value
    })
    .catch(function (error) { // 请求失败处理
      console.log(error);
    });

}

onMounted(()=>{
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

// 上传前
const handleBeforeUpload = (obj) => {
  loading.value = true
}

// 上传成功后
const handleSuccess = (obj) => {
  load();
  loading.value = false
}

const cellStyle = ({ row, column, columnIndex }) => {

  if (column.property && row.userTask && row.userTask[column.property.replaceAll("userTask.", "")]) {
    if (row.userTask[column.property.replaceAll("userTask.", "")].taskScore > 0 && row.userTask[column.property.replaceAll("userTask.", "")].taskScore == total.value[column.property.replaceAll("userTask.", "")]) {
      return {
        background: 'rgb(225, 243, 216)'
      }
    }
  }
}

// 当修改了多选项
const selsChange = (obj) => {
  console.log(obj.$index)
  console.log(obj)
  multipleSelection.value = obj
}

// 修改学生信息
const edit = (obj) => {
  type.value = '学生信息';

  student.index = obj.$index;
  student.id = obj.row.id;
  student.realname = obj.row.realname;
  student.stuNo = obj.row.stuNo;
  dialogFormVisible.value = true
}

// 添加学生
const add = (obj) => {
  type.value = '添加学生';
  student.index = student.id = student.realname = student.stuNo = '';
  student.stuClass = props.data;

  dialogFormVisible.value = true
}

// 提交
const submit = () => {
  console.log(999)
  console.log(student)

  if (type.value == '添加学生') {
    console.log(student)
    axios
      .post('/api/student/add', student)
      .then((response) => {
        console.log(response)
        dialogFormVisible.value = false
        student.id = response.data
        multipleTableRef.value.data.push(deepClone(student));
      })
      .catch(function (error) { // 请求失败处理
        console.log(error);
      });
  } else {
    axios
      .post('/api/student/update', student)
      .then((response) => {
        dialogFormVisible.value = false
        multipleTableRef.value.data[student.index] = deepClone(student)
      })
      .catch(function (error) { // 请求失败处理
        console.log(error);
      });
  }
}

// 删除单个
const del = (obj) => {
  axios
    .get('/api/student/delete', {
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
      .get('/api/student/batchDelete/' + ids)
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
</script>

<style scoped>
.table-container {
  padding: 10px;
  background-color: #fff;
}
.table-header {
  padding: 10px;
}
.cell-style {
  font-size: 12px;
}
</style>