package com.francin.linux.mapper;

import com.francin.linux.pojo.Classes;
import com.francin.linux.pojo.UserTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ClassesMapper {

    List<Classes> queryClassesList();

    List<Classes> queryClassesByUserId(int id);

    Classes queryClassesById(int id);

    int addClasses(Classes classes);

    int updateClasses(Classes classes);

    int deleteClasses(int id);

    int batchDeleteClasses(String[] ids);
}
