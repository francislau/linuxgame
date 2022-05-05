<template>
  <div class="top">
    <div>《挑战Linux》后台管理系统</div>
    <div class="version">version 0.1 build 2022.2.16</div>
  </div>
  <div class="center">
    <div class="center-left">
      <div>
        <div>
          <el-icon><user-filled /></el-icon>
        </div>
        <div>
          <div>{{ classes }}</div>
          <div>班级总数</div>
        </div>
      </div>
      <div>
        <div>
          <el-icon><avatar /></el-icon>
        </div>
        <div>
          <div>{{ users }}</div>
          <div>学生总数</div>
        </div>
      </div>
      <div>
        <div>
          <el-icon><list /></el-icon>
        </div>
        <div>
          <div>{{ tasks }}</div>
          <div>任务总数</div>
        </div>
      </div>
      <div>
        <div>
          <el-icon><user-filled /></el-icon>
        </div>
        <div>
          <div>{{ myClasses }}</div>
          <div>我的班级数</div>
        </div>
      </div>
    </div>
    <div class="center-right">
      <div class="data" ref="data1"></div>
    </div>
  </div>
  <div class="footer">
    <el-table :data="challenge" border height="280px">
      <el-table-column type="index" align="center" width="40" />
      <el-table-column prop="realname" label="姓名" width="100" />
      <el-table-column prop="score" label="挑战模式成绩" />
    </el-table>
    <el-table :data="practise" border height="280px">
      <el-table-column type="index" align="center" width="40" />
      <el-table-column prop="realname" label="姓名" width="100" />
      <el-table-column prop="score" label="练习模式成绩" />
    </el-table>
    <el-table :data="record" border height="280px">
      <el-table-column type="index" align="center" width="40" />
      <el-table-column prop="realname" label="姓名" width="100" />
      <el-table-column prop="task" label="操作（最近50条）" />
    </el-table>
  </div>
</template>


<script setup>
import { onMounted, ref } from "vue";
import { User, Flag, UserFilled, Avatar, List } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import axios from "axios";
import { utils } from '../assets/js/utils';

// 对外暴露属性
const props = defineProps({
  data: Number,
});

const classes = ref("");
const users = ref("");
const tasks = ref("");
const myClasses = ref("");
const challenge = ref([]);
const practise = ref([]);
const record = ref([]);

const tableData = [
  {
    date: "2016",
    name: "Tom",
    address: "No",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 1",
  },
];

const data1 = ref();

let chart1 = null;
const initEcharts = (legend, series) => {
  chart1 = echarts.init(data1.value);

  chart1.setOption({
    tooltip: {
      trigger: "axis",
      axisPointer: {
        // Use axis to trigger tooltip
        type: "shadow", // 'shadow' as default; can also be 'line' or 'shadow'
      },
    },
    legend: {
      data: legend,
      textStyle: {
        color: "#fff",
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "value",
      axisLabel: {
        interval: 0,
        color: "#CCC",
        fontSize: 10,
        margin: 8,
      },
    },
    yAxis: {
      type: "category",
      data: ["已完成", "已开始", "分配总数"],
      axisLabel: {
        interval: 0,
        color: "#CCC",
        fontSize: 10,
        margin: 8,
      },
    },
    series: series,
  });
};
onMounted(() => {
  

  let id = localStorage.getItem("id");
  console.log(id);
  if (id != null) {
    
    axios
      .get("/api/user/home/" + id)
      .then((response) => {
        console.log(response.data);
        classes.value = response.data.classes;
        users.value = response.data.users;
        tasks.value = response.data.tasks;
        myClasses.value = response.data.myClasses;
        challenge.value = response.data.ranks;
        practise.value = response.data.pranks;
        // record.value = response.data.records;

        let legend = [];
        let series = [];
        for (var i in response.data.datas) {
          legend.push(i);
          series.push({
            name: i,
            type: "bar",
            stack: "total",
            label: {
              show: true,
            },
            emphasis: {
              focus: "series",
            },
            data: [response.data.datas[i].finish, response.data.datas[i].start, response.data.datas[i].total],
          });
        }

        let task1 = [];
        for (let i = 0; i < response.data.records.length; i++) {
          const element = response.data.records[i];
          let op = "检查了";
          if(element.taskStatus == 2){
            op = "开始了";
          }
          if(element.taskStatus == 3){
            op = "完成了";
          }
          task1.push({
            realname: element.user.realname,
            task: utils.getDateDiff(element.createAt) + op+"#"+element.task.taskSn
          });
        }
        record.value = task1;
        console.log(record.value)

        console.log(legend);
        console.log(series);
        initEcharts(legend, series);
      })
      .catch(function (error) {
        // 请求失败处理
        console.log(error);
      });

    // axios
    //   .get("/api/classes/queryListByUserId/" + id)
    //   .then((response) => {
    //     console.log(response.data);
    //     class_arr.value = response.data;

    //     if (response.data.length > 0) {
    //       // manageStu(response.data[0].id)
    //     }
    //   })
    //   .catch(function (error) {
    //     // 请求失败处理
    //     console.log(error);
    //   });
  }
  // initEcharts2();
  // initEcharts3();
});
</script>

<style scoped>
.top {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 30px;
}
.top .version {
  font-size: 20px;
  margin: 20px 0px;
}
.center {
  width: 100%;
  display: flex;
}
.center-left {
  width: 50%;
  display: flex;
  flex-wrap: wrap;
  color: #fff;
}
.center-left > div {
  width: calc(50% - 20px);
  height: 100px;
  margin: 0 10px 15px 0;
  border-radius: 4px;
  box-sizing: border-box;
}
.center-left > div {
  display: flex;
  justify-content: center;
  align-items: center;
}
.center-left > div > div:nth-child(1) {
  width: 30%;
  height: 50%;
  font-size: 40px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}
.center-left > div > div:nth-child(2) {
  width: 50%;
  height: 50%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.center-left > div > div:nth-child(2) > div:first-child {
  font-weight: bolder;
  font-size: 30px;
}
.center-left > div:first-child {
  background-color: #6a8abe;
  box-shadow: 0 5px 0 #5f7cab;
}
.center-left > div:nth-child(2) {
  background-color: #fc8675;
  box-shadow: 0 5px 0 #e27869;
}
.center-left > div:nth-child(3) {
  background-color: #5ab6df;
  box-shadow: 0 5px 0 #51a3c8;
}
.center-left > div:nth-child(4) {
  background-color: #4acacb;
  box-shadow: 0 5px 0 #42b5b6;
}
.center-right {
  width: 50%;
  height: 220px;
  border-radius: 4px;
  background: rgb(73, 88, 110);
}
.data {
  /* width: 30%; */
  height: 200px;
  /* box-shadow: 0 0 10px #ccc; */
}
.el-table {
  width: 30%;
}
.footer {
  display: flex;
  justify-content: space-evenly;
  background-color: #fff;
  min-height: 300px;
  padding-top: 20px;
}

/* 设置滚动条的样式 */
::-webkit-scrollbar {
  width: 10px;
}
/* 滚动槽 */
::-webkit-scrollbar-track {
  border-radius: 10px;
}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.3);
}
::-webkit-scrollbar-thumb:window-inactive {
  background: rgba(0, 0, 0, 0.3);
}
</style>