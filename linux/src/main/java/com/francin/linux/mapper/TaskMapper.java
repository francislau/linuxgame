package com.francin.linux.mapper;

import com.francin.linux.pojo.Task;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TaskMapper {

    List<Task> queryTaskList();

    Task queryTaskById(long id);

    int addTask(Task task);

    int updateTask(Task task);

    int deleteTask(int id);

    int batchDeleteTask(String[] ids);
}
