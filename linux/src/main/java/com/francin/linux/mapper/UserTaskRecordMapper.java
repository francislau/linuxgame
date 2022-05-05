package com.francin.linux.mapper;

import com.francin.linux.pojo.UserTask;
import com.francin.linux.pojo.UserTaskRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserTaskRecordMapper {

    List<UserTaskRecord> queryUserTaskRecordList();

    List<UserTaskRecord> queryUserTaskRecordListByClassId(int id);

    UserTaskRecord queryUserTaskRecordById(int id);

    List<UserTaskRecord> queryUserTaskRecordByUserId(int id);

    int addUserTaskRecord(UserTaskRecord userTaskRecord);

    int updateUserTaskRecord(UserTaskRecord userTaskRecord);

    int deleteUserTaskRecord(int id);
}
