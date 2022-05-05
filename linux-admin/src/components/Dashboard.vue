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
          <div>123</div>
          <div>班级总数</div>
        </div>
      </div>
      <div>
        <div>
          <el-icon><avatar /></el-icon>
        </div>
        <div>
          <div>123</div>
          <div>学生总数</div>
        </div>
      </div>
      <div>
        <div>
          <el-icon><list /></el-icon>
        </div>
        <div>
          <div>123</div>
          <div>任务总数</div>
        </div>
      </div>
      <div>
        <div>
          <el-icon><user-filled /></el-icon>
        </div>
        <div>
          <div>123</div>
          <div>我的班级数</div>
        </div>
      </div>
    </div>
    <div class="center-right">
      <div class="data" ref="data1"></div>
    </div>
  </div>
  <div class="footer">
    <el-table :data="tableData" border height="280px">
      <el-table-column type="index" align="center" width="40" />
      <el-table-column prop="date" label="姓名" width="100" />
      <el-table-column prop="name" label="挑战模式成绩" />
    </el-table>
    <el-table :data="tableData" border height="280px">
      <el-table-column type="index" align="center" width="40" />
      <el-table-column prop="date" label="姓名" width="100" />
      <el-table-column prop="name" label="练习模式成绩" />
    </el-table>
    <el-table :data="tableData" border height="280px">
      <el-table-column type="index" align="center" width="40" />
      <el-table-column prop="date" label="姓名" width="100" />
      <el-table-column prop="name" label="动作" />
    </el-table>
  </div>
</template>
 
 
<script setup>
import { onMounted, ref } from "vue";
import { User, Flag, UserFilled, Avatar, List } from "@element-plus/icons-vue";
import * as echarts from "echarts";

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
  {
    date: "2016",
    name: "Tom",
    address: "No. 12",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
  {
    date: "2016",
    name: "Tom",
    address: "No. 18",
  },
];

const data1 = ref();

let chart1 = null;
const initEcharts1 = () => {
  console.log(data1);
  console.log(data1.value);

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
      data: ['20云计算1班','20云计算2班','20云计算3班'],
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
        textStyle: {
          color: "#CCC",
          fontSize: 10,
        },
        margin: 8,
      },
    },
    yAxis: {
      type: "category",
      data: ["已完成", "已开始", "全部"],
      axisLabel: {
        interval: 0,
        textStyle: {
          color: "#CCC",
          fontSize: 10,
        },
        margin: 8,
      },
    },
    series: [
      {
        name: "20云计算1班",
        type: "bar",
        stack: "total",
        label: {
          show: true,
        },
        emphasis: {
          focus: "series",
        },
        data: [320, 302, 301],
      },
      {
        name: "20云计算2班",
        type: "bar",
        stack: "total",
        label: {
          show: true,
        },
        emphasis: {
          focus: "series",
        },
        data: [120, 132, 101, 134, 90, 230, 210],
      },
      {
        name: "20云计算3班",
        type: "bar",
        stack: "total",
        label: {
          show: true,
        },
        emphasis: {
          focus: "series",
        },
        data: [220, 182, 191, 234, 290, 330, 310],
      },
    ],
  });
};

const data2 = ref();

let chart2 = null;
const initEcharts2 = () => {
  console.log(data2.value);

  chart2 = echarts.init(data2.value);

  chart2.setOption({
    backgroundColor: "#FFF",
    shadowColor: "rgba(0, 0, 0, 0.5)",
    legend: {
      bottom: "0",
      left: "center",
    },
    grid: {
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
    },
    title: {
      text: "已开始", //图形标题，配置在中间对应效果图的80%
      left: "left",
      textStyle: {
        color: "rgb(50,197,233)",
        fontSize: 16,
        align: "center",
      },
    },
    series: [
      {
        name: "访问来源",
        type: "pie",
        radius: ["30%", "70%"],
        color: ["#91cc75", "#fac858"],
        avoidLabelOverlap: false,
        label: {
          // show: false,
          // position: 'center'
        },
        labelLine: {
          show: false,
        },
        data: [
          { value: 335, name: "1" },
          { value: 310, name: "2" },
        ],
      },
    ],
  });
};

const data3 = ref();

let chart3 = null;
const initEcharts3 = () => {
  console.log(data3.value);

  chart3 = echarts.init(data3.value);

  chart3.setOption({
    backgroundColor: "#FFF",
    shadowColor: "rgba(0, 0, 0, 0.5)",
    legend: {
      bottom: "0",
      left: "center",
    },
    grid: {
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
    },
    title: {
      text: "已完成", //图形标题，配置在中间对应效果图的80%
      left: "left",
      textStyle: {
        color: "rgb(50,197,233)",
        fontSize: 16,
        align: "center",
      },
    },
    series: [
      {
        name: "访问来源",
        type: "pie",
        radius: ["30%", "70%"],
        color: ["#91cc75", "#fac858"],
        avoidLabelOverlap: false,
        label: {
          // show: false,
          // position: 'center'
        },
        labelLine: {
          show: false,
        },
        data: [
          { value: 335, name: "1" },
          { value: 310, name: "2" },
        ],
      },
    ],
  });
};

onMounted(() => {
  initEcharts1();
  // initEcharts2();
  // initEcharts3();
});
</script>

<style scope>
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
    background: rgba(0,0,0, 0.3);
}
::-webkit-scrollbar-thumb:window-inactive {
    background: rgba(0,0,0, 0.3);
}
</style>