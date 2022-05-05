package com.francin.linux.controller;

import com.francin.linux.annotation.UserLoginToken;
import com.francin.linux.common.Result;
import com.francin.linux.pojo.Chat;
import com.francin.linux.pojo.Level;
import com.francin.linux.pojo.Task;
import com.francin.linux.service.ChatService;
import com.francin.linux.service.impl.ChatServiceImpl;
import com.francin.linux.service.impl.TaskServiceImpl;
import com.francin.linux.service.impl.WebSocketServerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {


    @Autowired
    private ChatService chatService;

    @GetMapping("/queryList")
    public Map<String, Object> queryTaskList(){
        return Result.make(Result.SUCCESS, "查询成功", "data", chatService.queryChatList());
    }

    @UserLoginToken
    @PostMapping("/add")
    public Map<String, Object> addTask(@RequestBody Chat chat){
        log.error(chat.toString());
        return Result.make(Result.SUCCESS, "添加成功", "data", chatService.addChat(chat));
    }

}
