package com.francin.linux.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.francin.linux.common.SpringContext;
import com.francin.linux.pojo.Push;
import com.francin.linux.service.UserTaskService;
import com.francin.linux.service.WebSocketServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.francin.linux.service.impl.UserTaskServiceImpl.*;

@Service
@ServerEndpoint("/websocket/{type}/{id}")
@Slf4j
public class WebSocketServerServiceImpl implements WebSocketServerService {


    public static final String MAIN = "main";
    public static final String AGENT = "agent";
    public static final String HALT = "halt";

    private Session session;
    private String type;
    private String sessionId;

    private static CopyOnWriteArraySet<WebSocketServerServiceImpl> webSockets = new CopyOnWriteArraySet<>();
    private static ConcurrentHashMap<String, Session> mainSessionPool = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Session> agentSessionPool = new ConcurrentHashMap<>();

//    public static int online = 0;
//
//    @Autowired
//    private UserTaskServiceImpl userTaskService;

//    private static Map<String,Session> sessionPool = new HashMap<>();

    public UserTaskService getUserTaskService() {
        return SpringContext.getBean(UserTaskService.class);
    }

    public static List<String> getSessionPool(String type) {
        List<String> re = new ArrayList<>();
        for (Map.Entry<String, Session> entry : (MAIN.equals(type) ? mainSessionPool : agentSessionPool).entrySet()) {
            re.add(entry.getKey());
        }
        return re;
    }


    @OnOpen
    @Override
    public String onOpen(Session session, @PathParam(value = "type") String type, @PathParam(value = "id") String id) throws IOException {

        if (id.indexOf("-1") > -1) {
            sendOneMessage(AGENT, id.replace("-1", ""), HALT);
        }
        session.setMaxIdleTimeout(10 * 1000);
        this.session = session;
//        webSockets.add(this);
        this.type = type;
        this.sessionId = id;
        if (MAIN.equals(type)) {
            // 只能同时一个地方连接
            if (mainSessionPool.containsKey(id)) {
                mainSessionPool.get(id).close();
            }
            mainSessionPool.put(id, session);
//            this.online = mainSessionPool.size();
        } else {
            // 只能同时一个地方连接
            if (agentSessionPool.containsKey(id)) {
                agentSessionPool.get(id).close();
            }
            agentSessionPool.put(id, session);

            Push push = new Push();
            push.setOnlineStatus("1");
            sendOneMessage(MAIN, id, push.toString());
        }

        System.out.println("【websocket消息】" + new Date().toLocaleString() + "有新的" + type + "的" + this.sessionId + "连接，总数为:" + (MAIN.equals(type) ? mainSessionPool : agentSessionPool).size());

        return session.getId();
    }

    @OnClose
    @Override
    public void onClose() {

//        System.out.println(this);
//        webSockets.remove(this);

        if (MAIN.equals(this.type)) {
            mainSessionPool.remove(this.sessionId);
//            this.online = mainSessionPool.size();
        } else {
            agentSessionPool.remove(this.sessionId);

            Push push = new Push();
            push.setOnlineStatus("0");
            sendOneMessage(MAIN, this.sessionId, push.toString());
        }

        System.out.println("【websocket消息】" + new Date().toLocaleString() + this.type + "的" + this.sessionId + "连接断开，总数为:" + (MAIN.equals(this.type) ? mainSessionPool : agentSessionPool).size());
    }

    @OnMessage
    @Override
    public void onMessage(String message) {
        log.error("【websocket消息】收到" + this.type + "客户端" + this.sessionId + "消息:" + message);

        if (HEARTCHECK.equals(message)) {
            synchronized (this.session) {
                System.out.println("receive heart");
                try {
                    session.getBasicRemote().sendText(HEARTCHECK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (STARTTASK.equals(jsonObject.get("type"))) {
                getUserTaskService().startTaskCallback(jsonObject);
            }
            if (CHECKTASK.equals(jsonObject.get("type"))) {
                getUserTaskService().checkTaskCallback(jsonObject);
            }
            if (GETKEYCODE.equals(jsonObject.get("type"))) {
                getUserTaskService().getKeyCodeCallback(jsonObject);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("Error while websocket. ", error);
    }

    // 此为广播消息
    public void sendAllMessage(String type, String message) {
        Iterator<Map.Entry<String, Session>> entries = (MAIN.equals(type) ? mainSessionPool : agentSessionPool).entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Session> entry = entries.next();
            entry.getValue().getAsyncRemote().sendText(message);
            System.out.println("【websocket消息】广播" + type + "消息:" + message);
//            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }

//
//        for (Map.Entry<String, Session> webSocket : mainSessionPool.entrySet()) {
//            System.out.println("【websocket消息】广播消息:" + message);
//            try {
//                webSocket.session.getAsyncRemote().sendText(message);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public String checkOnline(String id) {
        Session session = agentSessionPool.get(id);
        return session != null ? "1" : "0";
    }

    // 此为单点消息
    @Override
    public String sendOneMessage(String type, String id, String message) {

        Session session = (MAIN.equals(type) ? mainSessionPool : agentSessionPool).get(id);
        if (session != null) {
            synchronized (session) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "1";
        } else {
            return "0";
        }
    }


//    /**
//     * 每30秒发送一个心跳
//     */
//    @Scheduled(cron = "${websocket.pong.schedule.cron}")
//    public void heartBeat() {
////        System.out.println("执行定时任务开始");
//        Iterator<Map.Entry<String, Session>> entries = mainSessionPool.entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry<String, Session> entry = entries.next();
//
////            try {
////                entry.getValue().getAsyncRemote().sendPong(ByteBuffer.wrap("1".getBytes()));
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
////            System.out.println("【websocket消息】广播pong");
//        }
//
//        Iterator<Map.Entry<String, Session>> entries2 = agentSessionPool.entrySet().iterator();
//        while (entries2.hasNext()) {
//            Map.Entry<String, Session> entry = entries2.next();
//
////            try {
////                entry.getValue().getAsyncRemote().sendPong(ByteBuffer.wrap("1".getBytes()));
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
////            System.out.println("【websocket消息】广播pong");
//        }
//
//    }
}