package com.francin.linux.service.impl;

import com.francin.linux.mapper.StudentMapper;
import com.francin.linux.mapper.TaskMapper;
import com.francin.linux.mapper.UserTaskRecordMapper;
import com.francin.linux.pojo.*;
import com.francin.linux.service.ChatService;
import com.francin.linux.service.StudentService;
import com.francin.linux.service.UserTaskService;
import com.francin.linux.service.WebSocketServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.francin.linux.service.impl.WebSocketServerServiceImpl.MAIN;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {


    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private UserTaskRecordMapper userTaskRecordMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private WebSocketServerService webSocketServerService;
    @Autowired
    private Environment env;

    @Override
    public List<User> queryStudentList() {
        return studentMapper.queryStudentList();
    }

    @Override
    public Push init() {
        List<Chat> chats = chatService.queryChatList();
        List<Chat> chats1 = new ArrayList<>();
        for (int i = 0; i < chats.size(); i++) {
            Chat chat = new Chat();
            chat.setRealname(chats.get(i).getUser().getRealname());
            chat.setContent(chats.get(i).getContent());
            chats1.add(chat);
        }

        List<UserTaskRecord> userTaskRecord = getUserTaskRecord();
        List<UserTaskRecord> userTaskRecord1 = new ArrayList<>();
        for (int i = 0; i < userTaskRecord.size(); i++) {
            UserTaskRecord userTaskRecord2 = new UserTaskRecord();
            userTaskRecord2.setTaskStatus(userTaskRecord.get(i).getTaskStatus());
            userTaskRecord2.setMode(userTaskRecord.get(i).getMode());
            userTaskRecord2.setRealname(userTaskRecord.get(i).getUser().getRealname());
            userTaskRecord2.setTaskSn(userTaskRecord.get(i).getTask().getTaskSn());
            userTaskRecord2.setCreateAt(userTaskRecord.get(i).getCreateAt());
            userTaskRecord1.add(userTaskRecord2);
        }

        Push push = new Push();
        push.setOnline(WebSocketServerServiceImpl.getSessionPool(MAIN).size());
        push.setCountStudent(queryStudentList().size() + "");
        push.setRank(rankStudent());
        push.setChats(chats1);
        push.setUserTaskRecord(userTaskRecord1);
        push.setPerson(person(push.getRank()));
        return push;
    }

    @Override
    public Push initById(int id) {
        User user = studentMapper.queryStudentById(id);

        int online = 0;
        List<String> onlinePeople = new ArrayList<>();
        List<User> users = studentMapper.queryStudentListByClassId(user.getStuClass());
        log.error(users.toString());
        log.error(WebSocketServerServiceImpl.getSessionPool(MAIN).toString());
        List<String> sessionPool = WebSocketServerServiceImpl.getSessionPool(MAIN);
        for (int i = 0; i < users.size(); i++) {
            if (sessionPool.contains(users.get(i).getStuNo())) {
                online++;
                onlinePeople.add(users.get(i).getRealname());
            }
        }

        List<Chat> chats = chatService.queryChatListByClassId(user.getStuClass());
        List<Chat> chats1 = new ArrayList<>();
        for (int i = 0; i < chats.size(); i++) {
            Chat chat = new Chat();
            chat.setRealname(chats.get(i).getUser().getRealname());
            chat.setContent(chats.get(i).getContent());
            chats1.add(chat);
        }

        List<UserTaskRecord> userTaskRecord = queryUserTaskRecordListByClassId(user.getStuClass());
        List<UserTaskRecord> userTaskRecord1 = new ArrayList<>();
        for (int i = 0; i < userTaskRecord.size(); i++) {
            UserTaskRecord userTaskRecord2 = new UserTaskRecord();
            userTaskRecord2.setTaskStatus(userTaskRecord.get(i).getTaskStatus());
            userTaskRecord2.setMode(userTaskRecord.get(i).getMode());
            userTaskRecord2.setRealname(userTaskRecord.get(i).getUser().getRealname());
            userTaskRecord2.setTaskSn(userTaskRecord.get(i).getTask().getTaskSn());
            userTaskRecord2.setCreateAt(userTaskRecord.get(i).getCreateAt());
            userTaskRecord1.add(userTaskRecord2);
        }



        Push push = new Push();
        push.setOnline(online);
        push.setOnlinePeople(onlinePeople);
        push.setCountStudent(users.size() + "");
        push.setScore(getScoreById(id));
        push.setRank(rankStudent());
        push.setChats(chats1);
        push.setUserTaskRecord(userTaskRecord1);
        push.setPerson(person(push.getRank()));
        push.setOnlineStatus(webSocketServerService.checkOnline(user.getStuNo()));

        return push;
    }

    public List<Rank> person(List<Rank> ranks) {
        DecimalFormat df = new DecimalFormat("0.000");
        int mapTotal = Integer.parseInt(env.getProperty("custom.map-total"));

        List<Task> tasks = taskMapper.queryTaskList();
//        List<Rank> ranks = studentMapper.rankStudent();
        for (int i = 0; i < ranks.size(); i++) {
            Double score = ranks.get(i).getScore() * mapTotal / (tasks.size() * 100);
            ranks.get(i).setScore(Double.parseDouble(df.format(score)));
        }
        log.error(ranks.toString());
        return ranks;
    }

    @Override
    public List<User> queryStudentListByClassId(int id) {
        return studentMapper.queryStudentListByClassId(id);
    }

    @Override
    public User queryStudentById(int id) {
        return studentMapper.queryStudentById(id);
    }

    @Override
    public List<Rank> rankStudent() {
        return studentMapper.rankStudent();
    }

    @Override
    public List<Rank> rankStudentByClassId(int id) {
        return studentMapper.rankStudentByClassId(id);
    }

    @Override
    public List<Rank> prankStudentByClassId(int id) {
        return studentMapper.prankStudentByClassId(id);
    }

    @Override
    public List<UserTaskRecord> getUserTaskRecord() {
        return userTaskRecordMapper.queryUserTaskRecordList();
    }

    @Override
    public List<UserTaskRecord> queryUserTaskRecordListByClassId(int id) {
        return userTaskRecordMapper.queryUserTaskRecordListByClassId(id);
    }

    @Override
    public Double getScoreById(int id) {
        return studentMapper.getScoreById(id);
    }

    @Override
    public int updateStudent(User user) {
        return studentMapper.updateStudent(user);
    }

    @Override
    public int updateVerify(User user) {
        User own = studentMapper.queryStudentById(user.getId());

        log.error(user.getUserDevice().getKeyCodeUnverified());
        log.error(own.getUserDevice().getKeyCode());
        // 如果keycode和自己的一样，可以直接修改
        if (user.getUserDevice().getKeyCodeUnverified().equals(own.getUserDevice().getKeyCode())) {
            if (!user.getUserDevice().getConnUnverified().equals(own.getUserDevice().getConn())) {
                User temp = new User();
                temp.setId(user.getId());
                temp.getUserDevice().setConn(user.getUserDevice().getConnUnverified());
                studentMapper.updateStudent(temp);
            }
            return 0;
        } else { // 如果不一样，就查一下有没有跟其他同学的一样
            log.error(user.toString());
            User user1 = studentMapper.queryStudentByKeyCodeUnverified(user);
            if (user1 != null) {
                User user2 = new User();
                user2.setId(user.getId());
                UserDevice ud = new UserDevice();
                ud.setVerifyMsg("提交的" + user.getUserDevice().getKeyCodeUnverified() + "与" + user1.getRealname() + "的设备码一样，有可能是同一台设备");
                user2.setUserDevice(ud);
                studentMapper.updateStudent(user2);
                return -1;
            }

            user.getUserDevice().setVerifyMsg("");
            studentMapper.updateStudent(user);
            return 1;
        }

    }

    @Override
    public int verify(int[] ids) {
        int aff = 0;
        for (int i = 0; i < ids.length; i++) {
            User user = studentMapper.queryStudentById(ids[i]);
            if ("".equals(user.getUserDevice().getKeyCodeUnverified())) {
                continue;
            }
            UserDevice userDevice = new UserDevice();
//            userDevice.setConn(user.getUserDevice().getConnUnverified());
//            userDevice.setConnUnverified("");
            userDevice.setKeyCode(user.getUserDevice().getKeyCodeUnverified());
            userDevice.setKeyCodeUnverified("");
            userDevice.setVerifyMsg("");

            User user1 = new User();
            user1.setId(user.getId());
            user1.setUserDevice(userDevice);
            studentMapper.updateStudent(user1);
            aff++;
        }

        return aff;
    }

    @Override
    public int clear(int id) {
        User user = new User();
        UserDevice userDevice = new UserDevice();
        userDevice.setKeyCode("");
        user.setId(id);
        user.setUserDevice(userDevice);
        return studentMapper.updateStudent(user);
    }

    @Override
    public long addStudent(User user) {
        studentMapper.addStudent(user);

        return user.getId();
    }


    @Override
    public User login(User user) {
        return studentMapper.queryStudentByStuNo(user.getStuNo());
    }

    @Override
    public int deleteStudent(int id) {
        User user = studentMapper.queryStudentById(id);

        int re = studentMapper.deleteStudent(id);

        // 删除后更新排名和成绩
        userTaskService.sortRankAndReCalcScoreByClassId(user.getStuClass());

        return re;
    }

    @Override
    public int batchDeleteStudent(String[] ids) {
        return studentMapper.batchDeleteStudent(ids);
    }


}
