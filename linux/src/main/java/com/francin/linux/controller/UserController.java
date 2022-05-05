package com.francin.linux.controller;

import com.alibaba.excel.EasyExcel;
import com.francin.linux.annotation.UserLoginToken;
import com.francin.linux.common.Result;
import com.francin.linux.common.TokenService;
import com.francin.linux.dao.StudentDao;
import com.francin.linux.listener.StudentListener;
import com.francin.linux.mapper.ClassesMapper;
import com.francin.linux.pojo.Classes;
import com.francin.linux.pojo.User;
import com.francin.linux.pojo.UserDevice;
import com.francin.linux.service.StudentService;
import com.francin.linux.service.UserService;
import com.francin.linux.service.WebSocketServerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        log.error(user.toString());
        User re = userService.login(user);
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());

        if (re == null) {
            return Result.make(Result.FAIL, "帐号有误", "", "");
        } else if (!re.getPassword().equals(md5Password)) {
            return Result.make(Result.FAIL, "密码有误", "", "");
        } else {
            User re1 = new User();
            re1.setId(re.getId());
            re1.setRealname(re.getRealname());
            re1.setToken(tokenService.getToken(re));
            return Result.make(Result.SUCCESS, "登录成功", "data", re1);
        }
    }

    @GetMapping("/home/{id}")
    public String home(@PathVariable int id){
        return userService.home(id);
    }

    @PostMapping("/chgPsw")
    public Map<String, Object> chgPsw(@RequestBody User user){
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Password);
        return Result.make(Result.SUCCESS, "修改成功", "data", userService.chgPsw(user));
    }
}
