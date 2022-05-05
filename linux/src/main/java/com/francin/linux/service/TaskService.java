package com.francin.linux.service;


import com.francin.linux.pojo.Level;
import com.francin.linux.pojo.Task;

import java.util.List;

public interface TaskService {

    List<Task> queryTaskList();

    long addTask(Task task);

    int updateTask(Task task);

    int deleteTask(int id);

    int batchDeleteTask(String[] ids);

    List<Level> queryLevelList();

    long addLevel(Level task);

    int updateLevel(Level task);

    int deleteLevel(int id);

    int batchDeleteLevel(String[] ids);

}
