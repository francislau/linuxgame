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
@RequestMapping("/student")
public class StudentController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private ClassesMapper classesMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private UserService userService;
    @Autowired
    private WebSocketServerService webSocketServerService;
    @Autowired
    private StudentDao studentDao;

    @GetMapping("/queryList")
    public Map<String, Object> queryStudentList() {
        return Result.make(Result.SUCCESS, "查询成功", "data", studentService.queryStudentList());
    }

@GetMapping("/init")
    public Map<String, Object> init() {
return Result.make(Result.SUCCESS, "查询成功", "data", studentService.init());
    }

    @UserLoginToken
    @GetMapping("/init/{id}")
    public Map<String, Object> initById(@PathVariable int id) {
        return Result.make(Result.SUCCESS, "查询成功", "data", studentService.initById(id));
    }

    @UserLoginToken
    @GetMapping("/queryListByClassId")
    public Map<String, Object> queryStudentListByClassId(@RequestParam("id") int id) {
        return Result.make(Result.SUCCESS, "查询成功", "data", studentService.queryStudentListByClassId(id));
    }

    @PostMapping("/update")
    public Map<String, Object> updateStudent(@RequestBody User user) {
        LOGGER.error(user.toString());
        User u1 = new User();
        u1.setId(1);
        UserDevice u2 = new UserDevice();
        u2.setConnPass("999");
        u1.setUserDevice(u2);
        return Result.make(Result.SUCCESS, "修改成功", "data", studentService.updateStudent(user));
    }

    @PostMapping("/updateVerify")
    public Map<String, Object> updateVerify(@RequestBody User user) {
        LOGGER.error(user.toString());
        int re = studentService.updateVerify(user);
        if (re == -1) {
            return Result.make(Result.FAIL, "提交失败，连接的设备有可能已经被其他同学提交过，请检查配置，或换一台设备", "", "");
        } else if (re == 0) {
            return Result.make(Result.SUCCESS, "修改成功", "", "");
        } else {
            return Result.make(Result.SUCCESS, "提交成功，请等待老师审核 ^_^", "", "");
        }
    }

    @GetMapping("/verify/{ids}")
    public Map<String, Object> verify(@PathVariable int[] ids) {
        return Result.make(Result.SUCCESS, "审核成功", "data", studentService.verify(ids));
    }

    @GetMapping("/clear/{id}")
    public Map<String, Object> clear(@PathVariable int id) {
        return Result.make(Result.SUCCESS, "清空成功", "data", studentService.clear(id));
    }

    @PostMapping("/add")
    public Map<String, Object> addStudent(@RequestBody User user) {
        LOGGER.error(user.toString());
        studentService.addStudent(user);
        return Result.make(Result.SUCCESS, "添加成功", "data", user.getId());
    }

    @GetMapping("/delete")
    public Map<String, Object> deleteStudent(@RequestParam("id") int id) {
        return Result.make(Result.SUCCESS, "删除成功", "data", studentService.deleteStudent(id));
    }

    @GetMapping("/batchDelete/{ids}")
    public Map<String, Object> batchDeleteStudent(@PathVariable String[] ids) {
        return Result.make(Result.SUCCESS, "删除成功", "data", studentService.batchDeleteStudent(ids));
    }

    @PostMapping("/login")
    public Map<String, Object> loginStudent(@RequestBody User user) {
        LOGGER.error(user.toString());
        User re = studentService.login(user);
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());

        if (re == null) {
            return Result.make(Result.FAIL, "学号有误", "", "");
        } else if (!re.getPassword().equals(md5Password)) {
            return Result.make(Result.FAIL, "密码有误", "", "");
        } else {
            User re1 = new User();
            re1.setId(re.getId());
            re1.setRealname(re.getRealname());
            re1.setStuNo(re.getStuNo());
            re1.setToken(tokenService.getToken(re));
            return Result.make(Result.SUCCESS, "登录成功", "data", re1);
        }
    }

    @GetMapping("/query/{id}")
    public Map<String, Object> queryStudent(@PathVariable int id) {
        return Result.make(Result.SUCCESS, "查询成功", "data", studentService.queryStudentById(id));
    }

    /**
     * 读取 excel
     *
     * @return
     */
    @PostMapping("import/{id}")
    @ResponseBody
    public String upload(@PathVariable int id, MultipartFile file) throws IOException {
        LOGGER.error(file.toString());
        studentDao.stuClass = id;
        //写法一
        // sheet里面可以传参 根据sheet下标读取或者根据名字读取 不传默认读取第一个
        EasyExcel.read(file.getInputStream(), User.class, new StudentListener(studentDao)).sheet().doRead();
        // 写法2：
        /*ExcelReader excelReader = EasyExcel.read(file.getInputStream(), Student.class, new StudentListener(studentDao)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
        excelReader.finish();*/
        return "success";
    }

    @GetMapping("export/{id}")
    public void download(@PathVariable int id, HttpServletResponse response) throws IOException {

        Classes classes = classesMapper.queryClassesById(id);
        List<User> users = studentService.queryStudentListByClassId(id);

        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(classes.getName(), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        //   User.class 是按导出类  data()应为数据库查询数据，这里只是模拟
        EasyExcel.write(response.getOutputStream(), User.class).sheet(classes.getName()).doWrite(users);
    }

    @GetMapping("/resetPsw/{id}")
    public Map<String, Object> resetPsw(@PathVariable int id){
        String md5Password = DigestUtils.md5DigestAsHex("666666".getBytes());

        User user = new User();
        user.setId(id);
        user.setPassword(md5Password);
        return Result.make(Result.SUCCESS, "修改成功", "data", userService.chgPsw(user));
    }

    @PostMapping("/chgPsw")
    public Map<String, Object> chgPsw(@RequestBody User user){
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());

        User user1 = new User();
        user1.setId(user.getId());
        user1.setPassword(md5Password);
        return Result.make(Result.SUCCESS, "修改成功", "data", userService.chgPsw(user1));
    }

    @UserLoginToken
    @GetMapping("/test")
    public String test(){
        return "11";
    }
}
