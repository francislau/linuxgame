package com.francin.linux.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.francin.linux.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GloablExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        e.printStackTrace();

        String msg = e.getMessage();
        if (msg == null || msg.equals("")) {
            msg = "服务器出错";
        }

        return Result.make(Result.ERROR, msg, "", "");
    }
}