package com.francin.linux.pojo;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Push {

    // 在线人数
    private int online;

    // 在线人名
    private List<String> onlinePeople;

    // 总人数
    private String countStudent;

    // 排名
    private List<Rank> rank;

    // 历史聊天内容
    private List<Chat> chats;

    // 新聊天内容
    private String chat;

    // 动态
    private List<UserTaskRecord> userTaskRecord;

    // 开始任务
    private Task startTask;

    // 检查任务
    private Task checkTask;

    // 弹幕
    private String bullet;

    // 设备码
    private KeyCode keyCode;

    // 成绩
    private Double score;

    // 主画面的人
    private List<Rank> person;

    // 是否在线
    private String onlineStatus;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Task {
        private String mode;
        private String taskId;
        private String state;
        private String uuid;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class KeyCode {
        private String keyCode;
        private String id;
        private String uuid;
    }

}


