package com.francin.linux.controller;

import com.francin.linux.common.Result;
import com.francin.linux.mapper.LevelMapper;
import com.francin.linux.mapper.TaskMapper;
import com.francin.linux.pojo.Level;
import com.francin.linux.pojo.Task;
import com.francin.linux.service.TaskService;
import com.francin.linux.service.impl.TaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/queryList")
    public Map<String, Object> queryTaskList(){
//        List<Task> taskList = taskMapper.queryTaskList();
//        return taskList;
        return Result.make(Result.SUCCESS, "查询成功", "data", taskService.queryTaskList());
    }

    @PostMapping("/add")
    public Map<String, Object> addTask(@RequestBody Task task){
        log.error(task.toString());
        return Result.make(Result.SUCCESS, "添加成功", "data", taskService.addTask(task));
    }

    @PostMapping("/update")
    public Map<String, Object> updateTask(@RequestBody Task task){
        log.error(task.toString());
//        return taskMapper.updateTask(task);
        return Result.make(Result.SUCCESS, "修改成功", "data", taskService.updateTask(task));
    }

    @GetMapping("/delete")
    public Map<String, Object> deleteTask(@RequestParam("id") int id){
        return Result.make(Result.SUCCESS, "删除成功", "data", taskService.deleteTask(id));

    }

    @GetMapping("/batchDelete")
    public Map<String, Object> batchDeleteTask(@RequestParam("ids") String[] ids){
        return Result.make(Result.SUCCESS, "删除成功", "data", taskService.batchDeleteTask(ids));
    }


    @GetMapping("/queryLevelList")
    public Map<String, Object> queryLevelList(){
//        List<Level> levelList = levelMapper.queryLevelList();
        return Result.make(Result.SUCCESS, "查询成功", "data", taskService.queryLevelList());
    }

    @PostMapping("/addLevel")
    public Map<String, Object> addLevel(@RequestBody Level level){
        log.error(level.toString());
        return Result.make(Result.SUCCESS, "添加成功", "data", taskService.addLevel(level));
    }

    @PostMapping("/updateLevel")
    public Map<String, Object> updateLevel(@RequestBody Level task){
        log.error(task.toString());
        return Result.make(Result.SUCCESS, "修改成功", "data", taskService.updateLevel(task));
    }

    @GetMapping("/deleteLevel")
    public Map<String, Object> deleteLevel(@RequestParam("id") int id){
        return Result.make(Result.SUCCESS, "删除成功", "data", taskService.deleteLevel(id));
    }

    @GetMapping("/batchDeleteLevel")
    public Map<String, Object> batchDeleteLevel(@RequestParam("ids") String[] ids){
        return Result.make(Result.SUCCESS, "删除成功", "data", taskService.batchDeleteLevel(ids));

    }

}
