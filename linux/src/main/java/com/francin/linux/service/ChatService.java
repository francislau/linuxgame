package com.francin.linux.service;


import com.francin.linux.pojo.Chat;
import com.francin.linux.pojo.Level;
import com.francin.linux.pojo.Task;

import java.util.List;

public interface ChatService {

    List<Chat> queryChatList();

    List<Chat> queryChatListByClassId(int id);

    long addChat(Chat chat);
}
