package com.francin.linux.service;

import com.francin.linux.pojo.Classes;

import java.util.List;

public interface ClassesService {
    List<Classes> queryClassesList();

    List<Classes> queryClassesByUserId(int id);

    long addClass(Classes classes);

    int updateClass(Classes classes);

    int deleteClass(int id);

    int batchDeleteClasses(String[] ids);
}
