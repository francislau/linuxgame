package com.francin.linux.controller;

import com.francin.linux.annotation.CurrentUser;
import com.francin.linux.annotation.UserLoginToken;
import com.francin.linux.common.Result;
import com.francin.linux.pojo.User;
import com.francin.linux.pojo.UserTask;
import com.francin.linux.pojo.UserTaskOpen;
import com.francin.linux.service.UserTaskService;
import com.francin.linux.service.WebSocketServerService;
import com.francin.linux.service.impl.WebSocketServerServiceImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usertask")
public class UserTaskController {

    private static final org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private UserTaskService userTaskService;

    @GetMapping("/queryList")
    public Map<String, Object> queryUserTaskList() {
        return Result.make(Result.SUCCESS, "查询成功", "data", userTaskService.queryUserTaskList());
    }

    @UserLoginToken
    @GetMapping("/queryByUserId/{mode}/{id}")
    public Map<String, Object> queryUserTaskByUserId(@PathVariable int mode, @PathVariable int id, @CurrentUser User user) {
        System.out.println(user);
        if(user.getRole() == 3){
            return Result.make(Result.SUCCESS, "查询成功", "data", userTaskService.queryUserTaskOpenedByModeAndUserId(mode, id));
        } else {
            return Result.make(Result.SUCCESS, "查询成功", "data", userTaskService.queryUserTaskByUserId(id));
        }
    }

    @GetMapping("/queryByClassId/{id}")
    public Map<String, Object> queryUserTaskByClassId(@PathVariable int id) {
        return Result.make(Result.SUCCESS, "查询成功", "data", userTaskService.queryUserTaskByClassId(id));
    }

    @GetMapping("/delete/{id}")
    public Map<String, Object> deleteUserTask(@PathVariable int id) {
        return Result.make(Result.SUCCESS, "删除成功", "data", userTaskService.deleteUserTask(id));
    }

    @PostMapping("/add/{ids}")
    public Map<String, Object> addUserTask(@PathVariable int[] ids, @RequestBody List<UserTask> tasks) {
        return Result.make(Result.SUCCESS, "添加成功", "data", userTaskService.addUserTask(ids, tasks));
    }

    @UserLoginToken
    @GetMapping("/start/{mode}/{id}/{uuid}")
    public Map<String, Object> startUserTask(@PathVariable int mode, @PathVariable int id, @PathVariable String uuid) {
        try {
            return Result.make(Result.SUCCESS, "开始成功", "data", userTaskService.startTask(mode, id, uuid));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.make(Result.FAIL, "连接失败，请测试连接", "", "");
        }
    }

    @UserLoginToken
    @GetMapping("/check/{mode}/{id}/{uuid}")
    public Map<String, Object> checkTask(@PathVariable int mode, @PathVariable int id, @PathVariable String uuid) {

        try {
            String re = userTaskService.checkTask(mode, id, uuid);
            return Result.make(Result.SUCCESS, "连接成功", "data", re);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.make(Result.FAIL, "连接失败，请测试连接", "", "");
        }
    }

    @UserLoginToken
    @GetMapping("/getKeyCode/{id}/{uuid}")
    public Map<String, Object> getKeyCode(@PathVariable int id, @PathVariable String uuid) {

        try {
            String re = userTaskService.getKeyCode(id, uuid);
            return Result.make(Result.SUCCESS, "连接成功", "data", re);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.make(Result.FAIL, "连接失败，请测试连接", "", "");
        }
    }

    @GetMapping("/queryUserTaskOpenListByUserIdAndClassId/{userId}/{id}")
    public Map<String, Object> queryUserTaskOpenListByUserIdAndClassId(@PathVariable int userId, @PathVariable int id) {
        return Result.make(Result.SUCCESS, "查询成功", "data", userTaskService.queryUserTaskOpenListByUserIdAndClassId(userId, id));
    }

    @PostMapping("/addUserTaskOpen")
    public Map<String, Object> addUserTaskOpen(@RequestBody List<UserTaskOpen> userTaskOpen) {
        return Result.make(Result.SUCCESS, "添加成功", "data", userTaskService.addUserTaskOpen(userTaskOpen));
    }

    @PostMapping("/deleteUserTaskOpen")
    public Map<String, Object> deleteUserTaskOpen(@RequestBody List<UserTaskOpen> userTaskOpen) {
        return Result.make(Result.SUCCESS, "删除成功", "data", userTaskService.deleteUserTaskOpen(userTaskOpen));
    }
}
