package com.francin.linux.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    // 任务开始
    public static final int TASK_START = 2;
    // 任务完成
    public static final int TASK_FINISH = 3;
    // 检查失败
    public static final int CHECK_FAIL = 1;

    private int id;
    private long levelId;
    private String taskSn;
    private String taskTitle;
    private String taskDesc;
    private String resourceId;
    private String reset;
    private String command;
    private String answer;
    private String errorMessage;
    private float ratio;
    private long status;
    private java.sql.Timestamp createAt;
    private java.sql.Timestamp updateAt;
    private long createBy;
    private long updateBy;

    private String resource;

    private UserTaskOpen userTaskOpen;

}
