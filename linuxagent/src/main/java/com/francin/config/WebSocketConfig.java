package com.francin.config;

import com.alibaba.fastjson.JSONObject;
import com.francin.LinuxagentApplication;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.TimeZone;


@Slf4j
@Component
@Configuration
public class WebSocketConfig {


    //    @Autowired
//    private WebSocketClient client;
    public static final String HEARTCHECK = "heartcheck";
    public static final String HALT = "halt";
    private static Boolean lockReconnect = false;
    private static WebSocketClient client;
//
//
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }


    public static WebSocketClient webSocketClient() {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI(LinuxagentApplication.remote), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {

                    log.info("[websocket] Connect Successfully");
                    log.info("\n" +
                            " #######  ##    ## \n" +
                            "##     ## ##   ##  \n" +
                            "##     ## ##  ##   \n" +
                            "##     ## #####    \n" +
                            "##     ## ##  ##   \n" +
                            "##     ## ##   ##  \n" +
                            " #######  ##    ## "
                    );
                    if(LinuxagentApplication.remote.indexOf("-1")>-1){
                        client.close();
                        System.exit(0);
                    } else {
                        startHeart();
                    }

                }

                @Override
                public void onMessage(String message) {
                    log.debug("[websocket] message={}", message);
                    startHeart();
                    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
                    if (HALT.equals(message)) {
                        log.info("An other client connected.");
                        client.close();
                        System.exit(0);
                    }
                    if (HEARTCHECK.equals(message)) {
                        return;
                    } else {
                        System.out.println(new Date().toLocaleString() + " Command In<<<<");
                    }

                    JSONObject jsonObject = JSONObject.parseObject(message);
                    String ip = "127.0.0.1";
                    int port = 22;
                    String username = "root";
                    String password = jsonObject.get("password").toString();
                    String mode = jsonObject.get("mode").toString();
                    String command = jsonObject.get("command").toString();

                    String re = null;
                    try {
                        re = exec(ip, port, username, password, command);
                    } catch (Exception e) {
                        re = "-2";
                        if ("1".equals(mode)) {
                            try { // 练习模式下尝试用123456再连一次
                                re = exec(ip, port, username, "123456", command);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        System.out.println(e.getMessage());
                    }

                    jsonObject.put("re", re);
                    System.out.println(new Date().toLocaleString() + " Result Out>>>>");
                    this.send(jsonObject.toJSONString());
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("[websocket] Connect close");
                    reConnect();
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                    log.info("[websocket] error={}", ex.getMessage());
                }
            };
            webSocketClient.connect();
            return webSocketClient;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Bean
    public static void startHeart() {
        log.debug("startHeart");

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                if (client != null && client.getReadyState() == WebSocket.READYSTATE.OPEN) {
                    client.send(HEARTCHECK);
                } else {
                    reConnect();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public static void reConnect() {
        System.out.println("Reconnect...");

        if (lockReconnect) {
            return;
        }
        lockReconnect = true;

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                client = WebSocketConfig.webSocketClient();
                lockReconnect = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


    }

    public static String exec(String ip, int port, String username, String password, String command) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, ip, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");

        session.setTimeout(120 * 1000);

        session.connect();
        //建立连接结束
        //发送指令
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        InputStream in = exec.getInputStream();
        exec.setCommand(command);
        exec.connect();
        String s = IOUtils.toString(in, "UTF-8");
        in.close();
        return s;
    }


}
