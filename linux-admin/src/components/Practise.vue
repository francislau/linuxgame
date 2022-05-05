<template>
  <div class="table-container">
    <div class="table-header">
      <el-button type="success" :icon="Download" @click="exportExcel">导出成绩</el-button>
      <el-select v-model="select" style="float: right;" placeholder="Select">
        <el-option key="score" label="参考分数" value="score"></el-option>
        <el-option @click="restSelect" key="detail" label="详情" value="detail"></el-option>
        <el-option @click="chgSelect" key="range" :label="'详情（'+valueRange+'）'" value="range"></el-option>
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
                <div>{{ scope.row.userTask[index].practiseScore }}</div>
              </template>
              <div>分数：100分 = 70+完成时间排名得分+完成用时排名得分+提交次数得分</div>
              <div v-if="scope.row.userTask[index].practiseScore > 0">
                分数：{{ scope.row.userTask[index].practiseScore }}分 = 70
                +
                10×({{ total.practiseFirstPass[index].total }}-{{ scope.row.userTask[index].practiseFirstPassRank }})/({{ total.practiseFirstPass[index].total }}-1)
                +
                10×({{ total.practiseMinLength[index].total }}-{{ scope.row.userTask[index].practiseMinLengthRank }})/({{ total.practiseMinLength[index].total }}-1)
                +
                10×({{ total.practiseAttempt[index].total }}-{{ scope.row.userTask[index].practiseAttemptRank }})/({{ total.practiseAttempt[index].total }}-1)
              </div>
              <div v-else>分数：0分 = 未完成</div>
              <div>提交最快的是：{{ total.practiseFirstPass[index].top }}</div>
              <div>用时最短的是：{{ total.practiseMinLength[index].top }}</div>
              <div>做得最多次的是：{{ total.practiseAttempt[index].top }}</div>
            </el-popover>
          </div>
          <div class="cell-style" v-else-if="scope.row.userTask[index]">
            <el-popover
              v-if="scope.row.userTask[index].practiseMinPassAt && checkRange(scope.row.userTask[index].practiseMinPassAt)"
              placement="bottom-start"
              title
              :width="220"
              trigger="hover"
            >
              <template #reference>
                <div>
                  <div>首次完成：{{ scope.row.userTask[index].practiseFirstPassAt ? utils.timeFormat(scope.row.userTask[index].practiseFirstPassAt, 'date') : '' }}</div>
                  <div>最短用时：{{ utils.lengthFormat(scope.row.userTask[index].practiseMinLength) }}</div>
                  <div>练习次数：{{ scope.row.userTask[index].practiseAttempt }}</div>
                </div>
              </template>
              <div>
                <div>首次完成：{{ scope.row.userTask[index].practiseFirstPassAt ? utils.timeFormat(scope.row.userTask[index].practiseFirstPassAt, 'datetime') : '' }}</div>
                <div>最短开始：{{ scope.row.userTask[index].practiseMinStartAt ? utils.timeFormat(scope.row.userTask[index].practiseMinStartAt) : '' }}</div>
                <div>最短结束：{{ scope.row.userTask[index].practiseMinPassAt ? utils.timeFormat(scope.row.userTask[index].practiseMinPassAt) : '' }}</div>
              </div>
            </el-popover>
            <div v-else>未完成</div>
            <!-- {{ scope.row.userTask[index] ? utils.timeFormat(scope.row.userTask[index].practiseMinStartAt, scope.row.userTask[index].practiseMinPassAt) + '×' + scope.row.userTask[index].practiseAttempt : '' }} -->
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-dialog v-model="dialogFormVisible" title="选择查看的时间范围">
    <el-form>
      <el-date-picker
        v-model="value1"
        type="datetimerange"
        start-placeholder="开始时间"
        end-placeholder="结束时间"
        :default-time="defaultTime2"
        :change="filterRange(value1)"
        size="small"
      ></el-date-picker>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="dialogFormVisible = false">确定</el-button>
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
const value1 = ref(null)
const value2 = ref(null)
const valueRange = ref("时间范围")

const headerTask = ref({});
const total = ref({
  practiseAttempt: {},
  practiseMinLength: {},
  practiseFirstPass: {},
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
      // console.log(student);
      // console.log(student.data);
      let students = {};

      for (let i = 0; i < student.data.data.length; i++) {
        const element = student.data.data[i];
        students[element.id] = element.realname;
      }

      // console.log(students);

      axios
        .get('/api/usertask/queryByClassId/' + props.data)
        .then((usertask) => {
          // console.log("usertask");
          // console.log(usertask);
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


                if (total.value["practiseAttempt"]["task" + element.task.id] == null) {
                  total.value["practiseAttempt"]["task" + element.task.id] = { total: 0, top: null }
                }
                if (total.value["practiseMinLength"]["task" + element.task.id] == null) {
                  total.value["practiseMinLength"]["task" + element.task.id] = { total: 0, top: null }
                }
                if (total.value["practiseFirstPass"]["task" + element.task.id] == null) {
                  total.value["practiseFirstPass"]["task" + element.task.id] = { total: 0, top: null }
                }
                if (total.value["task" + element.task.id] == null) {
                  total.value["task" + element.task.id] = 0;
                }

                if (element.practiseAttemptRank > total.value["practiseAttempt"]["task" + element.task.id]['total']) {
                  total.value["practiseAttempt"]["task" + element.task.id]['total'] = element.practiseAttemptRank;
                }
                if (element.practiseMinLengthRank > total.value["practiseMinLength"]["task" + element.task.id]['total']) {
                  total.value["practiseMinLength"]["task" + element.task.id]['total'] = element.practiseMinLengthRank;
                }
                if (element.practiseFirstPassRank > total.value["practiseFirstPass"]["task" + element.task.id]['total']) {
                  total.value["practiseFirstPass"]["task" + element.task.id]['total'] = element.practiseFirstPassRank;
                }
                if (element.practiseAttemptRank == 1) {
                  total.value["practiseAttempt"]["task" + element.task.id]['top'] = students[element.userId];
                }
                if (element.practiseMinLengthRank == 1) {
                  total.value["practiseMinLength"]["task" + element.task.id]['top'] = students[element.userId];
                }
                if (element.practiseFirstPassRank == 1) {
                  total.value["practiseFirstPass"]["task" + element.task.id]['top'] = students[element.userId];
                }
                if (element.practiseScore > total.value["task" + element.task.id]) {
                  total.value["task" + element.task.id] = element.practiseScore;
                }
              }
            }

            // student.data.data[i].userTask = userTask;
            stu.userTask = userTask;
          }

          // console.log("student.data");
          // console.log(student.data.data);


          // console.log(123123123)
          // console.log(headerTask.value)
          // console.log(total.value)

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

onMounted(() => {
  load();
})


// 对外暴露方法
const emit = defineEmits(['manageStu'])

// 路由
const router = useRouter();


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
    if (row.userTask[column.property.replaceAll("userTask.", "")].practiseScore > 0 && row.userTask[column.property.replaceAll("userTask.", "")].practiseScore == total.value[column.property.replaceAll("userTask.", "")]) {
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

const chgSelect = () => {
  dialogFormVisible.value = true;
}

const checkRange = (obj) => {
  if (value2.value) {
    // console.log(value2.value)
    // console.log(obj)
    // console.log(new Date(obj).getTime())
    // console.log(new Date(value2.value[0]).getTime())
    // console.log(new Date(value2.value[1]).getTime())
    valueRange.value = utils.timeFormat(value2.value[0]) +'-'+utils.timeFormat(value2.value[1]);
    return new Date(obj).getTime() >= new Date(value2.value[0]).getTime() && new Date(obj).getTime() <= new Date(value2.value[1]).getTime()
  } else {
    return true;
  }
}

const filterRange = (obj) => {
  if (obj) {
    dialogFormVisible.value = false;
    value2.value = deepClone(obj);
    value1.value = null;
  }
  //dialogFormVisible.value = false;
}
const restSelect = (obj) => {
  value2.value = null;
  valueRange.value = "时间范围";
}

const defaultTime2 = [
  new Date(2000, 1, 1, 12, 0, 0),
  new Date(2000, 2, 1, 8, 0, 0),
]

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