package com.francin.chat;

import com.francin.code.WebScoketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Auther: liaoshiyao
 * @Date: 2019/1/11 16:47
 * @Description: 测试后台websocket客户端
 */
@RestController
@RequestMapping("/websocket")
public class IndexController {

//    @Autowired
//    private WebScoketClient webScoketClient;
//
//    @GetMapping("/sendMessage")
//    public String sendMessage(String message){
//        webScoketClient.groupSending(message);
//        return message;
//    }

//    @Scheduled(fixedDelay = 2000)
//    public void heartBeat() {
//        webScoketClient.groupSending(HEARTCHECK);
//    }
}
