package com.francin.linux.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.francin.linux.common.Utils;
import com.francin.linux.mapper.StudentMapper;
import com.francin.linux.mapper.TaskMapper;
import com.francin.linux.mapper.UserTaskMapper;
import com.francin.linux.mapper.UserTaskRecordMapper;
import com.francin.linux.pojo.*;
import com.francin.linux.service.UserTaskService;
import com.francin.linux.service.WebSocketServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

import static com.francin.linux.pojo.Task.*;
import static com.francin.linux.pojo.UserTask.CHALLENGE_MODE;
import static com.francin.linux.pojo.UserTask.PRACTISE_MODE;
import static com.francin.linux.service.impl.SshServiceImpl.ERROR;
import static com.francin.linux.service.impl.SshServiceImpl.OK;
import static com.francin.linux.service.impl.WebSocketServerServiceImpl.AGENT;
import static com.francin.linux.service.impl.WebSocketServerServiceImpl.MAIN;

@Service
@Slf4j
public class UserTaskServiceImpl implements UserTaskService {

    // 开始任务
    public static final String STARTTASK = "starttask";
    // 检查任务
    public static final String CHECKTASK = "checktask";
    // 心跳检查
    public static final String HEARTCHECK = "heartcheck";
    // 获取设备码
    public static final String GETKEYCODE = "getkeycode";
    // 检查是否在线
    public static final String CHECKONLINE = "checkonline";

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserTaskMapper userTaskMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private SshServiceImpl sshService;
    @Autowired
    private WebSocketServerService webSocketServerService;
    @Autowired
    private UserTaskRecordMapper userTaskRecordMapper;
    @Autowired
    private Utils utils;
    @Autowired
    private Environment env;

    @Override
    public List<UserTask> queryUserTaskList() {
        List<UserTask> userTaskList = userTaskMapper.queryUserTaskList();
        for (int i = 0; i < userTaskList.size(); i++) {
            String rand = userTaskList.get(i).getRand();
            Task task = userTaskList.get(i).getTask();
            task.setTaskDesc(utils.replace(task.getTaskDesc(), rand, PRACTISE_MODE));
            userTaskList.get(i).setTask(task);
        }
        return userTaskList;
    }

    @Override
    public List<UserTask> queryUserTaskByUserId(int id) {
        List<UserTask> userTaskList = userTaskMapper.queryUserTaskByUserId(id);
        for (int i = 0; i < userTaskList.size(); i++) {
            String rand = userTaskList.get(i).getRand();
            Task task = userTaskList.get(i).getTask();
            task.setTaskDesc(utils.replace(task.getTaskDesc(), rand, PRACTISE_MODE));
            userTaskList.get(i).setTask(task);
        }
        return userTaskList;
    }

    @Override
    public List<UserTask> queryUserTaskOpenedByModeAndUserId(int mode, int id) {
        List<UserTask> userTaskList = userTaskMapper.queryUserTaskOpenedByUserId(id);
        for (int i = 0; i < userTaskList.size(); i++) {
            String rand = userTaskList.get(i).getRand();
            Task task = userTaskList.get(i).getTask();
            task.setTaskDesc(utils.replace(task.getTaskDesc(), rand, mode));
            userTaskList.get(i).setTask(task);
        }
        return userTaskList;
    }

    @Override
    public List<UserTask> queryUserTaskByClassId(int id) {
        List<UserTask> userTaskList = userTaskMapper.queryUserTaskByClassId(id);
        for (int i = 0; i < userTaskList.size(); i++) {
            String rand = userTaskList.get(i).getRand();
            Task task = userTaskList.get(i).getTask();
            task.setTaskDesc(utils.replace(task.getTaskDesc(), rand, PRACTISE_MODE));
            userTaskList.get(i).setTask(task);
        }
        return userTaskList;
    }

    @Override
    public int deleteUserTask(int id) {
        return userTaskMapper.deleteUserTask(id);
    }

    @Override
    public int addUserTask(int[] ids, List<UserTask> tasks) {
        log.error(tasks.size() + "");
        int re = 0;
        for (int i = 0; i < ids.length; i++) {
            for (int j = 0; j < tasks.size(); j++) {
                UserTask userTask = tasks.get(j);
                userTask.setUserId(ids[i]);
                userTaskMapper.addUserTask(userTask);
                if (userTask.getId() > 0) {
                    re++;
                }
            }
        }
        return re;
    }

    @Override
    public String startTask(int mode, int id, String uuid) {

        // 执行清除命令
        UserTask userTask = userTaskMapper.queryUserTaskById(id);
        Task task = taskMapper.queryTaskById(userTask.getTaskId());
        User user = studentMapper.queryStudentById(userTask.getUserId());

        String command = utils.replace(task.getReset(), userTask.getRand(), mode);
        command = utils.replaceRes(command, task.getResource());
        command = env.getProperty("custom.check-duuid") + " && " + command;
        log.error("发送命令");
        log.error(command);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", STARTTASK);
        jsonObject.put("mode", mode);
        jsonObject.put("id", id);
        jsonObject.put("uuid", uuid);
        jsonObject.put("password", user.getUserDevice().getConnPass());
        jsonObject.put("command", command);
        log.error(jsonObject.toString());

        return webSocketServerService.sendOneMessage(AGENT, user.getStuNo(), jsonObject.toJSONString());
    }

    @Override
    public void startTaskCallback(JSONObject jsonObject) {


        // 执行清除命令
//        UserTask userTask = userTaskMapper.queryUserTaskById(id);
//        Task task = taskMapper.queryTaskById(userTask.getTaskId());
//        String command = utils.replace(task.getRest(), userTask.getRand());
//        command = env.getProperty("custom.check-duuid") + " && " + command;
//
//        User user = studentMapper.queryStudentById(userTask.getUserId());
//
//
//        String conn = mode == PRACTISE_MODE ? user.getUserDevice().getConnPractise() : user.getUserDevice().getConn();
//
//        JSONObject jsonObject = JSONObject.parseObject(conn);
//        String ip = jsonObject.getString("ip");
//        int port = Integer.parseInt(jsonObject.getString("port"));
//        String username = jsonObject.getString("username");
//        String password = jsonObject.getString("password");
//
//        String re = sshService.exec(ip, port, username, mode == PRACTISE_MODE ? password : user.getUserDevice().getConnPass(), command);
//

        log.error(jsonObject.toString());

        int mode = (int) jsonObject.get("mode");
        int id = (int) jsonObject.get("id");
        String re = (String) jsonObject.get("re");
        String uuid = (String) jsonObject.get("uuid");

        UserTask userTask = userTaskMapper.queryUserTaskById(id);
        Task task = taskMapper.queryTaskById(userTask.getTaskId());
        User user = studentMapper.queryStudentById(userTask.getUserId());


        String[] res = re.split("\n");

        if (res.length != 2) {  // 少于2行的直接返回

            Push push = new Push();
            push.setStartTask(new Push().new Task(mode + "", id + "", re, uuid));
//            Push.StartTask s = push.new StartTask(id + "", ERROR, uuid);
//            push.setStartTask(OK.equals(s)?task.getId()+"":s);
//            push.setStartTask(new Push.new StartTask(1,1,1));
            webSocketServerService.sendOneMessage(MAIN, user.getStuNo(), push.toString());

            return;
        }

        log.error("res[0]");
        log.error(res[0]);
        log.error("res[1]");
        log.error(res[1]);

        UserTask userTask1 = new UserTask();
        userTask1.setId(id);

        String s = ERROR;
        if (mode == PRACTISE_MODE) {
            s = res[1];
            if (userTask.getPractiseStartAt() == null) {
                userTask1.setPractiseStartAt(new Timestamp(System.currentTimeMillis()));
            }
            if (userTask.getPractiseMinStartAt() == null) {
                userTask1.setPractiseMinStartAt(userTask1.getPractiseStartAt());
            }
//            UserTaskMapper.class.getDeclaredMethod("updateUserTask", UserTask.class);
            userTaskMapper.updateUserTask(userTask1);

            Push push = new Push();
            push.setBullet(user.getRealname() + "开始了练习#" + task.getTaskSn());
            webSocketServerService.sendAllMessage(MAIN, push.toString());
        } else if (user.getUserDevice().getKeyCode().equals(res[0])) {
            s = res[1];
            userTask1.setTaskStatus(TASK_START);
            if (userTask.getStartAt() == null) {
                userTask1.setStartAt(new Timestamp(System.currentTimeMillis()));
            }
            userTaskMapper.updateUserTask(userTask1);

            Push push = new Push();
            push.setBullet(user.getRealname() + "开始了挑战#" + task.getTaskSn());
            webSocketServerService.sendAllMessage(MAIN, push.toString());
        }

        log.error("startUserTask");
        log.error(userTask1.toString());

        if (OK.equals(s)) {
            // 记录user_task_record
            UserTaskRecord userTaskRecord = new UserTaskRecord();
            userTaskRecord.setUserId(userTask.getUserId());
            userTaskRecord.setUserTaskId(id);
            userTaskRecord.setMode(mode);
            userTaskRecord.setTaskStatus(TASK_START);
            userTaskRecord.setKeyCode(res[0]);
            userTaskRecordMapper.addUserTaskRecord(userTaskRecord);
        }

        Push push = new Push();
        push.setStartTask(new Push().new Task(mode + "", id + "", s, uuid));
        webSocketServerService.sendOneMessage(MAIN, user.getStuNo(), push.toString());

    }

    @Override
    public String checkTask(int mode, int id, String uuid) {
        log.error(id + "");

        UserTask userTask = userTaskMapper.queryUserTaskById(id);
        User user = studentMapper.queryStudentById(userTask.getUserId());
        Task task = userTask.getTask();
        String command = utils.replace(task.getCommand(), userTask.getRand(), mode);
        command = env.getProperty("custom.check-duuid") + " && " + command;


        log.error("发送命令");
        log.error(command);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", CHECKTASK);
        jsonObject.put("mode", mode);
        jsonObject.put("id", id);
        jsonObject.put("uuid", uuid);
        jsonObject.put("password", user.getUserDevice().getConnPass());
        jsonObject.put("command", command);
        log.error(jsonObject.toString());

        return webSocketServerService.sendOneMessage(AGENT, user.getStuNo(), jsonObject.toJSONString());
    }

    @Override
    public void checkTaskCallback(JSONObject jsonObject) {

        log.error(jsonObject.toString());
        DecimalFormat df = new DecimalFormat("#.00");

        int mode = (int) jsonObject.get("mode");
        int id = (int) jsonObject.get("id");
        String re = (String) jsonObject.get("re");
        String uuid = (String) jsonObject.get("uuid");

        UserTask userTask = userTaskMapper.queryUserTaskById(id);
        Task task = taskMapper.queryTaskById(userTask.getTaskId());
        User user = studentMapper.queryStudentById(userTask.getUserId());
//
//        String conn = mode == PRACTISE_MODE ? user.getUserDevice().getConnPractise() : user.getUserDevice().getConn();
//
//        JSONObject jsonObject = JSONObject.parseObject(conn);
//        String ip = jsonObject.getString("ip");
//        int port = Integer.parseInt(jsonObject.getString("port"));
//        String username = jsonObject.getString("username");
//        String password = jsonObject.getString("password");
//        String re = sshService.exec(ip, port, username, mode == PRACTISE_MODE ? password : user.getUserDevice().getConnPass(), command);
//        log.error("command");
//        log.error(command);
        String[] res = re.split("\n");

        if (res.length != 2) {  // 少于2行的直接返回
            Push push = new Push();
            push.setCheckTask(new Push().new Task(mode + "", id + "", re, uuid));
            webSocketServerService.sendOneMessage(MAIN, user.getStuNo(), push.toString());
            return;
        }

        log.error("res[0]");
        log.error(res[0]);
        log.error("res[1]");
        log.error(res[1]);


        String s = ERROR;
        if (mode == PRACTISE_MODE) {
            s = res[1];
            UserTask userTask1 = new UserTask();
            userTask1.setId(userTask.getId());
            userTask1.setPractiseAttempt(userTask.getPractiseAttempt() + 1);
            userTask1.setPractiseFirstPassAt(userTask.getPractiseFirstPassAt());

            if (OK.equals(s)) {
                userTask1.setPractiseStartAt(new Timestamp(0));

                if (userTask.getPractiseMinPassAt() == null) {
                    userTask1.setPractiseMinPassAt(new Timestamp(System.currentTimeMillis()));
                    userTask1.setPractiseFirstPassAt(userTask1.getPractiseMinPassAt());
                    userTask1.setPractiseMinLength(userTask1.getPractiseMinPassAt().getTime() - userTask.getPractiseMinStartAt().getTime());
                } else if (System.currentTimeMillis() - userTask.getPractiseStartAt().getTime() < userTask.getPractiseMinLength()) {
                    // 当时完成时长小于数据库记录的时长就更新数据库
                    userTask1.setPractiseMinStartAt(userTask.getPractiseStartAt());
                    userTask1.setPractiseMinPassAt(new Timestamp(System.currentTimeMillis()));
                    userTask1.setPractiseMinLength(userTask1.getPractiseMinPassAt().getTime() - userTask1.getPractiseMinStartAt().getTime());
                }

            }

            userTaskMapper.updateUserTask(userTask1);

            if (OK.equals(s)) {
                // 更新提交次数排名，次数越大排名越前
                /*
                值  排名
                20  4
                10  5
                40  1
                30  2
                25  3
                (25->26 或25->35 或25->45 再考虑新增的，新增的就是排名最后的)
                 */
                List<UserTask> userTasks = userTaskMapper.queryUserTaskByClassId(user.getStuClass());

                int orgRank = userTask.getPractiseAttemptRank();
                int rank = 0;
                int max = 0;
                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getPractiseAttemptRank() > 0) {
                        // 保存最大的排名
                        if (max < curUserTask.getPractiseAttemptRank()) {
                            max = curUserTask.getPractiseAttemptRank();
                        }
                    }
                }
                // 如果为0就是新增的，总数自增1再赋值
                if (orgRank == 0) {
                    orgRank = ++max;
                }

                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);

                    // 判断同一个任务内，并且有排名的
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getPractiseAttemptRank() > 0) {

                        UserTask userTask2 = new UserTask();
                        userTask2.setId(curUserTask.getId());
                        userTask2.setPractiseAttemptRank(curUserTask.getPractiseAttemptRank());
                        // 查出排名靠前的值
                        if (curUserTask.getPractiseAttemptRank() < orgRank) {
                            // 靠前的没有当前用户的值大，给他们排名加 1
                            if (curUserTask.getPractiseAttempt() < userTask1.getPractiseAttempt()) {
                                userTask2.setPractiseAttemptRank(curUserTask.getPractiseAttemptRank() + 1);

                                rank++;
                            }
                        }

                        // 更新他人成绩
//                        DecimalFormat df = new DecimalFormat("#.00");
                        double score = 10 * (max == 1 ? 1 : (double) (max - userTask2.getPractiseAttemptRank()) / (max - 1));
                        score = Double.parseDouble(df.format(score)); // 保留2位小数

                        userTask2.setPractiseScoreAttempt(score);
//                        userTask2.setPractiseScoreAttempt(score == 0 ? -1 : score);  // 数据库mapper要置为0时需要改成-1
                        userTask2.setPractiseScore(Double.parseDouble(df.format(70 + score + curUserTask.getPractiseScoreLength() + curUserTask.getPractiseScoreFirstPass())));
                        userTasks.get(i).setPractiseScoreAttempt(score);
                        userTaskMapper.updateUserTask(userTask2);
                    }

                }
                UserTask userTask3 = new UserTask();
                userTask3.setId(userTask.getId());

                userTask3.setPractiseAttemptRank(orgRank - rank);

                // 更新自己的提交次数成绩
                double score1 = 10;
                if (max > 1) {
                    score1 = 10 * (double) (max - userTask3.getPractiseAttemptRank()) / (max - 1);
//                    DecimalFormat df = new DecimalFormat("#.00");
                    score1 = Double.parseDouble(df.format(score1));
                }
                userTask3.setPractiseScoreAttempt(score1);
//                userTask3.setPractiseScoreAttempt(score1 == 0 ? -1 : score1);

                userTask3.setPractiseScore(Double.parseDouble(df.format(70 + score1 + userTask.getPractiseScoreLength() + userTask.getPractiseScoreFirstPass())));
                userTask.setPractiseScoreAttempt(score1);
                userTaskMapper.updateUserTask(userTask3);

                // 更新最短用时排名
                /*
                值  排名
                20  2
                10  1
                40  5
                30  4
                25  3
                (25->26 或25->35 或25->45 再考虑新增的，新增的就是排名最后的)
                 */
                int orgRank2 = userTask.getPractiseMinLengthRank();
                int rank2 = 0;
                int max2 = 0;
                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getPractiseMinLengthRank() > 0) {
                        // 保存最大的排名
                        if (max2 < curUserTask.getPractiseMinLengthRank()) {
                            max2 = curUserTask.getPractiseMinLengthRank();
                        }
                    }
                }
                if (orgRank2 == 0) {
                    orgRank2 = ++max2;
                }

                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);

                    // 判断同一个任务内，并且有排名的
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getPractiseMinLengthRank() > 0) {

                        UserTask userTask2 = new UserTask();
                        userTask2.setId(curUserTask.getId());
                        userTask2.setPractiseMinLengthRank(curUserTask.getPractiseMinLengthRank());
                        // 查出排名靠前的值
                        if (curUserTask.getPractiseMinLengthRank() < orgRank2) {
                            // 靠前的没有当前用户的值小，给他们排名加 1
                            if (curUserTask.getPractiseMinLength() > userTask1.getPractiseMinLength()) {
                                userTask2.setPractiseMinLengthRank(curUserTask.getPractiseMinLengthRank() + 1);

                                rank2++;
                            }
                        }

                        // 更新他人成绩
//                        DecimalFormat df = new DecimalFormat("#.00");
                        double score = 10 * (max2 == 1 ? 1 : (double) (max2 - userTask2.getPractiseMinLengthRank()) / (max2 - 1));
                        score = Double.parseDouble(df.format(score)); // 保留2位小数

                        userTask2.setPractiseScoreLength(score);
//                        userTask2.setPractiseScoreLength(score == 0 ? -1 : score); // 数据库mapper要置为0时需要改成-1
                        userTask2.setPractiseScore(Double.parseDouble(df.format(70 + curUserTask.getPractiseScoreAttempt() + score + curUserTask.getPractiseScoreFirstPass())));
                        userTasks.get(i).setPractiseScoreLength(score);
                        userTaskMapper.updateUserTask(userTask2);

                    }
                }
                UserTask userTask4 = new UserTask();
                userTask4.setId(userTask.getId());

                userTask4.setPractiseMinLengthRank(orgRank2 - rank2);

                // 更新自己的最短用时成绩
                double score2 = 10;
                if (max2 > 1) {
                    score2 = 10 * (double) (max2 - userTask4.getPractiseMinLengthRank()) / (max2 - 1);
//                    DecimalFormat df = new DecimalFormat("#.00");
                    score2 = Double.parseDouble(df.format(score2));
                }
                userTask4.setPractiseScoreLength(score2);
//                userTask4.setPractiseScoreLength(score2 == 0 ? -1 : score2);

                userTask4.setPractiseScore(Double.parseDouble(df.format(70 + userTask.getPractiseScoreAttempt() + score2 + userTask.getPractiseScoreFirstPass())));
                userTask.setPractiseScoreLength(score2);
                userTaskMapper.updateUserTask(userTask4);


                // 更新首次完成时间排名
                /*
                值  排名
                20  2
                10  1
                40  5
                30  4
                25  3
                (25->26 或25->35 或25->45 再考虑新增的，新增的就是排名最后的)
                 */
                int orgRank3 = userTask.getPractiseFirstPassRank();
                int rank3 = 0;
                int max3 = 0;
                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getPractiseFirstPassRank() > 0) {
                        // 保存最大的排名
                        if (max3 < curUserTask.getPractiseFirstPassRank()) {
                            max3 = curUserTask.getPractiseFirstPassRank();
                        }
                    }
                }
                if (orgRank3 == 0) {
                    orgRank3 = ++max3;
                }

                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);

                    // 判断同一个任务内，并且有排名的
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getPractiseFirstPassRank() > 0) {

                        UserTask userTask2 = new UserTask();
                        userTask2.setId(curUserTask.getId());
                        userTask2.setPractiseFirstPassRank(curUserTask.getPractiseFirstPassRank());
                        // 查出排名靠前的值
                        if (curUserTask.getPractiseFirstPassRank() < orgRank3) {
                            // 靠前的没有当前用户的值小，给他们排名加 1
                            log.error(curUserTask.toString());
                            log.error(userTask.toString());
                            if (curUserTask.getPractiseFirstPassAt().getTime() > userTask1.getPractiseFirstPassAt().getTime()) {
                                userTask2.setPractiseFirstPassRank(curUserTask.getPractiseFirstPassRank() + 1);

                                rank3++;
                            }
                        }

                        // 更新他人成绩
//                        DecimalFormat df = new DecimalFormat("#.00");
                        double score = 10 * (max3 == 1 ? 1 : (double) (max3 - userTask2.getPractiseFirstPassRank()) / (max3 - 1));
                        score = Double.parseDouble(df.format(score)); // 保留2位小数

                        userTask2.setPractiseScoreFirstPass(score);
//                        userTask2.setPractiseScoreFirstPass(score == 0 ? -1 : score); // 数据库mapper要置为0时需要改成-1
                        userTask2.setPractiseScore(Double.parseDouble(df.format(70 + curUserTask.getPractiseScoreAttempt() + curUserTask.getPractiseScoreLength() + score)));
                        userTasks.get(i).setPractiseScoreFirstPass(score);
                        userTaskMapper.updateUserTask(userTask2);

                    }
                }
                UserTask userTask5 = new UserTask();
                userTask5.setId(userTask.getId());

                userTask5.setPractiseFirstPassRank(orgRank3 - rank3);

                // 更新自己的最短用时成绩
                double score3 = 10;
                if (max3 > 1) {
                    score3 = 10 * (double) (max3 - userTask5.getPractiseFirstPassRank()) / (max3 - 1);
//                    DecimalFormat df = new DecimalFormat("#.00");
                    score3 = Double.parseDouble(df.format(score3));
                }
                userTask5.setPractiseScoreFirstPass(score3);
//                userTask5.setPractiseScoreFirstPass(score3 == 0 ? -1 : score3);

                userTask5.setPractiseScore(Double.parseDouble(df.format(70 + userTask.getPractiseScoreAttempt() + userTask.getPractiseScoreLength() + score3)));
                userTask.setPractiseScoreFirstPass(score3);
                userTaskMapper.updateUserTask(userTask5);


                Push push = new Push();
                push.setBullet(user.getRealname() + "完成练习了#" + task.getTaskSn());
                webSocketServerService.sendAllMessage(MAIN, push.toString());
            } else {
                Push push = new Push();
                push.setBullet(user.getRealname() + "练习了#" + task.getTaskSn());
                webSocketServerService.sendAllMessage(MAIN, push.toString());
            }
        } else if (user.getUserDevice().getKeyCode().equals(res[0])) { // 挑战模式

            s = res[1];
            UserTask userTask1 = new UserTask();
            userTask1.setId(userTask.getId());
            userTask1.setAttempt(userTask.getAttempt() + 1);
            if (OK.equals(s)) {
                userTask1.setTaskStatus(Task.TASK_FINISH);
                userTask1.setPassAt(new Timestamp(System.currentTimeMillis()));
                userTask1.setLength(userTask1.getPassAt().getTime() - userTask.getStartAt().getTime());

            }
            userTaskMapper.updateUserTask(userTask1);

            if (OK.equals(s)) {
                // 更新尝试次数排名，次数越小排名越前
                /*
                值  排名
                20  4
                10  5
                40  1
                30  2
                25  3
                (25->26 或25->35 或25->45 再考虑新增的，新增的就是排名最后的)
                 */
                List<UserTask> userTasks = userTaskMapper.queryUserTaskByClassId(user.getStuClass());

                int orgRank = userTask.getAttemptRank();
                int rank = 0;
                int max = 0;
                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getAttemptRank() > 0) {
                        // 保存最大的排名
                        if (max < curUserTask.getAttemptRank()) {
                            max = curUserTask.getAttemptRank();
                        }
                    }
                }
                // 如果为0就是新增的，总数自增1再赋值
                if (orgRank == 0) {
                    orgRank = ++max;
                }

                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);

                    // 判断同一个任务内，并且有排名的
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getAttemptRank() > 0) {

                        UserTask userTask2 = new UserTask();
                        userTask2.setId(curUserTask.getId());
                        userTask2.setAttemptRank(curUserTask.getAttemptRank());
                        // 查出排名靠前的值
                        if (curUserTask.getAttemptRank() < orgRank) {
                            // 靠前的没有当前用户的值小，给他们排名加 1
                            if (curUserTask.getAttempt() > userTask1.getAttempt()) {
                                userTask2.setAttemptRank(curUserTask.getAttemptRank() + 1);

                                rank++;
                            }
                        }

                        // 更新他人成绩
//                        DecimalFormat df = new DecimalFormat("#.00");
                        double score = 10 * (max == 1 ? 1 : (double) (max - userTask2.getAttemptRank()) / (max - 1));
                        score = Double.parseDouble(df.format(score)); // 保留2位小数

                        userTask2.setTaskScoreAttempt(score);
//                        userTask2.setTaskScoreAttempt(score == 0 ? -1 : score);  // 数据库mapper要置为0时需要改成-1
                        double taskScore = 70 + score + curUserTask.getTaskScoreLength() + curUserTask.getTaskScorePass();
                        taskScore = Double.parseDouble(df.format(taskScore));
                        userTask2.setTaskScore(taskScore);
                        userTasks.get(i).setTaskScoreAttempt(score);
                        userTaskMapper.updateUserTask(userTask2);
                    }

                }
                UserTask userTask3 = new UserTask();
                userTask3.setId(userTask.getId());

                userTask3.setAttemptRank(orgRank - rank);

                // 更新自己的尝试次数成绩
                double score1 = 10;

                if (max > 1) {
                    score1 = 10 * (double) (max - userTask3.getAttemptRank()) / (max - 1);

                    score1 = Double.parseDouble(df.format(score1));
                }
                userTask3.setTaskScoreAttempt(score1);
//                userTask3.setTaskScoreAttempt(score1 == 0 ? -1 : score1);
                double taskScore = 70 + score1 + userTask.getTaskScoreLength() + userTask.getTaskScorePass();
                taskScore = Double.parseDouble(df.format(taskScore));

                userTask3.setTaskScore(taskScore);
                userTask.setTaskScoreAttempt(score1);
                userTaskMapper.updateUserTask(userTask3);

                // 更新用时排名，越小排名越前
            /*
            值  排名
            20  2
            10  1
            40  5
            30  4
            25  3
            (25->26 或25->35 或25->45 再考虑新增的，新增的就是排名最后的)
             */
                int orgRank2 = userTask.getLengthRank();
                int rank2 = 0;
                int max2 = 0;
                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getLengthRank() > 0) {
                        // 保存最大的排名
                        if (max2 < curUserTask.getLengthRank()) {
                            max2 = curUserTask.getLengthRank();
                        }
                    }
                }
                if (orgRank2 == 0) {
                    orgRank2 = ++max2;
                }

                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);

                    // 判断同一个任务内，并且有排名的
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getLengthRank() > 0) {

                        UserTask userTask2 = new UserTask();
                        userTask2.setId(curUserTask.getId());
                        userTask2.setLengthRank(curUserTask.getLengthRank());
                        // 查出排名靠前的值
                        if (curUserTask.getLengthRank() < orgRank2) {
                            // 靠前的没有当前用户的值小，给他们排名加 1
                            if (curUserTask.getLength() > userTask1.getLength()) {
                                userTask2.setLengthRank(curUserTask.getLengthRank() + 1);

                                rank2++;
                            }
                        }

                        // 更新他人成绩
//                        DecimalFormat df = new DecimalFormat("#.00");
                        double score = 10 * (max2 == 1 ? 1 : (double) (max2 - userTask2.getLengthRank()) / (max2 - 1));
                        score = Double.parseDouble(df.format(score)); // 保留2位小数

                        userTask2.setTaskScoreLength(score);
//                        userTask2.setTaskScoreLength(score == 0 ? -1 : score); // 数据库mapper要置为0时需要改成-1
                        double taskScore2 = 70 + curUserTask.getTaskScoreAttempt() + score + curUserTask.getTaskScorePass();
                        taskScore2 = Double.parseDouble(df.format(taskScore2));

                        userTask2.setTaskScore(taskScore2);
                        userTasks.get(i).setTaskScoreLength(score);
                        userTaskMapper.updateUserTask(userTask2);

                    }
                }
                UserTask userTask4 = new UserTask();
                userTask4.setId(userTask.getId());

                userTask4.setLengthRank(orgRank2 - rank2);

                // 更新自己的最短用时成绩
                double score2 = 10;
                if (max2 > 1) {
                    score2 = 10 * (double) (max2 - userTask4.getLengthRank()) / (max2 - 1);
//                    DecimalFormat df = new DecimalFormat("#.00");
                    score2 = Double.parseDouble(df.format(score2));
                }
                userTask4.setTaskScoreLength(score2);
//                userTask4.setTaskScoreLength(score2 == 0 ? -1 : score2);
                double taskScore2 = 70 + userTask.getTaskScoreAttempt() + score2 + userTask.getTaskScorePass();
                taskScore2 = Double.parseDouble(df.format(taskScore2));

                userTask4.setTaskScore(taskScore2);
                userTask.setTaskScoreLength(score2);
                userTaskMapper.updateUserTask(userTask4);

                // 更新完成时间排名，越早的排名越前
            /*
            值  排名
            20  2
            10  1
            40  5
            30  4
            25  3
            (25->26 或25->35 或25->45 再考虑新增的，新增的就是排名最后的)
             */
                int orgRank3 = userTask.getPassRank();
                int rank3 = 0;
                int max3 = 0;
                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getPassRank() > 0) {
                        // 保存最大的排名
                        if (max3 < curUserTask.getPassRank()) {
                            max3 = curUserTask.getPassRank();
                        }
                    }
                }
                if (orgRank3 == 0) {
                    orgRank3 = ++max3;
                }

                for (int i = 0; i < userTasks.size(); i++) {
                    UserTask curUserTask = userTasks.get(i);

                    // 判断同一个任务内，并且有排名的
                    if (curUserTask.getTaskId() == userTask.getTaskId() && curUserTask.getPassRank() > 0) {

                        UserTask userTask2 = new UserTask();
                        userTask2.setId(curUserTask.getId());
                        userTask2.setPassRank(curUserTask.getPassRank());
                        // 查出排名靠前的值
                        if (curUserTask.getPassRank() < orgRank3) {
                            // 靠前的没有当前用户的值小，给他们排名加 1
                            if (curUserTask.getPassAt().getTime() > userTask1.getPassAt().getTime()) {
                                userTask2.setPassRank(curUserTask.getPassRank() + 1);

                                rank3++;
                            }
                        }

                        // 更新他人成绩
//                        DecimalFormat df = new DecimalFormat("#.00");
                        double score = 10 * (max3 == 1 ? 1 : (double) (max3 - userTask2.getPassRank()) / (max3 - 1));
                        score = Double.parseDouble(df.format(score)); // 保留2位小数

                        userTask2.setTaskScorePass(score);
//                        userTask2.setTaskScorePass(score == 0 ? -1 : score); // 数据库mapper要置为0时需要改成-1
                        double taskScore3 = 70 + curUserTask.getTaskScoreAttempt() + curUserTask.getTaskScoreLength() + score;
                        taskScore3 = Double.parseDouble(df.format(taskScore3));

                        userTask2.setTaskScore(taskScore3);
                        userTasks.get(i).setTaskScorePass(score);
                        userTaskMapper.updateUserTask(userTask2);

                    }
                }
                UserTask userTask5 = new UserTask();
                userTask5.setId(userTask.getId());

                userTask5.setPassRank(orgRank3 - rank3);

                // 更新自己的最短用时成绩
                double score3 = 10;
                if (max3 > 1) {
                    score3 = 10 * (double) (max3 - userTask5.getPassRank()) / (max3 - 1);
//                    DecimalFormat df = new DecimalFormat("#.00");
                    score3 = Double.parseDouble(df.format(score3));
                }
                userTask5.setTaskScorePass(score3);
//                userTask5.setTaskScorePass(score3 == 0 ? -1 : score3);
                double taskScore3 = 70 + userTask.getTaskScoreAttempt() + userTask.getTaskScoreLength() + score3;
                taskScore3 = Double.parseDouble(df.format(taskScore3));

                userTask5.setTaskScore(taskScore3);
                userTask.setTaskScorePass(score3);
                userTaskMapper.updateUserTask(userTask5);

                Rank rank1 = new Rank();
                rank1.setId(user.getId());
                rank1.setRealname(user.getRealname());

                int mapTotal = Integer.parseInt(env.getProperty("custom.map-total"));
                Double score = userTask5.getTaskScore() * mapTotal / (taskMapper.queryTaskList().size() * 100);
                rank1.setScore(score);

                List<Rank> person = new ArrayList<>();
                person.add(rank1);

                Push push = new Push();
                push.setPerson(person);
                push.setBullet(user.getRealname() + "成功挑战了#" + task.getTaskSn());
                webSocketServerService.sendAllMessage(MAIN, push.toString());
            } else {
                Push push = new Push();
                push.setBullet(user.getRealname() + "挑战了#" + task.getTaskSn());
                webSocketServerService.sendAllMessage(MAIN, push.toString());
            }
        }

        if (!ERROR.equals(s)) {
            // 记录user_task_record
            UserTaskRecord userTaskRecord = new UserTaskRecord();
            userTaskRecord.setUserId(userTask.getUserId());
            userTaskRecord.setUserTaskId(id);
            userTaskRecord.setMode(mode);
            userTaskRecord.setTaskStatus(OK.equals(s) ? TASK_FINISH : CHECK_FAIL);
            userTaskRecord.setKeyCode(res[0]);
            userTaskRecordMapper.addUserTaskRecord(userTaskRecord);
        }

        Push push = new Push();
        push.setCheckTask(new Push().new Task(mode + "", id + "", s, uuid));
        webSocketServerService.sendOneMessage(MAIN, user.getStuNo(), push.toString());
    }

    @Override
    public String getKeyCode(int id, String uuid) {

        User user = studentMapper.queryStudentById(id);
        String command = env.getProperty("custom.check-duuid");
        log.error("发送命令");
        log.error(command);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", GETKEYCODE);
        jsonObject.put("uuid", uuid);
        jsonObject.put("mode", CHALLENGE_MODE);
        jsonObject.put("id", id);
        jsonObject.put("password", user.getUserDevice().getConnPass());
        jsonObject.put("command", command);

        return webSocketServerService.sendOneMessage(AGENT, user.getStuNo(), jsonObject.toJSONString());
    }

    @Override
    public void getKeyCodeCallback(JSONObject jsonObject) {

        int id = (int) jsonObject.get("id");
        String uuid = (String) jsonObject.get("uuid");
        String re = (String) jsonObject.get("re");
        re = re.replaceAll("\n", "");

        User user = studentMapper.queryStudentById(id);

        Push push = new Push();
        push.setKeyCode(new Push().new KeyCode(re, id + "", uuid));
        webSocketServerService.sendOneMessage(MAIN, user.getStuNo(), push.toString());
    }

    @Override
    public void sortRankAndReCalcScoreByClassId(int id) {

        // 要排序的字段
        String[] challengeOrderBys = {"attempt_rank", "length_rank", "pass_rank"};
        String[] practiseOrderBys = {"practise_attempt_rank", "practise_min_length_rank", "practise_first_pass_rank"};

        // 保存每个任务的total
        Map<Integer, Integer> challengeMap = new HashMap<>();
        Map<Integer, Integer> practiseMap = new HashMap<>();

        DecimalFormat df = new DecimalFormat("#.00");

        List<UserTask> userTasks1 = userTaskMapper.queryUserTaskByClassId(id);
        for (int i = 0; i < userTasks1.size(); i++) {
            UserTask userTask = userTasks1.get(i);
            int taskId = userTask.getTaskId();

            if (userTask.getAttemptRank() > 0 || userTask.getLengthRank() > 0 || userTask.getPassRank() > 0) {
                int total = challengeMap.containsKey(taskId) ? challengeMap.get(taskId) : 0;
                challengeMap.put(taskId, total + 1);
            }
            if (userTask.getPractiseAttemptRank() > 0 || userTask.getPractiseMinLengthRank() > 0 || userTask.getPractiseFirstPassRank() > 0) {
                int total = practiseMap.containsKey(taskId) ? practiseMap.get(taskId) : 0;
                practiseMap.put(taskId, total + 1);
            }
        }

        // 重新计算挑战成绩
        Iterator<Map.Entry<Integer, Integer>> challengeIterator = challengeMap.entrySet().iterator();
        // 遍历任务total
        while (challengeIterator.hasNext()) {
            Map.Entry entry = challengeIterator.next();
            int taskId = (int) entry.getKey();
            int total = (int) entry.getValue();

            // 遍历挑战的排序字段
            for (int j = 0; j < challengeOrderBys.length; j++) {
                String orderBy = challengeOrderBys[j];
                List<UserTask> userTasks = userTaskMapper.queryUserTaskByClassIdAndOrderBy(id, orderBy);
                int skipZero = 0;

                // 遍历userTasks
                for (int k = 0; k < userTasks.size(); k++) {
                    UserTask userTask = userTasks.get(k);

                    // 筛选taskId
                    if (userTask.getTaskId() == taskId && (userTask.getAttemptRank() > 0 || userTask.getLengthRank() > 0 || userTask.getPassRank() > 0)) {
                        double score = calcScore(total, k - skipZero + 1);

                        UserTask userTask1 = new UserTask();
                        userTask1.setId(userTask.getId());
                        if ("attempt_rank".equals(orderBy)) {
                            userTask1.setAttemptRank(k - skipZero + 1);

                            // 计算成绩
                            userTask1.setTaskScoreAttempt(score);
//                            userTask1.setTaskScoreAttempt(score == 0 ? -1 : score);  // 数据库mapper要置为0时需要改成-1
                            double taskScore = 70 + score + userTask.getTaskScoreLength() + userTask.getTaskScorePass();
                            taskScore = Double.parseDouble(df.format(taskScore));

                            userTask1.setTaskScore(taskScore);
                        }
                        if ("length_rank".equals(orderBy)) {
                            userTask1.setLengthRank(k - skipZero + 1);

                            // 计算成绩
                            userTask1.setTaskScoreLength(score);
//                            userTask1.setTaskScoreLength(score == 0 ? -1 : score);  // 数据库mapper要置为0时需要改成-1
                            double taskScore = 70 + userTask.getTaskScoreAttempt() + score + userTask.getTaskScorePass();
                            taskScore = Double.parseDouble(df.format(taskScore));

                            userTask1.setTaskScore(taskScore);
                        }
                        if ("pass_rank".equals(orderBy)) {
                            userTask1.setPassRank(k - skipZero + 1);

                            // 计算成绩
                            userTask1.setTaskScorePass(score);
//                            userTask1.setTaskScorePass(score == 0 ? -1 : score);  // 数据库mapper要置为0时需要改成-1
                            double taskScore = 70 + userTask.getTaskScoreAttempt() + userTask.getTaskScoreLength() + score;
                            taskScore = Double.parseDouble(df.format(taskScore));

                            userTask1.setTaskScore(taskScore);
                        }
                        userTaskMapper.updateUserTask(userTask1);
                    } else {
                        skipZero++;
                    }
                }
            }

        }


        // 重新计算练习成绩
        Iterator<Map.Entry<Integer, Integer>> practiseIterator = practiseMap.entrySet().iterator();
        while (practiseIterator.hasNext()) {
            Map.Entry entry = practiseIterator.next();
            int taskId = (int) entry.getKey();
            int total = (int) entry.getValue();

            for (int j = 0; j < practiseOrderBys.length; j++) {
                String orderBy = practiseOrderBys[j];
                List<UserTask> userTasks = userTaskMapper.queryUserTaskByClassIdAndOrderBy(id, orderBy);
                int skipZero = 0;

                for (int k = 0; k < userTasks.size(); k++) {
                    UserTask userTask = userTasks.get(k);

                    if (userTask.getTaskId() == taskId && (userTask.getPractiseAttemptRank() > 0 || userTask.getPractiseMinLengthRank() > 0 || userTask.getPractiseFirstPassRank() > 0)) {
                        double score = calcScore(total, k - skipZero + 1);

                        UserTask userTask1 = new UserTask();
                        userTask1.setId(userTask.getId());

                        if ("practise_attempt_rank".equals(orderBy)) {
                            userTask1.setPractiseAttemptRank(k - skipZero + 1);

                            // 计算成绩
                            userTask1.setPractiseScoreAttempt(score);
//                            userTask1.setPractiseScoreAttempt(score == 0 ? -1 : score);  // 数据库mapper要置为0时需要改成-1
                            userTask1.setPractiseScore(Double.parseDouble(df.format(70 + score + userTask.getPractiseScoreLength() + userTask.getPractiseScoreFirstPass())));
                        }
                        if ("practise_min_length_rank".equals(orderBy)) {
                            userTask1.setPractiseMinLengthRank(k - skipZero + 1);

                            // 计算成绩
                            userTask1.setPractiseScoreLength(score);
//                            userTask1.setPractiseScoreLength(score == 0 ? -1 : score);  // 数据库mapper要置为0时需要改成-1
                            userTask1.setPractiseScore(Double.parseDouble(df.format(70 + userTask.getPractiseScoreAttempt() + score + userTask.getPractiseScoreFirstPass())));
                        }
                        if ("practise_first_pass_rank".equals(orderBy)) {
                            userTask1.setPractiseFirstPassRank(k - skipZero + 1);

                            // 计算成绩
                            userTask1.setPractiseScoreFirstPass(score);
//                            userTask1.setPractiseScoreFirstPass(score == 0 ? -1 : score);  // 数据库mapper要置为0时需要改成-1
                            userTask1.setPractiseScore(Double.parseDouble(df.format(70 + userTask.getPractiseScoreAttempt() + userTask.getPractiseScoreLength() + score)));

                        }

                        userTaskMapper.updateUserTask(userTask1);
                    } else {
                        skipZero++;
                    }
                }
            }

        }
    }

    @Override
    public List<UserTaskOpen> queryUserTaskOpenListByUserIdAndClassId(int userId, int id) {
        return userTaskMapper.queryUserTaskOpenListByUserIdAndClassId(userId, id);
    }

    @Override
    public int addUserTaskOpen(List<UserTaskOpen> userTaskOpen) {
        return userTaskMapper.addUserTaskOpen(userTaskOpen);
    }

    @Override
    public int deleteUserTaskOpen(List<UserTaskOpen> userTaskOpen) {
        return userTaskMapper.deleteUserTaskOpen(userTaskOpen);
    }

    // 重新计算成绩
    private double calcScore(int total, int rank) {
        DecimalFormat df = new DecimalFormat("#.00");
        double score = 10 * (total == 1 ? 1 : (double) (total - rank) / (total - 1));
        score = Double.parseDouble(df.format(score)); // 保留2位小数
        return score;
    }
}
