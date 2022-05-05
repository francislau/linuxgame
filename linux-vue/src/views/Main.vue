<template>
	<div id="container">
		<div ref="mask" class="mask">
			<div class="dialog">
				<div ref="dialog_title" class="dialog_title">任 务</div>
				<div v-if="canClose" @click="dialog_close" class="dialog_close"></div>
				<div class="dialog_body">
					<component ref="compRef" @close="dialog_close" :is="currentView"></component>
				</div>
			</div>
		</div>
		<div class="top">
			<div class="top_left">
				在线:{{ online }}/{{ studentCount }}{{ realname }}
			</div>
			<div class="top_name"></div>
			<div class="top_right">
				<div @click="open_info" class="top-icon info"></div>
				<div v-if="id" @click="logout" class="top-icon logout"></div>
				<div class="top-icon gem"></div>
				<div class="score">{{ score }}</div>
			</div>
		</div>
		<div class="body">
			<div class="game">
				<div :class="onlineStatus == '1' ? 'status online' : 'status'">
					<div></div>
					<div>{{ onlineStatus == "1" ? "在线" : "离线" }}</div>
				</div>
				<div class="onlinePeoples">
					<div v-for="(item,index) in onlinePeople">
						{{item}}
					</div>
				</div>
				<canvas id="canvas"></canvas>
			</div>
			<div class="right">
				<div class="rank">
					<div class="rank_title"></div>
					<div class="rank_body">
						<div v-for="(item, index) in rank" :key="index">
							<div :class="index < 3 ? 'rank_left rank_number' : 'rank_left'">
								{{ index + 1 }}
							</div>
							<div>{{ item.realname }}</div>
							<div>{{ item.score }}</div>
						</div>
					</div>
				</div>
				<div class="trend">
					<div class="chat_title"></div>
					<div class="trend_body">
						<div v-for="(item, index) in record" :key="index">
							{{ item }}
							<!-- {{ item.user.realname }}:
              {{
                item.taskStatus == 1
                  ? "检查了"
                  : item.taskStatus == 2
                  ? "开始了"
                  : "完成了"
              }}{{ item.mode == 1 ? "练习" : "挑战" }}#{{ item.task.taskSn }} -->
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="bottom">
			<div class="chatbox">
				<div id="chatmsg" class="chatmsg">
					<div v-for="(item, index) in chats" :key="index">
						<span>{{ item.realname }}：</span>
						<span>{{ item.content }}</span>
					</div>
				</div>
				<div class="chatsend">
					<input v-model="chatmsg" placeholder="限于讨论题目内容" @keyup.enter="chatsend" />
					<div @click="chatsend">发送</div>
				</div>
			</div>
			<div class="startbox">
				<img @click="open_task" src="/startgame.png" />
			</div>
		</div>
	</div>
</template>

<script setup>
	import axios from "axios";
	import {
		nextTick,
		onMounted,
		ref,
		shallowRef
	} from "vue";
	import game from "../assets/js/game.js";
	import astart from "../assets/js/astart.js";
	import bullet from "../assets/js/bullet.js";
	import Info from "../components/Info.vue";
	import Login from "../components/Login.vue";
	import ChgPsw from "../components/ChgPsw.vue";
	import Task from "../components/Task.vue";
	import WebSocketClient from "websocket";
	import {
		uuid
	} from "vue-uuid";

	import {
		utils
	} from "../assets/js/utils.js";

	const start = ref(null);
	const mask = ref(null);
	const dialog_title = ref(null);

	const currentView = shallowRef(null);
	const newStep = ref(0);
	const online = ref("-");
	const studentCount = ref("-");
	const rank = ref([]);
	const chatmsg = ref("");
	const chats = ref([]);
	const client = ref(null);
	const compRef = ref(null);
	const record = ref([]);
	const score = ref("— — —");
	const id = ref(null);
	const realname = ref("");
	const onlineStatus = ref("0");
	const canClose = ref(true);
	const onlinePeople = ref([])
	const ws = ref(null); //建立的连接
	const lockReconnect = ref(false); //是否真正建立连接
	const timeout = ref(5 * 1000); //30秒一次心跳
	const timeoutObj = ref(null); //心跳心跳倒计时
	const serverTimeoutObj = ref(null); //心跳倒计时
	const timeoutnum = ref(null); //断开 重连倒计时

	const dialog_close = (obj) => {
		mask.value.style.opacity = 0;
		setTimeout(() => {
			currentView.value = null;
			mask.value.style.display = "none";
		}, 500);

		if (obj && obj == "login" && localStorage.getItem("stuNo")) {
			client.value.close();
			initWebsocket();
		}
	};

	const open_task = () => {
		if (id.value) {
			dialog_title.value.innerHTML = "挑战榜";
			currentView.value = Task;

			mask.value.style.display = "flex";
			setTimeout(() => {
				mask.value.style.opacity = 1;
			}, 10);
		} else {
			open_info();
		}
	};

	const open_info = () => {
		dialog_title.value.innerHTML = id.value ? "设备码" : "登 录";
		currentView.value = id.value ? Info : Login;

		mask.value.style.display = "flex";
		setTimeout(() => {
			mask.value.style.opacity = 1;
		}, 10);
	};

	const logout = () => {
		utils.alert(
			"真的要退出吗？",
			true,
			true,
			() => {
				localStorage.clear();
				id.value = null;
				realname.value = "";
				initData();
				let s = utils.alert("已退出，下次再来哦", false, false);
				setTimeout(() => {
					utils.close(s);
				}, 1000);
			},
			() => {}
		);
	};

	const WIDTH = 1600;
	const HEIGHT = 960;
	const gameCanvas = new game.GameCanvas();
	const ctx = ref(null);
	const aarr = ref([]);
	const person = ref({});

	const chatsend = () => {
		if (id.value) {
			if (chatmsg.value) {
				// console.log(chatmsg.value)
				axios
					.post("/api/chat/add", {
						userId: localStorage.getItem("id"),
						realname: localStorage.getItem("realname"),
						content: chatmsg.value,
					})
					.then((response) => {
						console.log(response.data);
						if (response.data.result == "success") {
							chats.value.push({
								realname: localStorage.getItem("realname"),
								content: chatmsg.value,
							});
							chatmsg.value = "";

							nextTick(() => {
								scrollToBottom();
							});
						}
					})
					.catch(function(error) {
						// 请求失败处理
						console.log(error);
					});
			}
		} else {
			open_info();
		}
	};

	const scrollToBottom = () => {
		let el = document.getElementById("chatmsg");
		el.scrollTop = el.scrollHeight;
	};

	const goto = (id, to, realname) => {
		if (!person.value[id]) {
			let p = new game.GameImage(ctx.value, "/ren.jpg");
			// 起点
			// p.x = 32 * 8;
			// p.y = 32 * 23;
			p.x = 32 * 36;
			p.y = 32 * 27;

			p.setName(realname);
			person.value[id] = p;
		}

		let start = Math.ceil(person.value[id].score);
		let end = Math.ceil(to);
		let step = [];
		if (end >= start) {
			step = aarr.value.slice(start, end);
		} else {
			step = aarr.value.slice(end, start);
			step = step.reverse();
		}

		person.value[id].setNext(step);
		person.value[id].setScore(to);
	};

	const aaa = () => {
		console.log(person.value[91]);

		goto(91, 20);
		goto(91, 23);
		goto(91, 17);
		goto(91, 23);
		goto(91, 17);
		goto(91, 30);
		goto(91, 0);
		goto(91, 15);
		goto(91, 30);
		goto(91, 0);

		// initData();

		// let a = Math.floor(Math.random() * 50);
		// console.log(a)
		// let temp = new game.GameImage(ctx.value, "/ren.jpg")
		// // 起点
		// temp.x = 32 * 8
		// temp.y = 32 * 23
		// console.log(aarr.value.length)
		// temp.walk(aarr.value)
		// //temp.walk(aarr.value.slice(0, a))
		// temp.setName("刘星");
		// person.value.push(temp);
		// console.log(aarr.value.length)

		// let bullet1 = new bullet("恭喜刘星，闯关成功");

		//console.log(bullet)

		// var danmu = document.createElement('div');
		// danmu.style.cssText = "position:fixed; width: 200px; height: 50px;background-color: #000;"

		// let i = 0;
		// setInterval(() => {
		//     danmu.style.top = "100px"
		//     if (i < document.body.clientWidth) {
		//         danmu.style.left = (document.body.clientWidth - i++) + "px"
		//     }

		// }, 8);
		// document.getElementsByTagName('body')[0].appendChild(danmu);
	};
	const bbb = () => {
		let t = person.value.pop();
		console.log(t);
		t.x = 32 * 8;
		t.y = 32 * 30;
	};

	const initMap = () => {
		let map = [
			[1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,0,0,1,1,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,0,1,1,1,1,1,0,0,0,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,0,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,0,0,0,0,0,1,1,1,0,0,0,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,1,1,1,0,0,0,0,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
			[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
		];
		// 定义地图数组 0表示可以通过，1表示不可以通过
		let map1 = [
			//0 1 2 3 4 5 6 7
			[	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,], //0
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1,
			], //1
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1,
			], //2
			[
				1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //3
			[
				1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //4
			[
				1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //5
			[
				1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //6
			[
				1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //7
			[
				1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //8
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //9
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //10
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //11
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //12
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //13
			[
				1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1,
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //14
			[
				1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //15
			[
				1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //16
			[
				1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //17
			[
				1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //18
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //19
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1,
				1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //20
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //21
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //22
			[
				1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //23
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //24
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //25
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 1, 1, 1,
			], //26
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1,
			], //27
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1,
			], //28
			[
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1,
			], //29
		];

		let AStarPathFindingObj = new astart.AStarPathFinding(50, 30, map);

		aarr.value = AStarPathFindingObj.getPath(
			new astart.Node(36, 27),
			new astart.Node(20, 0),
			[1, 2, 3]
		);
		// aarr.value = AStarPathFindingObj.getPath(
		// 	new astart.Node(8, 23),
		// 	new astart.Node(21, 14),
		// 	[1, 2, 3]
		// );
	};
	const initGame = () => {
		gameCanvas.setCanvasSize(WIDTH, HEIGHT);
		gameCanvas.initCanvas();
		gameCanvas.fps = 60;
		ctx.value = gameCanvas.getContext();
		let gi_map = new game.GameImage(ctx.value, "/map.jpg");

		gameCanvas.update = function(ctx, dt) {
			gameCanvas.clear();
			gi_map.draw();

			for (const key in person.value) {
				if (Object.hasOwnProperty.call(person.value, key)) {
					const element = person.value[key];
					// console.log(element)
					element.draw();
				}
			}
		};
		gameCanvas.startGame();
	};
	const resetHeart = () => {
		//重置心跳
		//清除时间
		clearTimeout(timeoutObj.value);
		clearTimeout(serverTimeoutObj.value);
		//重启心跳L
		startHeart();
	};
	const startHeart = () => { //开启心跳
		timeoutObj.value && clearTimeout(timeoutObj.value);
		serverTimeoutObj.value && clearTimeout(serverTimeoutObj.value);
		timeoutObj.value = setTimeout(() => {
			//这里发送一个心跳，后端收到后，返回一个心跳消息，
			if (client.value.readyState == 1) { //如果连接正常
				client.value.send("heartcheck");
			} else { //否则重连
				reconnect();
			}
			serverTimeoutObj.value = setTimeout(function() {
				console.log("超时关闭")
				//超时关闭
				client.value.close();
			}, timeout.value);

		}, timeout.value)
	};
	const reconnect = () => { //重新连接
		if (lockReconnect.value) {
			return;
		}
		lockReconnect.value = true;
		//没连接上会一直重连，设置延迟避免请求过多
		timeoutnum.value && clearTimeout(timeoutnum.value);
		timeoutnum.value = setTimeout(() => {
			//新连接
			initWebsocket();
			lockReconnect.value = false;
		}, 5000);
	}

	const initWebsocket = () => {
		startHeart();
		let sessionId = "";
		if (localStorage.getItem("stuNo")) {
			sessionId = localStorage.getItem("stuNo");
		} else if (localStorage.getItem("sessionId")) {
			sessionId = localStorage.getItem("sessionId");
		} else {
			sessionId = uuid.v1().replace(new RegExp('-','g'),"");
			localStorage.setItem("sessionId", sessionId);
		}

		client.value = new WebSocketClient.w3cwebsocket(
			// "ws://localhost:8080/websocket/main/" + sessionId
			// "wss://"+location.host+"/websocket/main/" + sessionId
			"wss://linux.pingenetwork.com/websocket/main/" + sessionId
			// "ws://192.168.0.102:8080/websocket/main/" + sessionId
		);

		// console.log(client);

		client.value.onopen = (obj) => {
			console.log(obj);
			initData();
		};
		client.value.onmessage = (obj) => {

			resetHeart();
			
			if (obj.data == 'heartcheck') {
				return false;
			}
			console.log(obj);

			let res = JSON.parse(obj.data);

			if (res.startTask) {
				typeof compRef.value?.waitCallback === "function" ?
					compRef.value?.waitCallback(res.startTask) :
					false;
			}
			if (res.checkTask) {
				typeof compRef.value?.waitCheckCallback === "function" ?
					compRef.value?.waitCheckCallback(res.checkTask) :
					false;
			}
			if (res.keyCode) {
				typeof compRef.value?.keyCodeCallback === "function" ?
					compRef.value?.keyCodeCallback(res.keyCode) :
					false;
			}
			if (res.person) {
				console.log(res.person);
				for (let i = 0; i < res.person.length; i++) {
					const element = res.person[i];
					goto(element.id, element.score, element.realname);
				}
			}
			if (res.bullet) {
				console.log(res.bullet.indexOf("练习了#"));
				console.log(res.bullet.indexOf("挑战了#"));
				if (
					res.bullet.indexOf("开始了练习#") > -1 ||
					res.bullet.indexOf("开始了挑战#") > -1 ||
					res.bullet.indexOf("练习了#") > -1 ||
					res.bullet.indexOf("挑战了#") > -1
				) {
					console.log("initData");
					initData();
				}
				new bullet(res.bullet);
			}
			if (res.onlineStatus != undefined) {
				onlineStatus.value = res.onlineStatus;
			}
		};
		client.value.onerror = (obj) => {
			console.log(obj);
		};
	};

	const initData = () => {
		id.value = localStorage.getItem("id");
		realname.value = localStorage.getItem("realname") ?
			"【" + localStorage.getItem("realname") + "】" :
			"";

		if (localStorage.getItem("chgPsw") == 1) {
			setTimeout(() => {
				canClose.value = false;
				dialog_title.value.innerHTML = "修改密码";
				currentView.value = ChgPsw;
				mask.value.style.display = "flex";
				setTimeout(() => {
					mask.value.style.opacity = 1;
				}, 10);
			}, 1000);
		}
		axios
			.get("/api/student/init" + (id.value ? "/" + id.value : ""), {
				timeout: 6000,
			})
			.then((response) => {
				console.log(response.data);
				if (response.data.result == "success") {
					let data = response.data.data;

					online.value = data.online;
					studentCount.value = data.countStudent;
					rank.value = data.rank;
					chats.value = data.chats;
					// record.value = data.userTaskRecord;
					score.value = data.score ?? "— — —";
					onlineStatus.value = data.onlineStatus;
					onlinePeople.value = data.onlinePeople;

					record.value = [];
					for (let i = 0; i < data.userTaskRecord?.length; i++) {
						const element = data.userTaskRecord[i];
						let time = utils.getDateDiff(new Date(element.createAt).getTime());
						let op = "";
						if (element.taskStatus == 1) {
							op = element.mode == 1 ? "练习了" : "挑战了";
						}
						if (element.taskStatus == 2) {
							op = element.mode == 1 ? "开始练习" : "开始挑战";
						}
						if (element.taskStatus == 3) {
							op = element.mode == 1 ? "完成练习" : "挑战成功";
						}

						record.value.push(
							element.realname + ":" + time + op + "#" + element.taskSn
						);
					}

					for (let i = 0; i < data.person?.length; i++) {
						const element = data.person[i];
						// initPerson(element.id, element.realname);
						goto(element.id, element.score, element.realname);
					}

					// 等待页面渲染完
					nextTick(() => {
						scrollToBottom();
					});
				}
				if (
					response.data.result == "error" &&
					response.data.message == "token过期，请重新登录"
				) {
					let s = utils.alert("token过期，请重新登录", false, false);
					localStorage.clear();
					id.value = null;
					realname.value = "";
					dialog_close();
					initData();
					setTimeout(() => {
						utils.close(s);
					}, 1000);
				}
			})
			.catch(function(error) {
				// 请求失败处理
				console.log(error);
				utils.alert("网络连接失败，请刷新重试", false, false);
			});
	};

	onMounted(() => {
		initMap();
		initGame();
		initWebsocket();
	});
</script>

<style scoped>
	#container {
		width: 90vw;
		width: 1080px;
		margin: 0 auto;
		padding: 10px;
		background-color: #7d0000;
		position: relative;
		cursor: url("/cursor.png"), auto;
	}

	.mask {
		width: 100%;
		height: 100%;
		position: absolute;
		top: 0px;
		left: 0px;
		background-color: rgba(255, 255, 255, 0.6);
		display: none;
		opacity: 0;
		justify-content: center;
		align-items: center;
		transition: all 0.5s;
		z-index: 1;
	}

	.dialog {
		width: 600px;
		height: 400px;
		position: relative;
		border: 30px solid;
		border-image-source: url(/task.png);
		border-image-slice: 30 fill;
		padding-top: 20px;
	}

	.dialog_title {
		width: 306px;
		height: 83px;
		display: block;
		color: #fff;
		font-size: 40px;
		line-height: 73px;
		background-image: url(/dialog_title.png);
		background-size: cover;
		position: absolute;
		top: -65px;
		left: 150px;
	}

	.dialog_close {
		content: "";
		display: block;
		width: 40px;
		height: 40px;
		position: absolute;
		top: -50px;
		right: -20px;
		background-image: url("/close.png");
	}

	.dialog_body {
		overflow-y: scroll;
		width: 100%;
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.top {
		border: 5px solid #c0a920;
		margin-bottom: 10px;
		height: 5vw;
		min-height: 50px;
		border-radius: 10px;
		display: flex;
		align-items: center;
		padding: 0 10px;
	}

	.top_left {
		width: 33%;
		display: flex;
		justify-content: flex-start;
		font-size: 20px;
		color: #fff;
	}

	.top_name {
		width: 34%;
		height: 80px;
		background-image: url("/name.png");
		background-repeat: no-repeat;
		background-size: contain;
		background-position: center;
	}

	.top_right {
		width: 33%;
		display: flex;
		justify-content: flex-end;
	}

	.top-icon {
		width: 30px;
		height: 30px;
		background-size: contain;
		background-position: center;
		background-repeat: no-repeat;
		margin-right: 10px;
	}

	.info {
		background-image: url("/info.png");
	}

	.logout {
		background-image: url("/logout.png");
	}

	.gem {
		background-image: url("/gem.png");
	}

	.score {
		display: flex;
		justify-content: flex-start;
		align-items: center;
		color: #fff;
		font-weight: bolder;
		font-size: 28px;
	}

	.body {
		display: flex;
	}

	.game {
		width: 80%;
		position: relative;
	}

	.right {
		width: 20%;
		height: 520px;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.rank,
	.trend {
		border: 5px solid #c0a920;
		border-radius: 10px;
		height: calc(50% - 5px);
		margin-left: 10px;
		padding: 0 5px;
		box-sizing: border-box;
		color: #fff;
		text-align: left;
		line-height: 24px;
		font-size: 13px;
	}

	.rank {
		overflow-x: scroll;
	}

	.rank_content {
		display: flex;
	}

	.rank_content>div {
		width: 100%;
	}

	.rank_title {
		width: 100%;
		height: 50px;
		background-image: url("/rank_title1.png");
		background-repeat: no-repeat;
		background-position: center;
		background-size: contain;
	}

	.rank_title2 {
		background-image: url("/rank_title2.png");
	}

	.rank_body {
		display: flex;
		height: calc(100% - 50px);
		flex-direction: column;
		font-weight: bolder;
		overflow: scroll;
	}

	.rank_body>div {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.rank_body>div>div {
		width: calc(50% - 15px);
	}

	.trend_body {
		height: calc(100% - 50px);
		overflow: scroll;
	}

	.trend_body>div {
		white-space: nowrap;
	}

	.rank_number {
		background-image: url("/number.png");
		background-size: cover;
		line-height: 18px !important;
	}

	.rank_left {
		width: 19px !important;
		height: 23px;
		margin-right: 10px;
		text-align: center;
		font-size: 12px;
		line-height: 23px;
		background-repeat: no-repeat;
		background-size: contain;
		background-position-x: 1px;
	}

	.chat_title {
		width: 100%;
		height: 50px;
		background-image: url("/chat_title.png");
		background-repeat: no-repeat;
		background-position: center;
		background-size: contain;
	}

	.bottom {
		display: flex;
		height: 10vw;
		min-height: 100px;
	}

	.chatbox {
		border: 5px solid #c0a920;
		border-radius: 10px;
		box-sizing: border-box;
		width: 80%;
	}

	.chatbox .chatmsg {
		height: calc(100% - 30px);
		background-color: rgba(255, 255, 255, 0.3);
		color: #ccc;
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		padding-left: 10px;
		overflow: scroll;
		font-size: 12px;
	}

	.chatbox .chatsend {
		display: flex;
		align-items: center;
	}

	.chatbox .chatsend input {
		width: calc(100% - 140px);
		height: 29px;
		background-color: rgba(255, 255, 255, 0.4);
		border: 0px;
		background-image: url(/speak.png);
		background-repeat: no-repeat;
		background-size: 25px;
		background-position: 5px;
		padding-left: 40px;
		color: #ddd;
	}

	.chatbox .chatsend input::placeholder {
		color: #aaa;
	}

	.chatbox .chatsend>div {
		width: 100px;
		color: #aaa;
		font-weight: bolder;
	}

	.startbox {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 20%;
	}

	.startbox img {
		width: 90%;
	}

	canvas {
		width: 100%;
		/* height: 480px; */
	}

	.status {
		position: absolute;
		top: 50px;
		left: 10px;
		width: 50px;
		padding: 0 10px;
		border-radius: 10px;
		color: #fff;
		box-shadow: 0 0 20px #fff;
		background-color: #333;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.status.online {
		background-color: seagreen;
	}

	.status>div:first-child {
		width: 10px;
		height: 10px;
		border-radius: 50%;
		background-color: #fff;
	}

	.status.online>div:first-child {
		transition: 0.2s;
		animation: mymove 1s infinite;
		animation-direction: alternate;
		animation-timing-function: linear;
	}

	.onlinePeoples {
		position: absolute;
		top: 10px;
		right: 10px;
		opacity: 0.3;
		transition: all 0.4s;
		height: 86%;
		overflow-y: auto;
	}

	.onlinePeoples:hover {
		opacity: 1;
	}

	.onlinePeoples div {
		background-color: #FFF;
		padding: 5px 10px;
		border-radius: 10px;
		margin-top: 10px;
	}

	@keyframes mymove {
		from {
			opacity: 0;
		}

		to {
			opacity: 1;
		}
	}

	/* 设置滚动条的样式 */
	::-webkit-scrollbar {
		width: 6px;
		height: 6px;
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

	::-webkit-scrollbar-corner {
		background-color: transparent;
	}
</style>
