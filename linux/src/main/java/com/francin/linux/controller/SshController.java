package com.francin.linux.controller;

import com.francin.linux.common.Result;
import com.francin.linux.common.Utils;
import com.francin.linux.pojo.Task;
import com.francin.linux.service.SshService;
import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/ssh")
public class SshController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshController.class.getName());

//    @Autowired
//    private TaskResourceMapper taskResourceMapper;
    @Autowired
    private SshService sshService;
    @Autowired
    private Environment env;
    @Autowired
    private Utils utils;
//
//    @GetMapping("/checkTask")
//    public String checkTask(int id) {
//        UserTask userTask = userTaskMapper.queryUserTaskById(id);
//        Task task = userTask.getTask();
//        task.setTaskDesc(utils.replace(task.getTaskDesc(), userTask.getRand()));
//        task.setCommand(utils.replace(task.getCommand(), userTask.getRand()));
//        userTask.setTask(task);
//        return exec(task);
//    }

//    @GetMapping("/prepare")
//    public String prepare(int id) {
//        List<TaskResource> taskResources = taskResourceMapper.queryTaskResourceByTaskId(id);
//        String text = "";
//        for (int i = 0; i < taskResources.size(); i++) {
//            text = taskResources.get(i).getResource();
//        }
//        LOGGER.info(text);
//        Task task = new Task();
//        task.setErrorMessage("");
//
//        task.setCommand("echo -e \"" + text + "\" > /root/text && sed -i 's/\\r//' /root/text");
//        return exec(task);
//    }

    @GetMapping("/checkConn")
    public Map<String, Object> checkConn(
            String ip,
            int port,
            String username,
            String password
    ) {
        LOGGER.error(env.getProperty("custom.check-duuid"));
        try {
            String re = sshService.exec(ip, port, username, password, env.getProperty("custom.check-duuid"));
            re = re.replaceAll("\n", "");

            return Result.make(Result.SUCCESS, "接连成功", "keyCode", re);

        } catch (JSchException e) {
            if ("timeout: socket is not established".equals(e.getMessage())) {
                return Result.make(Result.FAIL, "连接失败，请检查网络", "", "");
            } else if ("Auth fail".equals(e.getMessage())) {
                return Result.make(Result.FAIL, "连接失败，请检查密码是否正确", "", "");
            } else {
                return Result.make(Result.FAIL, "连接失败，请检查配置", "", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.make(Result.FAIL, "连接失败，请检查配置", "", "");
        }
    }

    public String exec(Task task) {
        String result = ssh("192.168.122.88", 22, "root", "123456", task.getCommand());
        String[] messages = task.getErrorMessage().split(";");
        String[] rows = result.split("\n");
        for (int i = 0; i < rows.length; i++) {
            LOGGER.info("===========");
            LOGGER.info(rows[i]);
            if ("0".equals(rows[i])) {
                return messages[i];
            }
        }

        return "检查正确";
    }


    public String ssh(String host, int port, String username, String password, String command) {
        try {
            return sshService.exec(host, port, username, password, command);
        } catch (JSchException e) {
            if ("timeout: socket is not established".equals(e.getMessage())) {
                return "连接失败，请检查网络";
            } else if ("Auth fail".equals(e.getMessage())) {
                return "连接失败，请检查密码是否正确";
            } else {
                return "连接失败，请检查配置";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "连接失败，请检查配置";
        }

    }
}
