package com.francin.linux.service.impl;

import com.francin.linux.mapper.ChatMapper;
import com.francin.linux.pojo.Chat;
import com.francin.linux.pojo.Push;
import com.francin.linux.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.francin.linux.service.impl.WebSocketServerServiceImpl.MAIN;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private WebSocketServerServiceImpl webSocketServerService;

    @Override
    public List<Chat> queryChatList() {
        return chatMapper.queryChatList();
    }

    @Override
    public List<Chat> queryChatListByClassId(int id) {
        return chatMapper.queryChatListByClassId(id);
    }

    @Override
    public long addChat(Chat chat) {
        Push push = new Push();
        push.setChat(chat.getRealname()+":"+chat.getContent());
        push.setBullet(chat.getRealname()+":"+chat.getContent());
        webSocketServerService.sendAllMessage(MAIN, push.toString());

        return chatMapper.addChat(chat);
    }
}
