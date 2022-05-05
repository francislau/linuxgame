package com.francin.linux.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.francin.linux.mapper.UserMapper;
import com.francin.linux.mapper.UserTaskRecordMapper;
import com.francin.linux.pojo.*;
import com.francin.linux.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private UserTaskRecordMapper userTaskRecordMapper;

    @Override
    public User login(User user) {
        return userMapper.queryTeacher(user.getUsername());
    }

    @Override
    public String home(int id) {
        JSONObject jsonObj = new JSONObject();

        List<Classes> classes = classesService.queryClassesList();
        List<User> users = studentService.queryStudentList();
        List<Task> tasks = taskService.queryTaskList();
        List<Classes> myClasses = classesService.queryClassesByUserId(id);
        jsonObj.put("classes", classes.size());
        jsonObj.put("users", users.size());
        jsonObj.put("tasks", tasks.size());
        jsonObj.put("myClasses", myClasses.size());

        JSONObject datas = new JSONObject();

        List<Rank> ranks = new ArrayList<>();
        List<Rank> pranks = new ArrayList<>();
        List<UserTaskRecord> records = new ArrayList<>();
        for (int i = 0; i < myClasses.size(); i++) {
            List<UserTask> userTasks = userTaskService.queryUserTaskByClassId(myClasses.get(i).getId());
            int start = 0;
            int finish = 0;
            for (int j = 0; j < userTasks.size(); j++) {
                if (userTasks.get(j).getTaskStatus() == 2) {
                    start++;
                }
                if (userTasks.get(j).getTaskStatus() == 3) {
                    start++;
                    finish++;
                }
            }
            JSONObject data = new JSONObject();
            data.put("start", start);
            data.put("finish", finish);
            data.put("total", userTasks.size());
            datas.put(myClasses.get(i).getName(), data);

            List<Rank> rank = studentService.rankStudentByClassId(myClasses.get(i).getId());
            List<Rank> prank = studentService.prankStudentByClassId(myClasses.get(i).getId());
            List<UserTaskRecord> record = userTaskRecordMapper.queryUserTaskRecordListByClassId(myClasses.get(i).getId());

            ranks.addAll(rank);
            pranks.addAll(prank);
            records.addAll(record);
        }
        jsonObj.put("datas", datas);

        jsonObj.put("ranks", ranks);
        jsonObj.put("pranks", pranks);
        jsonObj.put("records", records);


        return jsonObj.toString();
    }

    @Override
    public int chgPsw(User user){
        return userMapper.updateUserPassword(user);
    }
}
