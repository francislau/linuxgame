package com.francin.linux.controller;

import com.francin.linux.service.WebSocketServerService;
import com.francin.linux.service.impl.WebSocketServerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.francin.linux.service.impl.WebSocketServerServiceImpl.AGENT;
import static com.francin.linux.service.impl.WebSocketServerServiceImpl.MAIN;

@RestController
@RequestMapping("/websocket")
public class WebSocketServerController {
    @Autowired
    private WebSocketServerService webSocketServerService;

    @RequestMapping("/sendAllWebSocket")
    public String test() {
        webSocketServerService.sendAllMessage(MAIN, "清晨起来打开窗，心情美美哒~");
        return "websocket群体发送！";
    }
    @RequestMapping("/sendAllAgentWebSocket")
    public String test1() {
        webSocketServerService.sendAllMessage(AGENT, "清晨起来打开窗，心情美美哒~");
        return "websocket群体发送！";
    }

    @RequestMapping("/sendOneWebSocket")
    public String sendOneWebSocket() {
//        webSocketServerService.sendOneMessage("DPS007", "只要你乖给你买条gai！");
        return "websocket单人发送";
    }
}