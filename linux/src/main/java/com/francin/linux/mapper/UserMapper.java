package com.francin.linux.mapper;

import com.francin.linux.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    User queryTeacher(String username);

    User queryById(int id);

    int updateUserPassword(User user);
}
