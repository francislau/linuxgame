package com.francin.linux.mapper;

import com.francin.linux.pojo.UserTask;
import com.francin.linux.pojo.UserTaskOpen;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserTaskOpenMapper {

    List<UserTaskOpen> queryUserTaskOpenListByClassId(int id);

    int addUserTaskOpen(UserTaskOpen userTaskOpen);

    int deleteUserTaskOpen(int id);
}
