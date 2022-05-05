package com.francin.linux.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTask {

    // 练习模式
    public static final int PRACTISE_MODE = 1;
    // 挑战模式
    public static final int CHALLENGE_MODE = 2;

    private int id;
    private int userId;
    private int taskId;
    private String rand;
    private int taskOrder = -1;
    private float taskRatio = -1;
    private double taskScore = -1;
    private double taskScorePass = -1;
    private double taskScoreLength = -1;
    private double taskScoreAttempt = -1;
    private int attempt = -1;
    private Timestamp startAt;
    private Timestamp passAt;
    private long length = -1;
    private int attemptRank = -1;
    private int lengthRank = -1;
    private int passRank = -1;
    private int taskStatus = -1;
    private double practiseScore = -1;
    private double practiseScoreFirstPass = -1;
    private double practiseScoreLength = -1;
    private double practiseScoreAttempt = -1;
    private int practiseAttempt = -1;
    private Timestamp practiseStartAt;
    private Timestamp practiseMinStartAt;
    private Timestamp practiseMinPassAt;
    private long practiseMinLength = -1;
    private Timestamp practiseFirstPassAt;
    private int practiseAttemptRank = -1;
    private int practiseMinLengthRank = -1;
    private int practiseFirstPassRank = -1;
    private int status;
    private Timestamp createAt;
    private Timestamp updateAt;
    private int createBy;
    private int updateBy;

    private Task task;

}