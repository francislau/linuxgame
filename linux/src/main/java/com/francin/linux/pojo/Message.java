package com.francin.linux.pojo;

import lombok.Data;

@Data
public class Message {
    /**
     * 聊天类型：0群聊，1私聊
     */
    private int type;

    /**
     * 发送方
     */
    private String fromUser;

    /**
     * 接收方
     */
    private String name;

    /**
     * 消息
     */
    private String msg;

    private String content;
    private String date;
}
