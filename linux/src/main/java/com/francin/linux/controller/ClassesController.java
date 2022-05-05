package com.francin.linux.controller;

import com.francin.linux.annotation.UserLoginToken;
import com.francin.linux.mapper.ClassesMapper;
import com.francin.linux.pojo.Classes;
import com.francin.linux.pojo.User;
import com.francin.linux.service.ClassesService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
@Slf4j
public class ClassesController {

    @Autowired
    private ClassesService classesService;

    @GetMapping("/queryList")
    public List<Classes> queryClassesList(){
        return classesService.queryClassesList();
    }

    @UserLoginToken
    @GetMapping("/queryListByUserId/{id}")
    public List<Classes> queryClassesByUserId(@PathVariable int id){
        return classesService.queryClassesByUserId(id);
    }

    @PostMapping("/add")
    public long addClass(@RequestBody Classes classes){
        log.error(classes.toString());
        classesService.addClass(classes);
        log.error(classes.getId()+"");
        return classes.getId();
    }

    @PostMapping("/update")
    public int updateClass(@RequestBody Classes classes){
        log.error(classes.toString());
        return classesService.updateClass(classes);
    }

    @GetMapping("/delete")
    public int deleteClass(@RequestParam("id") int id){
        return classesService.deleteClass(id);
    }

    @GetMapping("/batchDelete")
    public int batchDeleteClasses(@RequestParam("ids") String[] ids){
        return classesService.batchDeleteClasses(ids);
    }

}
