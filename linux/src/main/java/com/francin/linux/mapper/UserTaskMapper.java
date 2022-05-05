package com.francin.linux.mapper;

import com.francin.linux.pojo.UserTask;
import com.francin.linux.pojo.UserTaskOpen;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserTaskMapper {

    List<UserTask> queryUserTaskList();

    UserTask queryUserTaskById(int id);

    List<UserTask> queryUserTaskByUserId(int id);

    List<UserTask> queryUserTaskOpenedByUserId(int id);

    List<UserTask> queryUserTaskByClassId(int id);

    List<UserTask> queryUserTaskByClassIdAndOrderBy(int id, String orderBy);

    int addUserTask(UserTask userTask);

    int updateUserTask(UserTask userTask);

    int deleteUserTask(int id);

    List<UserTaskOpen> queryUserTaskOpenListByUserIdAndClassId(int userId, int id);

    int addUserTaskOpen(List<UserTaskOpen> userTaskOpen);

    int deleteUserTaskOpen(List<UserTaskOpen> userTaskOpen);
}
