package com.francin;

import com.alibaba.fastjson.JSONObject;
import com.francin.config.WebSocketConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class LinuxagentApplication {

    public static String remote = "ws://linux.pingenetwork.com:8080/websocket/agent/";

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("need ID");
            System.exit(0);
        }
        if (args.length == 1) {
            System.out.println(remote + args[0]);
            remote = remote + args[0];
        }
        if (args.length == 2) {
            System.out.println("ws://" + args[1]);
            remote = "ws://" + args[1] + "/websocket/agent/" + args[0];
        }
        SpringApplication.run(LinuxagentApplication.class, args);
    }

}

