package com.francin.linux.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskOpen {

    private int id;
    private int userId;
    private int classId;
    private int taskId;
    private int taskOrder;
    private int status;
    private Timestamp createAt;
    private Timestamp updateAt;
    private int createBy;
    private int updateBy;

}