package com.francin.linux.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskRecord {

  private int id;
  private int userId;
  private int userTaskId;
  private int mode;
  private int taskStatus;
  private String keyCode;
  private int status;
  private Timestamp createAt;
  private Timestamp updateAt;
  private long createBy;
  private long updateBy;

  private String realname;
  private String taskSn;

  private User user;
  private UserTask userTask;
  private Task task;

}