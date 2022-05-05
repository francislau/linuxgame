package com.francin.linux.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classes {

  private int id;
  private String name;
  private int userId;
  private int status;
  private Timestamp createAt;
  private Timestamp updateAt;
  private int createBy;
  private int updateBy;

}
