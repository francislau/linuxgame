
<template>
  <div class="state">
    {{ mode == 1 ? practise_task_finish : task_finish }}/{{ tasks.length }}
  </div>
  <div :class="mode == 1 ? 'mode' : 'mode challenge'">
    <div class="challenge" @click="chgChallenge"></div>
    <div class="practise" @click="chgPractise"></div>
  </div>
  <div v-for="task in tasks" class="task" :key="task">
    <!-- 挑战模式 -->
    <div
      v-if="mode == 2"
      @click="checkTask(task)"
      :class="task.taskStatus == 1 ? 'check_task' : 'check_task started'"
    >
      <img
        :src="
          task.taskStatus == 1
            ? '/starttask.png'
            : task.taskStatus == 2
            ? '/checktask.png'
            : '/achievement.png'
        "
        alt
      />
      <div class="use">
        <span>{{ task.taskStatus==3?task.use.replace(new RegExp('已用','g'),""):task.use }}×{{ task.attempt }}</span>
      </div>
    </div>
    <!-- 练习模式 -->
    <div
      v-else
      @click="checkTask(task)"
      :class="task.practiseMinStartAt ? 'check_task started' : 'check_task'"
    >
      <img
        :src="task.practiseStartAt ? '/checktask.png' : '/starttask.png'"
        alt
      />
      <div class="use">
        <span :style="task.practiseMinPassAt?'color:#AAA':''">{{ task.use }}×{{ task.practiseAttempt }}</span>
      </div>
    </div>
    <div>
      {{ task.task.taskTitle }}
      <span class="tasksn">#{{ task.task.taskSn }}</span>
    </div>
    <div class="task-desc">{{ task.task.taskDesc }}</div>
  </div>
  <div v-if="dialog" class="dialog_bg">
    <div class="dialog">
      <input v-model="conn.ip" type="text" placeholder="请输入IP地址" />
      <input
        v-model="conn.port"
        type="text"
        placeholder="请输入端口号，如：22"
      />
      <input v-model="conn.username" type="text" placeholder="请输入用户名" />
      <input v-model="conn.password" type="text" placeholder="请输入密码" />
      <div class="footer">
        <div @click="dialogCheck" class="confirm"></div>
        <div @click="dialogCancel" class="cancel"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, reactive, onMounted, watch } from "vue";
import { utils } from "../assets/js/utils";
import { uuid } from "vue-uuid";

const emit = defineEmits(["close"]);
const tasks = ref([]);
const task_finish = ref(0);
const practise_task_finish = ref(0);
const mode = ref(1);
const dialog = ref(false);
const waiting = ref({});
const waitingCheck = ref({});

const waitCallback = (res) => {
	console.log(6666666666);
	
  console.log(res);
  // console.log(waiting.value[res.uuid]?.loading)
  console.log("utils.close");
  console.log(waiting.value[res.uuid]);
  utils.close(waiting.value[res.uuid]?.loading);
  let obj = waiting.value[res.uuid]?.obj;
  if (res.state != 1) {
    if (res.mode == "2" && res.state == "-1") {
      utils.alert("初始化失败，设备唯一码有误，请检查配置，或等待老师审核");
    } else if (res.state == "-2") {
      utils.alert("root的密码错误，请检查配置");
    } else {
      utils.alert("初始化失败，请检查配置，或报告老师");
    }
  } else if (obj) {
    if (res.mode == "1") {
      if (!obj.practiseStartAt) {
        obj.practiseStartAt = utils.timeFormat();
        obj.use = "已用0秒";
      }
      if (!obj.practiseMinStartAt) {
        obj.practiseMinStartAt = utils.timeFormat();
      }
    } else if (res.mode == "2") {
      obj.taskStatus = 2;
      if (!obj.startAt) {
        obj.startAt = new Date().getTime();
        obj.use = "已用0秒";
      }
    }
  }
};

const waitCheckCallback = (res) => {
  console.log(6666666666);
  console.log(res);
  // console.log(waiting.value[res.uuid]?.loading)

  utils.close(waitingCheck.value[res.uuid]?.loading);
  let obj = waitingCheck.value[res.uuid]?.obj;

  if (obj) {
    if (res.mode == "1") {
      // 练习模式
      if (res.state == 1) {
        let str = "";
        if (obj.practiseMinPassAt == null) {
          obj.practiseMinPassAt = utils.timeFormat();
          obj.practiseMinLength =
            new Date(obj.practiseMinPassAt).getTime() -
            new Date(obj.practiseMinStartAt).getTime();
          str = "，你可以反复提交，系统会记录你最短用时";
        } else if (
          new Date().getTime() - new Date(obj.practiseStartAt).getTime() <
          obj.practiseMinLength
        ) {
          obj.practiseMinStartAt = obj.practiseStartAt;
          obj.practiseMinPassAt = utils.timeFormat();
          obj.practiseMinLength =
            new Date(obj.practiseMinPassAt).getTime() -
            new Date(obj.practiseMinStartAt).getTime();
          str =
            "，恭喜你刷新了最短用时记录<br/>" +
            utils.lengthFormat(obj.practiseMinLength);
        } else {
          str =
            "，你的最短用时记录是：<br/>" +
            utils.lengthFormat(obj.practiseMinLength);
        }
        obj.practiseStartAt = null;
		
        obj.use = obj.use.replace(new RegExp('已用','g'),"");
        practise_task_finish.value++;
        utils.alert("检查成功" + str);
      } else {
        utils.alert(
          "检查失败，是否需要重置本题环境，点确定重置，否则点取消",
          true,
          true,
          () => {
            utils.alert(
              "确定后会初始化本题的环境，如果本题已安装服务，也会卸载该服务，请谨慎操作！！！",
              true,
              true,
              () => {
                let uuid1 = uuid.v1().replace(new RegExp('-','g'),"");;

                axios
                  .get(
                    "/api/usertask/start/" +
                      mode.value +
                      "/" +
                      obj.id +
                      "/" +
                      uuid1
                  )
                  .then((response) => {
                    console.log(response.data);

                    if (response.data.data == 1) {
						console.log(6666666666);
						console.log(uuid1);
                      waiting.value = {};

                      waiting.value[uuid1] = {
                        loading: utils.alert(
                          "正在初始化，如果长时间无反应，请手动刷新页面",
                          false,
                          false
                        ),
                        obj: obj,
                      };
                    } else {
                      utils.alert("开始失败，请检查左上角是否在线状态");
                    }
                  })
                  .catch(function (error) {
                    // 请求失败处理
                    console.log(error);
                  });
              },
              () => {}
            );
          },
          () => {}
        );
      }
    } else if (res.mode == "2") {
      // 挑战模式
      if (res.state == 1) {
        // 成功
        obj.attempt = obj.attempt + 1;

        utils.alert("挑战成功、太棒了！ ^_^");
        obj.taskStatus = 3;
        // obj.passAt = new Date().getTime();
        obj.use = obj.use.replace(new RegExp('已用','g'),"");;
        obj.passAt = new Date();

        task_finish.value++;
      } else if (res.state == -1) {
        let s = utils.alert("设备唯一码有误，请测试连接", false, false);
        setTimeout(() => {
          utils.close(s);
        }, 1000);
      } else {
        obj.attempt = obj.attempt + 1;
        utils.alert(
          "挑战失败，是否需要重置本题环境，点确定重置，否则点取消",
          true,
          true,
          () => {
            utils.alert(
              "确定后会初始化本题的环境，如果本题已安装服务，也会卸载该服务，请谨慎操作！！！",
              true,
              true,
              () => {
                let uuid1 = uuid.v1().replace(new RegExp('-','g'),"");;

                axios
                  .get(
                    "/api/usertask/start/" +
                      mode.value +
                      "/" +
                      obj.id +
                      "/" +
                      uuid1
                  )
                  .then((response) => {
                    console.log(response.data);

                    if (response.data.data == 1) {
                      waiting.value = {};

                      waiting.value[uuid1] = {
                        loading: utils.alert(
                          "正在初始化，如果长时间无反应，请手动刷新页面",
                          false,
                          false
                        ),
                        obj: obj,
                      };
                    } else {
                      utils.alert("开始失败，请检查左上角是否在线状态");
                    }
                  })
                  .catch(function (error) {
                    // 请求失败处理
                    console.log(error);
                  });
              },
              () => {}
            );
          },
          () => {}
        );
      }
    }
  }
};
defineExpose({ waitCallback, waitCheckCallback });

const conn = ref({
  ip: "",
  port: "",
  username: "",
  password: "",
  keyCode: "",
});
console.log(new Date().getTime());
const checkTask = (obj) => {
  if (mode.value == 1) {
    // 练习模式
    if (obj.practiseStartAt) {
      let uuid1 = uuid.v1().replace(new RegExp('-','g'),"");

      axios
        .get("/api/usertask/check/" + mode.value + "/" + obj.id + "/" + uuid1)
        .then((response) => {
          console.log(response.data);
          // utils.close(loading);
          if (response.data.result == "success") {
            obj.practiseAttempt = obj.practiseAttempt + 1;
            if (response.data.data == 1) {
              waitingCheck.value = {};

              waitingCheck.value[uuid1] = {
                loading: utils.alert(
                  "正在检查，如果长时间无反应，请手动刷新页面",
                  false,
                  false
                ),
                obj: obj,
              };
              console.log("waitingCheck.value");
              console.log(waitingCheck.value);
              // let str = '';
              // if (obj.practiseMinPassAt == null) {
              //     obj.practiseMinPassAt = utils.timeFormat();
              //     obj.practiseMinLength = new Date(obj.practiseMinPassAt).getTime() - new Date(obj.practiseMinStartAt).getTime();
              //     str = '，你可以反复提交，系统会记录你最短用时';
              // } else if (new Date().getTime() - new Date(obj.practiseStartAt).getTime() < obj.practiseMinLength) {
              //     obj.practiseMinStartAt = obj.practiseStartAt;
              //     obj.practiseMinPassAt = utils.timeFormat();
              //     obj.practiseMinLength = new Date(obj.practiseMinPassAt).getTime() - new Date(obj.practiseMinStartAt).getTime();
              //     str = '，恭喜你刷新了最短用时记录<br/>' + utils.lengthFormat(obj.practiseMinLength);
              // } else {
              //     str = '，你的最短用时记录是：<br/>' + utils.lengthFormat(obj.practiseMinLength);
              // }
              // obj.practiseStartAt = null;
              // obj.use = obj.use.replaceAll("已用", "");
              // practise_task_finish.value++;
              // utils.alert("检查成功" + str);
            } else {
              utils.alert("开始失败，请检查左上角是否在线状态");

              // let s = utils.alert("检查失败，再来一次吧", false, false);
              // setTimeout(() => {
              //     utils.close(s);
              // }, 1000);
            }
          } else {
            utils.alert(response.data.message);
          }
        })
        .catch(function (error) {
          // 请求失败处理
          console.log(error);
        });
    } else {
      utils.alert(
        "确定后会初始化本题的环境，如果本题已安装服务，也会卸载该服务，请谨慎操作！！！",
        true,
        true,
        () => {
          let uuid1 = uuid.v1().replace(new RegExp('-','g'),"");

          axios
            .get(
              "/api/usertask/start/" + mode.value + "/" + obj.id + "/" + uuid1
            )
            .then((response) => {
              console.log(response.data);

              if (response.data.data == 1) {
                waiting.value = {};

                waiting.value[uuid1] = {
                  loading: utils.alert(
                    "正在初始化，如果长时间无反应，请手动刷新页面",
                    false,
                    false
                  ),
                  obj: obj,
                };

                // obj.practiseStartAt = utils.timeFormat();
                // if (!obj.practiseMinStartAt) {
                //     obj.practiseMinStartAt = utils.timeFormat();
                // }
                // obj.use = "已用0秒";
              } else {
                utils.alert("开始失败，请检查左上角是否在线状态");
              }
            })
            .catch(function (error) {
              // 请求失败处理
              console.log(error);
            });
        },
        () => {}
      );
    }
  } else {
    // 挑战模式
    if (obj.taskStatus == 1) {
      utils.alert(
        "确定后会初始化本题的环境，如果本题已安装服务，也会卸载该服务，请谨慎操作！！！",
        true,
        true,
        () => {
		  let uuid1 = uuid.v1().replace(new RegExp('-','g'),"");

          axios
            .get(
              "/api/usertask/start/" + mode.value + "/" + obj.id + "/" + uuid1
            )
            .then((response) => {
              console.log(response.data);

              if (response.data.data == 1) {
                waiting.value = {};

                waiting.value[uuid1] = {
                  loading: utils.alert(
                    "正在初始化，如果长时间无反应，请手动刷新页面",
                    false,
                    false
                  ),
                  obj: obj,
                };
                // waiting.value[uuid]['obj'] = obj;
                // obj.taskStatus = 2;
                // obj.startAt = new Date().getTime();
                // obj.use = "已用0秒";
              } else {
                utils.alert("开始失败，请检查左上角是否在线状态");
              }
            })
            .catch(function (error) {
              // 请求失败处理
              console.log(error);
            });
        },
        () => {}
      );
    } else if (obj.taskStatus == 2) {
      let uuid1 = uuid.v1().replace(new RegExp('-','g'),"");

      axios
        .get("/api/usertask/check/" + mode.value + "/" + obj.id + "/" + uuid1)
        .then((response) => {
          console.log(response.data);
          if (response.data.data == 1) {
            waitingCheck.value = {};

            waitingCheck.value[uuid1] = {
              loading: utils.alert(
                "正在检查，如果长时间无反应，请手动刷新页面",
                false,
                false
              ),
              obj: obj,
            };
            // obj.attempt = obj.attempt + 1;
            // if (response.data.data == 1) {
            //     utils.alert("挑战成功、太棒了！ ^_^");
            //     obj.taskStatus = 3;
            //     // obj.passAt = new Date().getTime();
            //     obj.use = obj.use.replaceAll("已用", "");
            //     obj.passAt = new Date();

            //     task_finish.value++;
            // } else if (response.data.data == -1) {
            //     let s = utils.alert("设备唯一码有误，请测试连接", false, false);
            //     setTimeout(() => {
            //         utils.close(s);
            //     }, 1000);
            // } else {
            //     let s = utils.alert("挑战失败，再来一次吧", false, false);
            //     setTimeout(() => {
            //         utils.close(s);
            //     }, 1000);
            // }
          } else {
            // utils.alert(response.data.message);
            utils.alert("开始失败，请检查左上角是否在线状态");
          }
        })
        .catch(function (error) {
          // 请求失败处理
          console.log(error);
        });
    }
  }
};

const chgChallenge = () => {
  console.log(2);
  if (mode.value == 1) {
    chgMode(2);
  }
  mode.value = 2;
};

const chgPractise = () => {
  console.log(1);
  if (mode.value == 2) {
    chgMode(1);
  }
  // if (mode.value == 1) {
  //     dialog.value = true;
  // }
  mode.value = 1;
};

const chgMode = (mode) => {
  axios
    .post("/api/student/update", {
      id: localStorage.getItem("id"),
      defaultMode: mode,
    })
    .then((response) => {
      console.log(response.data);
	  initData();
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

const dialogCheck = () => {
  if (conn.value.ip == "") {
    utils.alert("请填写IP地址");
    return false;
  }
  if (conn.value.port == "") {
    utils.alert("请填写端口号");
    return false;
  }
  if (conn.value.username == "") {
    utils.alert("请填写用户名");
    return false;
  }
  if (conn.value.password == "") {
    utils.alert("请填写密码");
    return false;
  }
  let loading = utils.alert("正在连接...", false, false);
  axios
    .get("/api/ssh/checkConn", {
      params: {
        ip: conn.value.ip,
        port: conn.value.port,
        username: conn.value.username,
        password: conn.value.password,
      },
    })
    .then((response) => {
      console.log(response.data);
      utils.close(loading);
      if (response.data.result == "success") {
        utils.alert("连接成功 ^_^");
        conn.value.keyCode = response.data.keyCode;

        axios
          .post("/api/student/update", {
            id: localStorage.getItem("id"),
            connPractise: JSON.stringify(conn.value),
          })
          .then((response) => {
            console.log(response.data);
          })
          .catch(function (error) {
            // 请求失败处理
            console.log(error);
          });
      } else {
        utils.alert(response.data.message);
      }
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
};

const dialogCancel = () => {
  dialog.value = false;
};

const initData = () =>{
	axios
	  .get("/api/usertask/queryByUserId/"+mode.value+"/" + localStorage.getItem("id"))
	  .then((response) => {
	    console.log(response.data);
	    tasks.value = response.data.data;
	
	    for (let i = 0; i < tasks.value.length; i++) {
	      const element = tasks.value[i];
	      if (mode.value == 2) {
	        // 挑战模式
	        if (element.taskStatus == 2) {
	          element.use = utils.dateDiff(new Date(element.startAt));
	        }
	        if (element.taskStatus == 3) {
	          element.use = utils.dateDiff(
	            new Date(element.startAt),
	            new Date(element.passAt)
	          );
	
	          element.use = element.use.replace(new RegExp('已用','g'),"");
			  
	        }
	      } else {
	        // 练习模式
	        // if (element.practisePassAt != null) {
	        //     element.use = utils.dateDiff(new Date(element.practiseStartAt),
	        //         new Date(element.practisePassAt))
	        //     element.use = element.use.replaceAll("已用", "");
	        //     practise_task_finish.value++;
	        // } else
	        if (element.practiseStartAt != null) {
	          element.use = utils.dateDiff(new Date(element.practiseStartAt));
	        } else if (element.practiseMinPassAt != null) {
	          // 显示最短完成时间
	          element.use = utils.lengthFormat(element.practiseMinLength);
	        }
	      }
	      if (element.taskStatus == 3) {
	        task_finish.value++;
	      }
	      if (element.practiseMinPassAt != null) {
	        practise_task_finish.value++;
	      }
	    }
	    setInterval(() => {
	      for (let i = 0; i < tasks.value.length; i++) {
	        const element = tasks.value[i];
	        if (mode.value == 2) {
	          // 挑战模式
	          if (element.taskStatus == 2) {
	            element.use = utils.dateDiff(new Date(element.startAt));
	          }
	          if (element.taskStatus == 3) {
	            element.use = utils.dateDiff(
	              new Date(element.startAt),
	              new Date(element.passAt)
	            );
	            element.use = element.use.replace(new RegExp('已用','g'),"");
	          }
	        } else {
	          // 练习模式
	          // 正在做
	          if (element.practiseStartAt) {
	            element.use = utils.dateDiff(new Date(element.practiseStartAt));
	          } else if (element.practiseMinPassAt != null) {
	            // 显示最短完成时间
	            element.use = utils.lengthFormat(element.practiseMinLength);
	          }
	        }
	      }
	    }, 1000);
	  })
	  .catch(function (error) {
	    // 请求失败处理
	    console.log(error);
	  });
}

onMounted(() => {
 
  axios
    .get("/api/student/query/" + localStorage.getItem("id"))
    .then((response) => {
      console.log("response.data");
      console.log(response.data);
      // conn.value = JSON.parse(response.data.data.userDevice.connPractise);
      mode.value = response.data.data.defaultMode == 2 ? 2 : 1;
	  initData();
    })
    .catch(function (error) {
      // 请求失败处理
      console.log(error);
    });
});
</script>

<style scoped>
.task {
  width: 80%;
  /* height: 80px; */
  border-radius: 10px;
  border: 5px solid #c0a920;
  background-color: #7d0000;
  color: #ddd;
  font-size: 30px;
  margin-bottom: 20px;
  text-align: left;
  flex-shrink: 0;
  transition: all 0.5s;
  position: relative;
  padding: 10px;
}
.task .task-desc {
  height: 40px;
  transition: all 0.3s;
  overflow: hidden;
}
.task:hover {
  border-color: #f4d72a;
  animation: mymove 0.5s infinite;
  animation-direction: alternate;
  animation-timing-function: linear;
}
@keyframes mymove {
  from {
    box-shadow: 0px 0px 0px #fff;
  }
  to {
    box-shadow: 0px 0px 30px #fff;
  }
}
.task:hover .task-desc {
  height: 115px;
  overflow-y: scroll;
}
.task .tasksn {
  font-size: 16px;
  font-weight: bolder;
  font-style: italic;
}
.dialog_bg {
  position: fixed;
  top: 0px;
  left: 0px;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.4);
}
.dialog {
  position: absolute;
  width: 300px;
  height: 210px;
  top: 50%;
  left: 50%;
  padding-top: 10px;
  transform: translate(-49%, -49%);
  border: 16px solid;
  border-image-source: url(/task.png);
  border-image-slice: 25 fill;
}
.dialog input {
  border: 5px solid #c0a920;
  background-color: #7d0000;
  color: #ddd;
  font-weight: bolder;
  margin-bottom: 15px;
  border-radius: 5px;
}
.dialog input::placeholder {
  color: #999;
}
.dialog .footer {
  display: flex;
  justify-content: space-evenly;
}
.dialog .footer > div {
  width: 100px;
  height: 30px;
  background-size: 60px;
  background-repeat: no-repeat;
  background-position: center;
}
.dialog .cancel {
  background-image: url("/cancel.png");
}
.dialog .confirm {
  background-image: url("/confirm.png");
}
.state {
  position: absolute;
  top: -8px;
  left: 50px;
  color: #ddd;
  font-weight: bolder;
  background-image: url("/achievement.png");
  background-repeat: no-repeat;
  background-size: 14px;
  background-position: 0px 2px;
  padding-left: 16px;
}
.mode {
  position: absolute;
  display: flex;
  width: 66px;
  height: 30px;
  top: -10px;
  right: 50px;
  background-image: url(/practise.png);
  background-repeat: no-repeat;
  background-size: 66px;
  transition: all 0.5s;
}
.mode.challenge {
  background-image: url(/challenge.png);
}
.mode .challenge,
.mode .practise {
  width: 50%;
  height: 100%;
}
.check_task {
  position: absolute;
  left: 330px;
  top: 12px;
  width: 130px;
  background-image: url(/point.png);
  background-repeat: no-repeat;
  background-size: 0px 30px;
  background-position: 18px 3px;
  transition: all 0.2s;
}
.check_task.started {
  background-size: 110px 30px;
}
.check_task .use {
  display: none;
  position: absolute;
  top: 9px;
  left: 32px;
  width: 100px;
  text-align: center;
  font-size: 13px;
}
.check_task.started .use {
  display: block;
}
.check_task.started .use span {
  color: #fff;
  font-weight: bolder;
}

.check_task img {
  width: 38px;
  height: 38px;
}

/* 设置滚动条的样式 */
::-webkit-scrollbar {
  width: 6px;
}
/* 滚动槽 */
::-webkit-scrollbar-track {
  border-radius: 10px;
}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: rgba(192, 169, 32, 0.6);
}
::-webkit-scrollbar-thumb:window-inactive {
  background: rgba(255, 0, 0, 0.4);
}
</style>
