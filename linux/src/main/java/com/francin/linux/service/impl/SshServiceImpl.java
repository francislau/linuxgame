package com.francin.linux.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.francin.linux.common.Utils;
import com.francin.linux.mapper.StudentMapper;
import com.francin.linux.mapper.TaskMapper;
import com.francin.linux.mapper.UserTaskMapper;
import com.francin.linux.pojo.Task;
import com.francin.linux.pojo.User;
import com.francin.linux.pojo.UserTask;
import com.francin.linux.service.SshService;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;

import static com.francin.linux.pojo.UserTask.CHALLENGE_MODE;
import static com.francin.linux.pojo.UserTask.PRACTISE_MODE;

@Service
public class SshServiceImpl implements SshService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshServiceImpl.class.getName());

    public static final String ERROR = "-1";
    public static final String OK = "1";

    @Autowired
    StudentMapper studentMapper;
    @Autowired
    UserTaskMapper userTaskMapper;
    @Autowired
    TaskMapper taskMapper;
    @Autowired
    private Utils utils;

    @Override
    public String exec(String ip, int port, String username, String password, String command) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, ip, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");

        session.setTimeout(6000);

        session.connect();
        //建立连接结束
        //发送指令
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        InputStream in = exec.getInputStream();
        exec.setCommand(command);
        exec.connect();
        String s = IOUtils.toString(in, "UTF-8");
        in.close();
        return s;
    }

}
