package com.francin.linux.service;

import com.francin.linux.pojo.Rank;
import com.francin.linux.pojo.Push;
import com.francin.linux.pojo.User;
import com.francin.linux.pojo.UserTaskRecord;

import java.util.List;

public interface StudentService {

    User login(User user);

    List<User> queryStudentList();

    List<User> queryStudentListByClassId(int id);

    Push init();

    Push initById(int id);

    User queryStudentById(int id);

    List<Rank> rankStudent();

    List<Rank> rankStudentByClassId(int id);

    List<Rank> prankStudentByClassId(int id);

    List<UserTaskRecord> getUserTaskRecord();

    List<UserTaskRecord> queryUserTaskRecordListByClassId(int id);

    Double getScoreById(int id);

    int updateStudent(User user);

    int updateVerify(User user);

    int verify(int[] id);

    int clear(int id);

    long addStudent(User user);

    int deleteStudent(int id);

    int batchDeleteStudent(String[] ids);

}
