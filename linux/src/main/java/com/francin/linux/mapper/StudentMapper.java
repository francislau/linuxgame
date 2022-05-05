package com.francin.linux.mapper;

import com.francin.linux.pojo.Rank;
import com.francin.linux.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StudentMapper {

    List<User> queryStudentList();

    int countStudent();

    List<Rank> rankStudent();

    List<Rank> rankStudentByClassId(int id);

    List<Rank> prankStudentByClassId(int id);

    Double getScoreById(int id);

    List<User> queryStudentListByClassId(int id);

    User queryStudentById(long id);

    User queryStudentByStuNo(String stuNo);

    int addStudent(User user);

    int updateStudent(User user);

    User queryStudentByKeyCodeUnverified(User user);

    int deleteStudent(int id);

    int batchDeleteStudent(String[] ids);
}
