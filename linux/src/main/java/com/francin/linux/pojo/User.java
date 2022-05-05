package com.francin.linux.pojo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private int id;
  private String username;
  private String password;
  private int role;
  @ExcelProperty("学号")
  private String stuNo;
  @ExcelProperty("姓名")
  private String realname;
  private int stuClass;
  private int defaultMode = -1;
  private String teacherNo;
  private int status;
  private Timestamp createAt;
  private Timestamp updateAt;
  private int createBy;
  private int updateBy;

  private String token;

  private UserDevice userDevice;
}
