package com.francin.linux.dao;

import com.francin.linux.mapper.StudentMapper;
import com.francin.linux.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;

@Repository
public class StudentDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(StudentDao.class);

    @Autowired
    private StudentMapper studentMapper;

    public int stuClass = 0;

    public void save(List<User> user){
        for(int i = 0;i < user.size(); i++){
            studentMapper.addStudent(user.get(i));
        }
    }
}
