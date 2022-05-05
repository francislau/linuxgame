package com.francin.linux.mapper;

import com.francin.linux.pojo.Chat;
import com.francin.linux.pojo.Level;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChatMapper {

    List<Chat> queryChatList();

    List<Chat> queryChatListByClassId(int id);

    int addChat(Chat chat);
}
