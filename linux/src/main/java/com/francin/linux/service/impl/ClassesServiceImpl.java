package com.francin.linux.service.impl;

import com.francin.linux.mapper.ClassesMapper;
import com.francin.linux.pojo.Classes;
import com.francin.linux.service.ClassesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
@Slf4j
public class ClassesServiceImpl implements ClassesService {

    @Autowired
    private ClassesMapper classesMapper;

    @Override
    public List<Classes> queryClassesList(){
        List<Classes> classesList = classesMapper.queryClassesList();
        return classesList;
    }

    @Override
    public List<Classes> queryClassesByUserId(int id){
        List<Classes> classesList = classesMapper.queryClassesByUserId(id);
        return classesList;
    }

    @Override
    public long addClass(Classes classes){
        log.error(classes.toString());
        classesMapper.addClasses(classes);
        log.error(classes.getId()+"");
        return classes.getId();
    }

    @Override
    public int updateClass(Classes classes){
        log.error(classes.toString());
        return classesMapper.updateClasses(classes);
    }

    @Override
    public int deleteClass(int id){
        return classesMapper.deleteClasses(id);
    }

    @Override
    public int batchDeleteClasses(String[] ids){
        return classesMapper.batchDeleteClasses(ids);
    }

}
