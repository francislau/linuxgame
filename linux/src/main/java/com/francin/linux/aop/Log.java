package com.francin.linux.aop;

import com.francin.linux.mapper.UserTaskMapper;
import com.francin.linux.mapper.UserTaskRecordMapper;
import com.francin.linux.pojo.UserTask;
import com.francin.linux.pojo.UserTaskRecord;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Log {
    private static final Logger LOGGER = LoggerFactory.getLogger(Log.class.getName());

    @Autowired
    UserTaskMapper userTaskMapper;
    @Autowired
    UserTaskRecordMapper userTaskRecordMapper;

//    @AfterReturning(pointcut = "execution(* com.francin.linux.service.SshService.checkTask(..))", returning = "obj")
//    public void afterReturning(JoinPoint joinPoint, Object obj){
//        LOGGER.error("======after======"+obj);
//        int userTaskId = (int) joinPoint.getArgs()[0];
//        UserTask userTask = userTaskMapper.queryUserTaskById(userTaskId);
//
//        UserTaskRecord userTaskRecord = new UserTaskRecord();
//        userTaskRecord.setUserId(userTask.getUserId());
//        userTaskRecord.setUserTaskId(userTaskId);
//        userTaskRecord.setTaskStatus("1".equals(obj)?3:2);
//        userTaskRecordMapper.addUserTaskRecord(userTaskRecord   );
//    }
}
