package com.francin.linux.service;

import com.alibaba.fastjson.JSONObject;
import com.francin.linux.pojo.UserTask;
import com.francin.linux.pojo.UserTaskOpen;

import java.util.List;

public interface UserTaskService {


    List<UserTask> queryUserTaskList();

    List<UserTask> queryUserTaskByUserId(int id);

    List<UserTask> queryUserTaskOpenedByModeAndUserId(int mode, int id);

    List<UserTask> queryUserTaskByClassId(int id);

    int deleteUserTask(int id);

    int addUserTask(int[] ids, List<UserTask> tasks);

    String startTask(int mode, int id, String uuid);

    void startTaskCallback(JSONObject jsonObject);

    String checkTask(int id, int mode, String uuid);

    void checkTaskCallback(JSONObject jsonObject);

    String getKeyCode(int id, String uuid);

    void getKeyCodeCallback(JSONObject jsonObject);

    void sortRankAndReCalcScoreByClassId(int id);

    List<UserTaskOpen> queryUserTaskOpenListByUserIdAndClassId(int userId, int id);

    int addUserTaskOpen(List<UserTaskOpen> userTaskOpen);

    int deleteUserTaskOpen(List<UserTaskOpen> userTaskOpen);

}
