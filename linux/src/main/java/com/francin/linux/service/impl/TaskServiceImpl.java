package com.francin.linux.service.impl;

import com.francin.linux.mapper.LevelMapper;
import com.francin.linux.mapper.TaskMapper;
import com.francin.linux.pojo.Level;
import com.francin.linux.pojo.Task;
import com.francin.linux.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private LevelMapper levelMapper;

    @Override
    public List<Task> queryTaskList() {
        return taskMapper.queryTaskList();
    }

    @Override
    public long addTask(Task task) {
        taskMapper.addTask(task);
        return task.getId();
    }

    @Override
    public int updateTask(Task task) {
        return taskMapper.updateTask(task);
    }

    @Override
    public int deleteTask(int id) {
        return taskMapper.deleteTask(id);
    }

    @Override
    public int batchDeleteTask(String[] ids) {
        return taskMapper.batchDeleteTask(ids);
    }

    @Override
    public List<Level> queryLevelList() {
        return levelMapper.queryLevelList();
    }

    @Override
    public long addLevel(Level level) {
        levelMapper.addLevel(level);
        return level.getId();
    }

    @Override
    public int updateLevel(Level task) {
        return levelMapper.updateLevel(task);
    }

    @Override
    public int deleteLevel(int id) {
        return levelMapper.deleteLevel(id);
    }

    @Override
    public int batchDeleteLevel(String[] ids) {
        return levelMapper.batchDeleteLevel(ids);
    }
}
